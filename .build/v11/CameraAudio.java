package com.erzhan.lostcamera.client;

import com.erzhan.lostcamera.LostCameraMedia;
import com.erzhan.lostcamera.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public final class CameraAudio {
    private static final ResourceLocation HEARTBEAT = id("heartbeat");
    private static final ResourceLocation GLASS_CRACK = id("glass_crack");
    private static final ResourceLocation HUM = id("hum");
    private static final ResourceLocation SIGNAL_DROP = id("signal_drop");
    private static final ResourceLocation HEART_BREAK = id("heart_break");
    private static final ResourceLocation REVIVE = id("revive");

    private static SimpleSoundInstance humInstance;
    private static boolean humWanted;

    public static void tick(Minecraft minecraft, boolean silence, boolean threat) {
        boolean wanted = ClientConfig.AMBIENT_HUM.get() && !silence && !threat && minecraft.player != null && minecraft.level != null;
        if (wanted != humWanted) {
            humWanted = wanted;
            if (!wanted) {
                stopHum(minecraft);
            } else {
                startHum(minecraft);
            }
        } else if (wanted && (humInstance == null || !minecraft.getSoundManager().isActive(humInstance))) {
            startHum(minecraft);
        }

        if (silence || threat) {
            minecraft.getMusicManager().stopPlaying();
        }
    }

    public static void playCrack(float severity) {
        if (!ClientConfig.CRACK_SOUND.get()) {
            return;
        }
        float volume = ClientConfig.CRACK_SOUND_VOLUME.get().floatValue() * (0.45f + 0.65f * severity);
        float pitch = 0.92f + 0.18f * severity;
        playOneShot(GLASS_CRACK, SoundSource.PLAYERS, volume, pitch);
    }

    public static void playHeartbeat(float volume, float pitch) {
        playOneShot(HEARTBEAT, SoundSource.AMBIENT, volume, pitch);
    }

    public static void playSignalDrop() {
        playOneShot(SIGNAL_DROP, SoundSource.AMBIENT, 0.72f, 0.96f);
    }

    public static void playHeartBreak() {
        playOneShot(HEART_BREAK, SoundSource.AMBIENT, 0.95f, 0.9f);
    }

    public static void playRevive() {
        playOneShot(REVIVE, SoundSource.AMBIENT, 0.85f, 1.05f);
    }

    public static void stopHum(Minecraft minecraft) {
        if (humInstance != null) {
            minecraft.getSoundManager().stop(humInstance);
            humInstance = null;
        }
    }

    private static void startHum(Minecraft minecraft) {
        stopHum(minecraft);
        float volume = ClientConfig.AMBIENT_HUM_VOLUME.get().floatValue();
        if (volume <= 0.001f) {
            return;
        }
        humInstance = new SimpleSoundInstance(
                HUM,
                SoundSource.AMBIENT,
                volume,
                1.0f,
                RandomSource.create(),
                true,
                0,
                SoundInstance.Attenuation.NONE,
                0.0,
                0.0,
                0.0,
                true
        );
        minecraft.getSoundManager().play(humInstance);
    }

    private static void playOneShot(ResourceLocation location, SoundSource source, float volume, float pitch) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        SimpleSoundInstance sound = new SimpleSoundInstance(
                location,
                source,
                volume,
                pitch,
                RandomSource.create(),
                false,
                0,
                SoundInstance.Attenuation.NONE,
                0.0,
                0.0,
                0.0,
                true
        );
        minecraft.getSoundManager().play(sound);
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(LostCameraMedia.MODID, path);
    }

    private CameraAudio() {}
}
