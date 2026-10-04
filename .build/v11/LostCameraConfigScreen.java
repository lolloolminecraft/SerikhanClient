package com.erzhan.lostcamera.client;

import com.erzhan.lostcamera.config.ClientConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public final class LostCameraConfigScreen extends Screen {
    private final Screen parent;
    private int page;

    public LostCameraConfigScreen(Screen parent) {
        super(Component.literal("Lost Camera Media"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        clearWidgets();
        if (page == 0) {
            initVisualPage();
        } else if (page == 1) {
            initDamagePage();
        } else if (page == 2) {
            initAtmospherePage();
        } else {
            initImmersionPage();
        }

        addRenderableWidget(Button.builder(Component.literal("<"), b -> changePage(-1))
                .bounds(width / 2 - 110, height - 28, 24, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(width / 2 - 75, height - 28, 150, 20).build());
        addRenderableWidget(Button.builder(Component.literal(">"), b -> changePage(1))
                .bounds(width / 2 + 86, height - 28, 24, 20).build());
    }

    private void initVisualPage() {
        addToggle("VHS", ClientConfig.VHS_ENABLED, left(), 42);
        addSlider("VHS intensity", ClientConfig.VHS_INTENSITY::get, ClientConfig.VHS_INTENSITY::set, 0.0, 1.0, right(), 42);
        addToggle("Scanlines", ClientConfig.SCANLINES, left(), 66);
        addToggle("Noise", ClientConfig.NOISE, left(), 90);
        addToggle("Chromatic bleed", ClientConfig.CHROMATIC_BLEED, left(), 114);
        addSlider("Chromatic", ClientConfig.CHROMATIC_INTENSITY::get, ClientConfig.CHROMATIC_INTENSITY::set, 0.0, 1.0, right(), 66);
        addToggle("Dark vignette", ClientConfig.VIGNETTE, left(), 138);
        addToggle("Camcorder HUD", ClientConfig.CAMCORDER_HUD, left(), 162);
        addToggle("Third person", ClientConfig.RENDER_THIRD_PERSON, left(), 186);
        addToggle("Over GUI", ClientConfig.RENDER_OVER_GUI, left(), 210);

        addToggle("Camera shake", ClientConfig.CAMERA_SHAKE, right(), 90);
        addSlider("Walk shake", ClientConfig.WALK_SHAKE::get, ClientConfig.WALK_SHAKE::set, 0.0, 2.0, right(), 114);
        addSlider("Sprint x", ClientConfig.SPRINT_MULTIPLIER::get, ClientConfig.SPRINT_MULTIPLIER::set, 1.0, 3.0, right(), 138);
        addToggle("Motion triggers", ClientConfig.MOTION_TRIGGERS, right(), 162);
        addSlider("Jump kick", ClientConfig.JUMP_KICK::get, ClientConfig.JUMP_KICK::set, 0.0, 2.0, right(), 186);
        addSlider("Fall sway", ClientConfig.FALL_SWAY::get, ClientConfig.FALL_SWAY::set, 0.0, 2.0, right(), 210);
    }

    private void initDamagePage() {
        addToggle("Glass cracks", ClientConfig.CRACKS_ENABLED, left(), 42);
        addSlider("Crack amount", ClientConfig.CRACK_INTENSITY::get, ClientConfig.CRACK_INTENSITY::set, 0.0, 1.5, right(), 42);
        addSlider("First cracks hearts", ClientConfig.CRACK_START_HEARTS::get, ClientConfig.CRACK_START_HEARTS::set, 1.0, 20.0, left(), 74);
        addSlider("Heavy cracks hearts", ClientConfig.HEAVY_CRACK_HEARTS::get, ClientConfig.HEAVY_CRACK_HEARTS::set, 1.0, 20.0, right(), 74);
        addSlider("Critical hearts", ClientConfig.CRITICAL_HEARTS::get, ClientConfig.CRITICAL_HEARTS::set, 1.0, 20.0, left(), 106);
        addToggle("Crack sound", ClientConfig.CRACK_SOUND, left(), 138);
        addSlider("Crack volume", ClientConfig.CRACK_SOUND_VOLUME::get, ClientConfig.CRACK_SOUND_VOLUME::set, 0.0, 1.5, right(), 138);
        addToggle("Pain edges", ClientConfig.PAIN_VIGNETTE, left(), 170);
        addSlider("Pain intensity", ClientConfig.PAIN_INTENSITY::get, ClientConfig.PAIN_INTENSITY::set, 0.0, 1.5, right(), 170);
        addToggle("Critical pulse", ClientConfig.PAIN_PULSE, left(), 202);
        addToggle("Heartbeat", ClientConfig.HEARTBEAT, right(), 202);
        addToggle("Pixel heart", ClientConfig.CRITICAL_HEART_ICON, left(), 226);
        addToggle("Respawn heart", ClientConfig.RESPAWN_HEART_ICON, right(), 226);
    }

    private void initAtmospherePage() {
        addToggle("Real darkness", ClientConfig.REAL_DARKNESS, left(), 42);
        addSlider("Darkness", ClientConfig.DARKNESS_STRENGTH::get, ClientConfig.DARKNESS_STRENGTH::set, 0.0, 1.0, right(), 42);
        addSlider("Night darkness", ClientConfig.NIGHT_DARKNESS::get, ClientConfig.NIGHT_DARKNESS::set, 0.0, 1.0, left(), 74);
        addToggle("Electrical hum", ClientConfig.AMBIENT_HUM, left(), 106);
        addSlider("Hum volume", ClientConfig.AMBIENT_HUM_VOLUME::get, ClientConfig.AMBIENT_HUM_VOLUME::set, 0.0, 1.0, right(), 106);
        addToggle("Random silence", ClientConfig.RANDOM_SILENCE, left(), 138);
        addSlider("Silence min sec", () -> ClientConfig.SILENCE_MIN_SECONDS.get().doubleValue(), v -> ClientConfig.SILENCE_MIN_SECONDS.set((int) Math.round(v)), 1, 30, left(), 170);
        addSlider("Silence max sec", () -> ClientConfig.SILENCE_MAX_SECONDS.get().doubleValue(), v -> ClientConfig.SILENCE_MAX_SECONDS.set((int) Math.round(v)), 1, 60, right(), 170);
        addToggle("Threat detection", ClientConfig.THREAT_DETECTION, left(), 202);
        addSlider("Threat radius", ClientConfig.THREAT_RADIUS::get, ClientConfig.THREAT_RADIUS::set, 4.0, 64.0, right(), 202);
        addToggle("Threat heartbeat", ClientConfig.THREAT_HEARTBEAT, left(), 226);
    }

    private void initImmersionPage() {
        addToggle("First-person body", ClientConfig.FIRST_PERSON_BODY, left(), 42);
        addToggle("Damage jolt", ClientConfig.DAMAGE_JOLT, right(), 42);
        addSlider("Jolt power", ClientConfig.DAMAGE_JOLT_INTENSITY::get, ClientConfig.DAMAGE_JOLT_INTENSITY::set, 0.0, 2.0, right(), 74);
        addSlider("Swim sway", ClientConfig.SWIM_SWAY::get, ClientConfig.SWIM_SWAY::set, 0.0, 2.0, left(), 74);
        addToggle("LOST MEDIA death", ClientConfig.DEATH_SEQUENCE, left(), 114);
        addSlider("LOST MEDIA flashes", () -> ClientConfig.LOST_MEDIA_FLASHES.get().doubleValue(), v -> ClientConfig.LOST_MEDIA_FLASHES.set((int) Math.round(v)), 1.0, 12.0, right(), 114);
        addSlider("Blackout ticks", () -> ClientConfig.BLACKOUT_TICKS.get().doubleValue(), v -> ClientConfig.BLACKOUT_TICKS.set((int) Math.round(v)), 1.0, 100.0, left(), 146);
        addSlider("Restart delay", () -> ClientConfig.RESTART_DELAY_TICKS.get().doubleValue(), v -> ClientConfig.RESTART_DELAY_TICKS.set((int) Math.round(v)), 20.0, 200.0, right(), 146);
    }

    private void addToggle(String name, ForgeConfigSpec.BooleanValue value, int x, int y) {
        Button[] holder = new Button[1];
        holder[0] = Button.builder(toggleLabel(name, value.get()), button -> {
                    value.set(!value.get());
                    button.setMessage(toggleLabel(name, value.get()));
                })
                .bounds(x, y, 150, 20)
                .build();
        addRenderableWidget(holder[0]);
    }

    private void addSlider(String label, DoubleSupplier getter, DoubleConsumer setter, double min, double max, int x, int y) {
        double normalized = (getter.getAsDouble() - min) / (max - min);
        addRenderableWidget(new ValueSlider(x, y, 150, 20, label, normalized, min, max, setter));
    }

    private void changePage(int delta) {
        page = Math.floorMod(page + delta, 4);
        rebuildWidgets();
    }

    private int left() {
        return width / 2 - 158;
    }

    private int right() {
        return width / 2 + 8;
    }

    private static Component toggleLabel(String name, boolean value) {
        return Component.literal(name + ": " + (value ? "ON" : "OFF"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        String[] names = {"CAMERA / VHS", "DAMAGE / BODY", "DARKNESS / SOUND", "IMMERSION / DEATH"};
        graphics.drawCenteredString(font, title, width / 2, 13, 0xFFE8E4D8);
        graphics.drawCenteredString(font, names[page], width / 2, 26, 0xFF8F8C84);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }

    private static final class ValueSlider extends AbstractSliderButton {
        private final String label;
        private final double min;
        private final double max;
        private final DoubleConsumer setter;

        private ValueSlider(int x, int y, int width, int height, String label, double value, double min, double max, DoubleConsumer setter) {
            super(x, y, width, height, Component.empty(), Mth.clamp(value, 0.0, 1.0));
            this.label = label;
            this.min = min;
            this.max = max;
            this.setter = setter;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            double actual = min + value * (max - min);
            String formatted = Math.abs(actual - Math.rint(actual)) < 0.005
                    ? String.format("%.0f", actual)
                    : String.format("%.2f", actual);
            setMessage(Component.literal(label + ": " + formatted));
        }

        @Override
        protected void applyValue() {
            setter.accept(min + value * (max - min));
        }
    }
}
