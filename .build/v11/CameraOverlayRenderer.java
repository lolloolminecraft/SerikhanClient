package com.erzhan.lostcamera.client;

import com.erzhan.lostcamera.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;

import java.util.Locale;

public final class CameraOverlayRenderer {
    private static final Segment[] CRACKS = {
            new Segment(0.86f, 0.00f, 0.83f, 0.10f),
            new Segment(0.00f, 0.31f, 0.08f, 0.34f),
            new Segment(1.00f, 0.65f, 0.92f, 0.62f),
            new Segment(0.34f, 1.00f, 0.38f, 0.89f),
            new Segment(0.13f, 0.00f, 0.17f, 0.09f),
            new Segment(0.74f, 1.00f, 0.71f, 0.90f),
            new Segment(1.00f, 0.20f, 0.94f, 0.25f),
            new Segment(0.00f, 0.78f, 0.07f, 0.74f),
            new Segment(0.83f, 0.10f, 0.78f, 0.19f),
            new Segment(0.83f, 0.10f, 0.90f, 0.18f),
            new Segment(0.08f, 0.34f, 0.16f, 0.40f),
            new Segment(0.08f, 0.34f, 0.12f, 0.27f),
            new Segment(0.92f, 0.62f, 0.85f, 0.69f),
            new Segment(0.92f, 0.62f, 0.86f, 0.55f),
            new Segment(0.38f, 0.89f, 0.43f, 0.79f),
            new Segment(0.38f, 0.89f, 0.31f, 0.81f),
            new Segment(0.17f, 0.09f, 0.23f, 0.17f),
            new Segment(0.17f, 0.09f, 0.11f, 0.19f),
            new Segment(0.71f, 0.90f, 0.65f, 0.80f),
            new Segment(0.71f, 0.90f, 0.77f, 0.81f),
            new Segment(0.94f, 0.25f, 0.87f, 0.31f),
            new Segment(0.07f, 0.74f, 0.14f, 0.68f),
            new Segment(0.78f, 0.19f, 0.72f, 0.28f),
            new Segment(0.78f, 0.19f, 0.82f, 0.29f),
            new Segment(0.90f, 0.18f, 0.94f, 0.29f),
            new Segment(0.16f, 0.40f, 0.23f, 0.48f),
            new Segment(0.16f, 0.40f, 0.11f, 0.51f),
            new Segment(0.85f, 0.69f, 0.78f, 0.76f),
            new Segment(0.86f, 0.55f, 0.79f, 0.49f),
            new Segment(0.43f, 0.79f, 0.48f, 0.68f),
            new Segment(0.31f, 0.81f, 0.26f, 0.71f),
            new Segment(0.23f, 0.17f, 0.29f, 0.26f),
            new Segment(0.11f, 0.19f, 0.08f, 0.30f),
            new Segment(0.65f, 0.80f, 0.59f, 0.70f),
            new Segment(0.77f, 0.81f, 0.82f, 0.71f),
            new Segment(0.87f, 0.31f, 0.80f, 0.39f),
            new Segment(0.14f, 0.68f, 0.21f, 0.61f),
            new Segment(0.72f, 0.28f, 0.66f, 0.37f),
            new Segment(0.72f, 0.28f, 0.74f, 0.40f),
            new Segment(0.23f, 0.48f, 0.31f, 0.55f),
            new Segment(0.11f, 0.51f, 0.08f, 0.62f),
            new Segment(0.78f, 0.76f, 0.70f, 0.68f),
            new Segment(0.79f, 0.49f, 0.71f, 0.44f),
            new Segment(0.48f, 0.68f, 0.53f, 0.57f),
            new Segment(0.26f, 0.71f, 0.33f, 0.63f),
            new Segment(0.29f, 0.26f, 0.36f, 0.34f),
            new Segment(0.59f, 0.70f, 0.53f, 0.61f),
            new Segment(0.80f, 0.39f, 0.73f, 0.46f),
            new Segment(0.21f, 0.61f, 0.28f, 0.54f)
    };

    public static void renderWorldOverlay(GuiGraphics graphics, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }
        if (minecraft.screen != null && !ClientConfig.RENDER_OVER_GUI.get()) {
            return;
        }
        if (!minecraft.options.getCameraType().isFirstPerson() && !ClientConfig.RENDER_THIRD_PERSON.get()) {
            return;
        }

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        long time = System.currentTimeMillis();

