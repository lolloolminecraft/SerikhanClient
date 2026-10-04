package com.erzhan.lostcamera.client;

import com.erzhan.lostcamera.config.ClientConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public final class LostMediaDeathScreen extends Screen {
    private int ticks;
    private boolean restartRequested;

    public LostMediaDeathScreen() {
        super(Component.literal("LOST MEDIA"));
        LostCameraState.markDeath();
    }

    @Override
    public void tick() {
        ticks++;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int blackoutTicks = Math.max(1, ClientConfig.BLACKOUT_TICKS.get());
        float fade = Mth.clamp((ticks + partialTick) / blackoutTicks, 0.0f, 1.0f);
        int alpha = Math.round(255.0f * (0.55f + 0.45f * fade));
        graphics.fill(0, 0, width, height, alpha << 24);

        CameraOverlayRenderer.renderVhs(graphics, width, height, System.currentTimeMillis(), true);
        renderBrokenHeart(graphics);

        if (!restartRequested && shouldShowLostMedia()) {
            int textWidth = font.width("LOST MEDIA");
            int x = width / 2 - textWidth / 2;
            int y = height / 2 - 18;
            graphics.drawString(font, "LOST MEDIA", x + 1, y + 1, 0xFF64191D, false);
            graphics.drawString(font, "LOST MEDIA", x, y, 0xFFE8E4D8, false);
        }

        if (!restartRequested && ticks >= ClientConfig.RESTART_DELAY_TICKS.get()) {
            String text = "RESTART";
            int x = width / 2 - font.width(text) / 2;
            int y = height / 2 + 28;
            boolean hover = mouseX >= x - 7 && mouseX <= x + font.width(text) + 7 && mouseY >= y - 5 && mouseY <= y + 14;
            int color = hover ? 0xFFFFFFFF : 0xFFC4C0B7;
            graphics.drawString(font, text, x + 1, y + 1, 0xAA261012, false);
            graphics.drawString(font, text, x, y, color, false);
        }
    }

    private void renderBrokenHeart(GuiGraphics graphics) {
        if (!ClientConfig.CRITICAL_HEART_ICON.get() || ticks > 30) {
            return;
        }
        float fade = 1.0f - Mth.clamp((ticks - 10) / 20.0f, 0.0f, 1.0f);
        int red = ((int) (255 * fade) << 24) | 0x00DE2330;
        int dark = ((int) (190 * fade) << 24) | 0x001D0508;
        CameraOverlayRenderer.renderPixelHeart(graphics, width / 2, height / 2 + 4, 3, red, dark, ticks >= 5);
        if (ticks >= 5) {
            int debrisAlpha = Math.max(0, (int) (180 * fade));
            int c = (debrisAlpha << 24) | 0x00B91D27;
            int d = ticks - 5;
            graphics.fill(width / 2 - 9 - d / 2, height / 2 + 13 + d / 3, width / 2 - 6 - d / 2, height / 2 + 16 + d / 3, c);
            graphics.fill(width / 2 + 7 + d / 2, height / 2 + 10 + d / 2, width / 2 + 10 + d / 2, height / 2 + 13 + d / 2, c);
        }
    }

    private boolean shouldShowLostMedia() {
        int blackout = ClientConfig.BLACKOUT_TICKS.get();
        int restartAt = ClientConfig.RESTART_DELAY_TICKS.get();
        if (ticks < blackout) {
            return false;
        }
        if (ticks >= restartAt) {
            return true;
        }
        int flashes = Math.max(1, ClientConfig.LOST_MEDIA_FLASHES.get());
        int span = Math.max(1, restartAt - blackout);
        int slice = Math.max(2, span / (flashes * 2));
        return ((ticks - blackout) / slice) % 2 == 0;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && !restartRequested && ticks >= ClientConfig.RESTART_DELAY_TICKS.get()) {
            String text = "RESTART";
            int x = width / 2 - font.width(text) / 2;
            int y = height / 2 + 28;
            if (mouseX >= x - 9 && mouseX <= x + font.width(text) + 9 && mouseY >= y - 7 && mouseY <= y + 16) {
                restartRequested = true;
                LostCameraState.requestRespawn();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
