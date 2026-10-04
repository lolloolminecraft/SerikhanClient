package com.erzhan.lostcamera.client;

import com.erzhan.lostcamera.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.concurrent.ThreadLocalRandom;

public final class LostCameraState {
    private static float previousHealth = -1.0f;
    private static float crackLevel;
    private static float damageFlash;
    private static float damageJolt;
    private static float motionKick;
    private static boolean previousOnGround = true;
    private static int heartbeatCooldown;
    private static int threatTicks;
    private static boolean threatActive;
    private static int silenceTicks;
    private static int silenceGapTicks = 500;
    private static int respawnHeartTicks;
    private static boolean deathMarked;
    private static boolean respawnPending;
    private static int cameraNumber = 1;
    private static long sessionStart = System.currentTimeMillis();

    public static void tick(Minecraft minecraft) {
        if (minecraft.player == null || minecraft.level == null) {
            previousHealth = -1.0f;
            crackLevel = 0.0f;
            damageFlash = 0.0f;
            damageJolt = 0.0f;
            motionKick = 0.0f;
            threatTicks = 0;
            threatActive = false;
            CameraAudio.tick(minecraft, false, false);
            return;
        }

        Player player = minecraft.player;
        float health = player.getHealth();
        float hearts = health * 0.5f;

        if (health <= 0.0f || player.isDeadOrDying()) {
            markDeath();
        }

        if (respawnPending && health > 0.0f && !player.isDeadOrDying()) {
            finishRespawn(minecraft);
        }

        if (previousHealth < 0.0f) {
            previousHealth = health;
            previousOnGround = player.onGround();
        }

        if (!player.isDeadOrDying() && health + 0.01f < previousHealth) {
            damageFlash = 1.0f;
            damageJolt = 1.0f;
            float oldTarget = targetCrackLevel(previousHealth * 0.5f);
            float newTarget = targetCrackLevel(hearts);
            if (newTarget > oldTarget + 0.01f) {
                CameraAudio.playCrack(Mth.clamp(newTarget, 0.0f, 1.0f));
            }
        }

        if (ClientConfig.MOTION_TRIGGERS.get() && !player.isDeadOrDying()) {
            boolean onGround = player.onGround();
            double vy = player.getDeltaMovement().y;
            if (previousOnGround && !onGround && vy > 0.06) {
                motionKick = Math.max(motionKick, 1.0f);
            }
            previousOnGround = onGround;
        }

        float target = player.isDeadOrDying() ? crackLevel : targetCrackLevel(hearts);
        float speed = target > crackLevel ? 0.13f : 0.045f;
        crackLevel = Mth.lerp(speed, crackLevel, target);
        if (Math.abs(crackLevel - target) < 0.002f) {
            crackLevel = target;
        }

        damageFlash = Math.max(0.0f, damageFlash - 0.095f);
        damageJolt = Math.max(0.0f, damageJolt - 0.11f);
        motionKick = Math.max(0.0f, motionKick - 0.12f);

        if (!player.isDeadOrDying()) {
            updateThreat(minecraft);
            updateSilence();
            updateHeartbeat(hearts);
        } else {
            threatActive = false;
            threatTicks = 0;
        }

        boolean silent = silenceTicks > 0;
        CameraAudio.tick(minecraft, silent, threatActive);

        if (respawnHeartTicks > 0) {
            respawnHeartTicks--;
        }

        previousHealth = health;
    }

    private static void updateThreat(Minecraft minecraft) {
        if (!ClientConfig.THREAT_DETECTION.get() || minecraft.player == null || minecraft.level == null) {
            threatActive = false;
            threatTicks = 0;
            return;
        }

        if (minecraft.player.tickCount % 8 == 0) {
            double radius = ClientConfig.THREAT_RADIUS.get();
            boolean found = !minecraft.level.getEntities(
                    minecraft.player,
                    minecraft.player.getBoundingBox().inflate(radius),
                    LostCameraState::isThreatEntity
            ).isEmpty();

            if (found) {
                threatTicks = 30;
            }
        }

        boolean now = threatTicks > 0;
        if (now && !threatActive) {
            CameraAudio.playSignalDrop();
            heartbeatCooldown = 0;
        }
        threatActive = now;
        if (threatTicks > 0) {
            threatTicks--;
        }
    }