        renderDarkness(graphics, width, height, partialTick);
        renderVhs(graphics, width, height, time, false);
        renderCracks(graphics, width, height);
        renderPain(graphics, width, height, time);
        renderDamageFlash(graphics, width, height);
        renderCriticalHeart(graphics, width, height, time);
        renderRespawnHeart(graphics, width, height);
        renderHud(graphics, width, height);
    }

    public static void renderVhs(GuiGraphics graphics, int width, int height, long time, boolean deathMode) {
        if (!ClientConfig.VHS_ENABLED.get()) {
            return;
        }

        float intensity = ClientConfig.VHS_INTENSITY.get().floatValue();
        if (intensity <= 0.001f) {
            return;
        }

        if (ClientConfig.SCANLINES.get()) {
            int alpha = Math.round((deathMode ? 30.0f : 20.0f) * intensity);
            int color = argb(alpha, 0, 0, 0);
            for (int y = 1; y < height; y += 3) {
                graphics.fill(0, y, width, y + 1, color);
            }
        }

        if (ClientConfig.NOISE.get()) {
            int seed = (int) (time / 42L);
            int count = deathMode ? 30 : 18;
            for (int i = 0; i < count; i++) {
                int n = hash(seed + i * 131);
                int y = Math.floorMod(n, Math.max(1, height));
                int x = Math.floorMod(n >>> 8, Math.max(1, width));
                int len = 4 + Math.floorMod(n >>> 16, Math.max(5, width / 8));
                int alpha = Math.round((8 + Math.floorMod(n >>> 24, 18)) * intensity);
                graphics.fill(x, y, Math.min(width, x + len), y + 1, argb(alpha, 220, 220, 210));
            }
        }

        int cycle = (int) ((time / 55L) % 173L);
        if (cycle < 5 || (deathMode && cycle < 13)) {
            int y = Math.floorMod(hash(cycle * 911 + (int) (time / 400L)), Math.max(1, height - 4));
            int bandAlpha = Math.round(52.0f * intensity);
            graphics.fill(0, y, width, y + 1, argb(bandAlpha, 235, 235, 225));
            graphics.fill(0, y + 2, width, y + 4, argb(Math.round(36.0f * intensity), 0, 0, 0));
        }

        if (ClientConfig.CHROMATIC_BLEED.get()) {
            float chroma = ClientConfig.CHROMATIC_INTENSITY.get().floatValue() * intensity;
            int alpha = Math.round(18.0f * chroma);
            int edge = Math.max(1, Math.round(2.0f + 3.0f * chroma));
            graphics.fill(0, 0, edge, height, argb(alpha, 255, 40, 40));
            graphics.fill(width - edge, 0, width, height, argb(alpha, 30, 210, 255));
        }

        if (ClientConfig.VIGNETTE.get()) {
            drawNeutralVignette(graphics, width, height, intensity * (deathMode ? 1.25f : 1.0f));
        }
    }

    public static void renderHud(GuiGraphics graphics, int width, int height) {
        if (!ClientConfig.CAMCORDER_HUD.get()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        long seconds = LostCameraState.sessionMillis() / 1000L;
        long hours = seconds / 3600L;
        long minutes = (seconds / 60L) % 60L;
        long secs = seconds % 60L;
        String timecode = String.format(Locale.ROOT, "%02d:%02d:%02d", hours % 100, minutes, secs);
        String cam = String.format(Locale.ROOT, "CAM %02d  SP", Math.min(99, LostCameraState.cameraNumber()));

        int white = 0xFFE8E4D8;
        graphics.fill(8, 8, 11, 11, 0xFFE52521);
        graphics.drawString(minecraft.font, "REC", 14, 6, white, false);
        graphics.drawString(minecraft.font, timecode, 8, 17, white, false);
        graphics.drawString(minecraft.font, cam, 8, 28, 0xFFC8C3B8, false);

        String battery = "BAT [|||]";
        int textWidth = minecraft.font.width(battery);
        graphics.drawString(minecraft.font, battery, width - textWidth - 8, 7, white, false);
        graphics.drawString(minecraft.font, "AUTO", width - minecraft.font.width("AUTO") - 8, height - 16, 0xFFBCB7AD, false);
    }

    public static void renderPixelHeart(GuiGraphics graphics, int cx, int cy, int scale, int color, int darkColor, boolean broken) {
        int[][] pixels = {
                {1, 0}, {2, 0}, {4, 0}, {5, 0},
                {0, 1}, {1, 1}, {2, 1}, {3, 1}, {4, 1}, {5, 1}, {6, 1},
                {0, 2}, {1, 2}, {2, 2}, {3, 2}, {4, 2}, {5, 2}, {6, 2},
                {1, 3}, {2, 3}, {3, 3}, {4, 3}, {5, 3},
                {2, 4}, {3, 4}, {4, 4},
                {3, 5}
        };
        int left = cx - (7 * scale) / 2;
        int top = cy - 3 * scale;
        for (int[] p : pixels) {
            int x = p[0];
            int y = p[1];
            if (broken && ((y <= 2 && x == 3) || (y == 3 && x == 3) || (y == 4 && x == 2))) {
                continue;
            }
            int px = left + x * scale;
            int py = top + y * scale;
            graphics.fill(px + 1, py + 1, px + scale + 1, py + scale + 1, darkColor);
            graphics.fill(px, py, px + scale, py + scale, color);
        }
        if (broken) {
            graphics.fill(cx - scale, cy - scale, cx, cy, 0xFF1B0B0B);
            graphics.fill(cx, cy, cx + scale, cy + scale, 0xFF1B0B0B);
        }
    }

    private static void renderCracks(GuiGraphics graphics, int width, int height) {
        if (!ClientConfig.CRACKS_ENABLED.get()) {
            return;
        }
        float level = Mth.clamp(LostCameraState.crackLevel() * ClientConfig.CRACK_INTENSITY.get().floatValue(), 0.0f, 1.35f);
        if (level <= 0.01f) {
            return;
        }

        float reveal = level * CRACKS.length;
        for (int i = 0; i < CRACKS.length; i++) {
            float local = Mth.clamp(reveal - i, 0.0f, 1.0f);
            if (local <= 0.0f) {
                break;
            }
            Segment s = CRACKS[i];
            int x1 = Math.round(s.x1 * width);
            int y1 = Math.round(s.y1 * height);
            int x2 = Math.round(Mth.lerp(local, s.x1, s.x2) * width);
            int y2 = Math.round(Mth.lerp(local, s.y1, s.y2) * height);
            int alpha = Math.round(92.0f + 100.0f * Math.min(1.0f, level));
            drawLine(graphics, x1 + 1, y1 + 1, x2 + 1, y2 + 1, argb(Math.round(alpha * 0.82f), 8, 9, 9));
            drawLine(graphics, x1, y1, x2, y2, argb(alpha, 151, 157, 153));
            if ((i & 1) == 0) {
                drawLine(graphics, x1 - 1, y1, x2 - 1, y2, argb(Math.round(alpha * 0.28f), 236, 238, 232));
            }
        }
    }

    private static void renderPain(GuiGraphics graphics, int width, int height, long time) {
        if (!ClientConfig.PAIN_VIGNETTE.get()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        float hearts = minecraft.player.getHealth() * 0.5f;
        float heavy = ClientConfig.HEAVY_CRACK_HEARTS.get().floatValue();
        if (hearts > heavy) {
            return;
        }

        float critical = Math.min(heavy - 0.1f, ClientConfig.CRITICAL_HEARTS.get().floatValue());
        float intensity = Mth.clamp(1.0f - hearts / Math.max(0.5f, heavy), 0.18f, 1.0f);
        intensity *= ClientConfig.PAIN_INTENSITY.get().floatValue();

        if (hearts < critical && ClientConfig.PAIN_PULSE.get()) {
            float danger = Mth.clamp(1.0f - hearts / Math.max(0.5f, critical), 0.0f, 1.0f);
            double speed = 0.0065 + 0.0055 * danger;
            float pulse = 0.68f + 0.32f * (float) ((Math.sin(time * speed) + 1.0) * 0.5);
            intensity *= pulse;
        }

        drawRedVignette(graphics, width, height, intensity);
    }

    private static void renderCriticalHeart(GuiGraphics graphics, int width, int height, long time) {
        if (!ClientConfig.CRITICAL_HEART_ICON.get()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.player.isDeadOrDying()) {
            return;
        }
        float hearts = minecraft.player.getHealth() * 0.5f;
        float critical = ClientConfig.CRITICAL_HEARTS.get().floatValue();
        if (hearts >= critical) {
            return;
        }
        float danger = Mth.clamp(1.0f - hearts / Math.max(0.5f, critical), 0.0f, 1.0f);
        float pulse = 0.78f + 0.22f * (float) ((Math.sin(time * (0.010 + danger * 0.008)) + 1.0) * 0.5);
        int red = Math.round(175 + 70 * pulse);
        renderPixelHeart(graphics, width / 2, height / 2 + 34, 2, argb(220, red, 26, 30), 0x99180000, false);
    }

    private static void renderRespawnHeart(GuiGraphics graphics, int width, int height) {
        if (!ClientConfig.RESPAWN_HEART_ICON.get()) {
            return;
        }
        int ticks = LostCameraState.respawnHeartTicks();
        if (ticks <= 0) {
            return;
        }
        float alpha = Mth.clamp(ticks / 22.0f, 0.0f, 1.0f);
        int color = argb(Math.round(255.0f * alpha), 232, 32, 44);
        int dark = argb(Math.round(180.0f * alpha), 42, 4, 8);
        int scale = ticks > 18 ? 4 : ticks > 12 ? 3 : 2;
        renderPixelHeart(graphics, width / 2, height / 2, scale, color, dark, false);
    }

    private static void renderDarkness(GuiGraphics graphics, int width, int height, float partialTick) {
        if (!ClientConfig.REAL_DARKNESS.get()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }
        BlockPos pos = minecraft.player.blockPosition().above();
        float block = minecraft.level.getBrightness(LightLayer.BLOCK, pos) / 15.0f;
        float skyRaw = minecraft.level.getBrightness(LightLayer.SKY, pos) / 15.0f;
        float skyBrightness = Mth.clamp(minecraft.level.getSkyDarken(partialTick), 0.0f, 1.0f);
        float sky = skyRaw * skyBrightness;
        float light = Math.max(block, sky);
        float darkness = Mth.clamp((1.0f - light) * ClientConfig.DARKNESS_STRENGTH.get().floatValue(), 0.0f, 0.92f);
        float night = Mth.clamp((1.0f - skyBrightness) * skyRaw * ClientConfig.NIGHT_DARKNESS.get().floatValue(), 0.0f, 0.48f);
        float finalDarkness = Mth.clamp(darkness + night, 0.0f, 0.94f);
        if (finalDarkness > 0.015f) {
            graphics.fill(0, 0, width, height, argb(Math.round(205.0f * finalDarkness), 0, 0, 0));
        }
    }

    private static void renderDamageFlash(GuiGraphics graphics, int width, int height) {
        if (!ClientConfig.DAMAGE_JOLT.get()) {
            return;
        }
        float flash = LostCameraState.damageFlash();
        if (flash <= 0.01f) {
            return;
        }
        graphics.fill(0, 0, width, height, argb(Math.round(36.0f * flash), 160, 0, 0));
    }

    private static void drawNeutralVignette(GuiGraphics graphics, int width, int height, float intensity) {
        int bands = 8;
        int maxX = Math.max(8, width / 9);
        int maxY = Math.max(8, height / 9);
        for (int i = 0; i < bands; i++) {
            float t = 1.0f - i / (float) bands;
            int alpha = Math.round(11.0f * intensity * t);
            int x = Math.max(1, maxX / bands);
            int y = Math.max(1, maxY / bands);
            int color = argb(alpha, 0, 0, 0);
            graphics.fill(i * x, i * y, width - i * x, (i + 1) * y, color);
            graphics.fill(i * x, height - (i + 1) * y, width - i * x, height - i * y, color);
            graphics.fill(i * x, (i + 1) * y, (i + 1) * x, height - (i + 1) * y, color);
            graphics.fill(width - (i + 1) * x, (i + 1) * y, width - i * x, height - (i + 1) * y, color);
        }
    }

    private static void drawRedVignette(GuiGraphics graphics, int width, int height, float intensity) {
        int bands = 9;
        int maxX = Math.max(10, width / 7);
        int maxY = Math.max(10, height / 7);
        for (int i = 0; i < bands; i++) {
            float t = 1.0f - i / (float) bands;
            int alpha = Math.round(24.0f * intensity * t);
            int x = Math.max(1, maxX / bands);
            int y = Math.max(1, maxY / bands);
            int color = argb(alpha, 150, 0, 0);
            graphics.fill(i * x, i * y, width - i * x, (i + 1) * y, color);
            graphics.fill(i * x, height - (i + 1) * y, width - i * x, height - i * y, color);
            graphics.fill(i * x, (i + 1) * y, (i + 1) * x, height - (i + 1) * y, color);
            graphics.fill(width - (i + 1) * x, (i + 1) * y, width - i * x, height - (i + 1) * y, color);
        }
    }

    private static void drawLine(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int sx = x1 < x2 ? 1 : -1;
        int dy = -Math.abs(y2 - y1);
        int sy = y1 < y2 ? 1 : -1;
        int err = dx + dy;
        int x = x1;
        int y = y1;
        while (true) {
            graphics.fill(x, y, x + 1, y + 1, color);
            if (x == x2 && y == y2) {
                break;
            }
            int e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                x += sx;
            }
            if (e2 <= dx) {
                err += dx;
                y += sy;
            }
        }
    }

    private static int hash(int x) {
        x ^= x << 13;
        x ^= x >>> 17;
        x ^= x << 5;
        return x;
    }

    private static int argb(int a, int r, int g, int b) {
        return ((Mth.clamp(a, 0, 255) & 255) << 24)
                | ((Mth.clamp(r, 0, 255) & 255) << 16)
                | ((Mth.clamp(g, 0, 255) & 255) << 8)
                | (Mth.clamp(b, 0, 255) & 255);
    }

    private record Segment(float x1, float y1, float x2, float y2) {}

    private CameraOverlayRenderer() {}
}
