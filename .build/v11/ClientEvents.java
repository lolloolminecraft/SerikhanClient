package com.erzhan.lostcamera.client;

import com.erzhan.lostcamera.LostCameraMedia;
import com.erzhan.lostcamera.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LostCameraMedia.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientEvents {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LostCameraState.tick(minecraft);
        while (ClientBootstrap.OPEN_CONFIG.consumeClick()) {
            minecraft.setScreen(new LostCameraConfigScreen(minecraft.screen));
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        CameraOverlayRenderer.renderWorldOverlay(event.getGuiGraphics(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        FirstPersonBodyRenderer.render(event);
    }

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (!ClientConfig.CAMERA_SHAKE.get()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !minecraft.options.getCameraType().isFirstPerson()) {
            return;
        }

        boolean moving = minecraft.player.getDeltaMovement().horizontalDistanceSqr() > 0.00025;
        float shake = moving ? ClientConfig.WALK_SHAKE.get().floatValue() : 0.0f;
        if (minecraft.player.isSprinting()) {
            shake *= ClientConfig.SPRINT_MULTIPLIER.get().floatValue();
        }

        double t = (minecraft.player.tickCount + event.getPartialTick()) * 0.72;
        float walkRoll = (float) Math.sin(t * 1.7) * 0.36f * shake;
        float walkPitch = (float) Math.sin(t * 2.0 + 0.8) * 0.22f * shake;
        float walkYaw = (float) Math.sin(t * 1.15 + 1.3) * 0.18f * shake;

        float jolt = ClientConfig.DAMAGE_JOLT.get()
                ? LostCameraState.damageJolt() * ClientConfig.DAMAGE_JOLT_INTENSITY.get().floatValue()
                : 0.0f;
        float joltWave = (float) Math.sin((1.0f - LostCameraState.damageJolt()) * Math.PI * 5.0f);

        float motionPitch = 0.0f;
        float motionRoll = 0.0f;
        float motionYaw = 0.0f;
        if (ClientConfig.MOTION_TRIGGERS.get()) {
            float jump = LostCameraState.motionKick() * ClientConfig.JUMP_KICK.get().floatValue();
            motionPitch -= jump * 1.65f;
            motionRoll += (float) Math.sin(t * 2.5) * jump * 0.45f;

            double vy = minecraft.player.getDeltaMovement().y;
            if (!minecraft.player.onGround() && vy < -0.08) {
                float fall = Mth.clamp((float) (-vy - 0.08) * 1.35f, 0.0f, 1.0f) * ClientConfig.FALL_SWAY.get().floatValue();
                motionPitch += fall * 1.45f;
                motionRoll += (float) Math.sin(t * 0.9) * fall * 1.2f;
                motionYaw += (float) Math.sin(t * 0.67 + 0.8) * fall * 0.65f;
            }

            if (minecraft.player.isSwimming() || minecraft.player.isInWater()) {
                float swim = ClientConfig.SWIM_SWAY.get().floatValue();
                motionRoll += (float) Math.sin(t * 0.75) * 1.25f * swim;
                motionPitch += (float) Math.sin(t * 0.55 + 1.6) * 0.75f * swim;
                motionYaw += (float) Math.sin(t * 0.46 + 0.4) * 0.55f * swim;
            }
        }

        event.setRoll(event.getRoll() + walkRoll + joltWave * 1.7f * jolt + motionRoll);
        event.setPitch(event.getPitch() + walkPitch + joltWave * 0.75f * jolt + motionPitch);
        event.setYaw(event.getYaw() + walkYaw - joltWave * 0.55f * jolt + motionYaw);
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (!(event.getNewScreen() instanceof DeathScreen)) {
            return;
        }
        if (LostCameraState.isRespawnPending()) {
            event.setCanceled(true);
            return;
        }
        if (ClientConfig.DEATH_SEQUENCE.get()) {
            event.setNewScreen(new LostMediaDeathScreen());
        }
    }

    private ClientEvents() {}
}
