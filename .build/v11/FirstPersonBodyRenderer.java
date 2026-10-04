package com.erzhan.lostcamera.client;

import com.erzhan.lostcamera.config.ClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;

public final class FirstPersonBodyRenderer {
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES || !ClientConfig.FIRST_PERSON_BODY.get()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.player instanceof AbstractClientPlayer player)
                || !minecraft.options.getCameraType().isFirstPerson()
                || player.isSpectator()
                || player.isInvisible()
                || player.isSleeping()) {
            return;
        }

        EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
        EntityRenderer<? super AbstractClientPlayer> entityRenderer = dispatcher.getRenderer(player);
        if (!(entityRenderer instanceof PlayerRenderer renderer)) {
            return;
        }

        float partial = event.getPartialTick();
        Vec3 camera = event.getCamera().getPosition();
        double x = Mth.lerp(partial, player.xOld, player.getX()) - camera.x;
        double y = Mth.lerp(partial, player.yOld, player.getY()) - camera.y;
        double z = Mth.lerp(partial, player.zOld, player.getZ()) - camera.z;

        PoseStack pose = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        int light = dispatcher.getPackedLightCoords(player, partial);
        PlayerModel<AbstractClientPlayer> model = renderer.getModel();
        boolean headVisible = model.head.visible;
        boolean hatVisible = model.hat.visible;

        pose.pushPose();
        pose.translate(x, y, z);
        model.head.visible = false;
        model.hat.visible = false;
        renderer.render(player, player.getYRot(), partial, pose, buffers, light);
        model.head.visible = headVisible;
        model.hat.visible = hatVisible;
        pose.popPose();
        buffers.endBatch();
    }

    private FirstPersonBodyRenderer() {}
}