    private static boolean isThreatEntity(Entity entity) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || entity == minecraft.player || !entity.isAlive()) {
            return false;
        }
        if (entity.getType().getCategory() == MobCategory.MONSTER) {
            return true;
        }
        if (entity instanceof Mob mob && mob.getTarget() == minecraft.player) {
            return true;
        }

        var typeId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        String namespace = typeId == null ? "minecraft" : typeId.getNamespace();
        if ("minecraft".equals(namespace)) {
            return false;
        }
        if (!(entity instanceof Mob)) {
            return false;
        }
        return !(entity instanceof Animal)
                && !(entity instanceof WaterAnimal)
                && !(entity instanceof AbstractHorse)
                && !(entity instanceof AbstractVillager);
    }

    private static void updateSilence() {
        if (!ClientConfig.RANDOM_SILENCE.get()) {
            silenceTicks = 0;
            silenceGapTicks = 200;
            return;
        }

        if (silenceTicks > 0) {
            silenceTicks--;
            if (silenceTicks == 0) {
                silenceGapTicks = randomTicks(ClientConfig.SILENCE_GAP_MIN_SECONDS.get(), ClientConfig.SILENCE_GAP_MAX_SECONDS.get());
            }
            return;
        }

        silenceGapTicks--;
        if (silenceGapTicks <= 0) {
            silenceTicks = randomTicks(ClientConfig.SILENCE_MIN_SECONDS.get(), ClientConfig.SILENCE_MAX_SECONDS.get());
            CameraAudio.playSignalDrop();
        }
    }

    private static void updateHeartbeat(float hearts) {
        boolean lowHealth = ClientConfig.HEARTBEAT.get() && hearts <= ClientConfig.CRACK_START_HEARTS.get();
        boolean threatBeat = ClientConfig.THREAT_HEARTBEAT.get() && threatActive;
        if (!lowHealth && !threatBeat) {
            heartbeatCooldown = 0;
            return;
        }

        if (heartbeatCooldown > 0) {
            heartbeatCooldown--;
            return;
        }

        float start = ClientConfig.CRACK_START_HEARTS.get().floatValue();
        float danger = lowHealth ? Mth.clamp(1.0f - hearts / Math.max(0.5f, start), 0.0f, 1.0f) : 0.25f;
        if (threatBeat) {
            danger = Math.max(danger, 0.72f);
        }

        int interval = Math.max(7, Math.round(Mth.lerp(danger, 28.0f, 8.0f)));
        float volume = ClientConfig.HEARTBEAT_VOLUME.get().floatValue() * Mth.lerp(danger, 0.32f, 1.0f);
        float pitch = Mth.lerp(danger, 0.92f, 1.12f);
        CameraAudio.playHeartbeat(volume, pitch);
        heartbeatCooldown = interval;
    }

    private static int randomTicks(int minSeconds, int maxSeconds) {
        int min = Math.min(minSeconds, maxSeconds);
        int max = Math.max(minSeconds, maxSeconds);
        return ThreadLocalRandom.current().nextInt(min * 20, max * 20 + 1);
    }

    public static float targetCrackLevel(float hearts) {
        float start = ClientConfig.CRACK_START_HEARTS.get().floatValue();
        float heavy = Math.min(start - 0.1f, ClientConfig.HEAVY_CRACK_HEARTS.get().floatValue());
        float critical = Math.min(heavy - 0.1f, ClientConfig.CRITICAL_HEARTS.get().floatValue());

        if (hearts > start) {
            return 0.0f;
        }
        if (hearts > heavy) {
            float t = 1.0f - (hearts - heavy) / Math.max(0.1f, start - heavy);
            return Mth.lerp(t, 0.18f, 0.48f);
        }
        if (hearts > critical) {
            float t = 1.0f - (hearts - critical) / Math.max(0.1f, heavy - critical);
            return Mth.lerp(t, 0.55f, 0.78f);
        }
        float t = 1.0f - hearts / Math.max(0.5f, critical);
        return Mth.clamp(Mth.lerp(t, 0.82f, 1.0f), 0.82f, 1.0f);
    }

    public static void markDeath() {
        if (!deathMarked) {
            deathMarked = true;
            CameraAudio.playHeartBreak();
            CameraAudio.stopHum(Minecraft.getInstance());
        }
    }

    public static void requestRespawn() {
        if (respawnPending) {
            return;
        }
        respawnPending = true;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.player.respawn();
        }
    }

    private static void finishRespawn(Minecraft minecraft) {
        respawnPending = false;
        deathMarked = false;
        cameraNumber++;
        previousHealth = minecraft.player == null ? -1.0f : minecraft.player.getHealth();
        crackLevel = 0.0f;
        damageFlash = 0.0f;
        damageJolt = 0.0f;
        motionKick = 1.0f;
        threatActive = false;
        threatTicks = 0;
        silenceTicks = 0;
        silenceGapTicks = randomTicks(ClientConfig.SILENCE_GAP_MIN_SECONDS.get(), ClientConfig.SILENCE_GAP_MAX_SECONDS.get());
        heartbeatCooldown = 0;
        respawnHeartTicks = 22;
        sessionStart = System.currentTimeMillis();
        CameraAudio.playRevive();
        Screen screen = minecraft.screen;
        if (screen instanceof LostMediaDeathScreen) {
            minecraft.setScreen(null);
        }
    }

    public static float crackLevel() {
        return crackLevel;
    }

    public static float damageFlash() {
        return damageFlash;
    }

    public static float damageJolt() {
        return damageJolt;
    }

    public static float motionKick() {
        return motionKick;
    }

    public static boolean isThreatActive() {
        return threatActive;
    }

    public static boolean isSilent() {
        return silenceTicks > 0;
    }

    public static boolean isRespawnPending() {
        return respawnPending;
    }

    public static int cameraNumber() {
        return cameraNumber;
    }

    public static int respawnHeartTicks() {
        return respawnHeartTicks;
    }

    public static long sessionMillis() {
        return Math.max(0L, System.currentTimeMillis() - sessionStart);
    }

    private LostCameraState() {}
}
