package com.erzhan.lostcamera.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ClientConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue VHS_ENABLED;
    public static final ForgeConfigSpec.DoubleValue VHS_INTENSITY;
    public static final ForgeConfigSpec.BooleanValue SCANLINES;
    public static final ForgeConfigSpec.BooleanValue NOISE;
    public static final ForgeConfigSpec.BooleanValue CHROMATIC_BLEED;
    public static final ForgeConfigSpec.DoubleValue CHROMATIC_INTENSITY;
    public static final ForgeConfigSpec.BooleanValue VIGNETTE;
    public static final ForgeConfigSpec.BooleanValue CAMCORDER_HUD;
    public static final ForgeConfigSpec.BooleanValue RENDER_THIRD_PERSON;
    public static final ForgeConfigSpec.BooleanValue RENDER_OVER_GUI;

    public static final ForgeConfigSpec.BooleanValue CAMERA_SHAKE;
    public static final ForgeConfigSpec.DoubleValue WALK_SHAKE;
    public static final ForgeConfigSpec.DoubleValue SPRINT_MULTIPLIER;
    public static final ForgeConfigSpec.BooleanValue DAMAGE_JOLT;
    public static final ForgeConfigSpec.DoubleValue DAMAGE_JOLT_INTENSITY;
    public static final ForgeConfigSpec.BooleanValue MOTION_TRIGGERS;
    public static final ForgeConfigSpec.DoubleValue JUMP_KICK;
    public static final ForgeConfigSpec.DoubleValue FALL_SWAY;
    public static final ForgeConfigSpec.DoubleValue SWIM_SWAY;

    public static final ForgeConfigSpec.BooleanValue CRACKS_ENABLED;
    public static final ForgeConfigSpec.DoubleValue CRACK_INTENSITY;
    public static final ForgeConfigSpec.DoubleValue CRACK_START_HEARTS;
    public static final ForgeConfigSpec.DoubleValue HEAVY_CRACK_HEARTS;
    public static final ForgeConfigSpec.DoubleValue CRITICAL_HEARTS;
    public static final ForgeConfigSpec.BooleanValue CRACK_SOUND;
    public static final ForgeConfigSpec.DoubleValue CRACK_SOUND_VOLUME;

    public static final ForgeConfigSpec.BooleanValue PAIN_VIGNETTE;
    public static final ForgeConfigSpec.DoubleValue PAIN_INTENSITY;
    public static final ForgeConfigSpec.BooleanValue PAIN_PULSE;
    public static final ForgeConfigSpec.BooleanValue HEARTBEAT;
    public static final ForgeConfigSpec.DoubleValue HEARTBEAT_VOLUME;
    public static final ForgeConfigSpec.BooleanValue CRITICAL_HEART_ICON;
    public static final ForgeConfigSpec.BooleanValue RESPAWN_HEART_ICON;

    public static final ForgeConfigSpec.BooleanValue REAL_DARKNESS;
    public static final ForgeConfigSpec.DoubleValue DARKNESS_STRENGTH;
    public static final ForgeConfigSpec.DoubleValue NIGHT_DARKNESS;
    public static final ForgeConfigSpec.BooleanValue AMBIENT_HUM;
    public static final ForgeConfigSpec.DoubleValue AMBIENT_HUM_VOLUME;
    public static final ForgeConfigSpec.BooleanValue RANDOM_SILENCE;
    public static final ForgeConfigSpec.IntValue SILENCE_MIN_SECONDS;
    public static final ForgeConfigSpec.IntValue SILENCE_MAX_SECONDS;
    public static final ForgeConfigSpec.IntValue SILENCE_GAP_MIN_SECONDS;
    public static final ForgeConfigSpec.IntValue SILENCE_GAP_MAX_SECONDS;
    public static final ForgeConfigSpec.BooleanValue THREAT_DETECTION;
    public static final ForgeConfigSpec.DoubleValue THREAT_RADIUS;
    public static final ForgeConfigSpec.BooleanValue THREAT_HEARTBEAT;

    public static final ForgeConfigSpec.BooleanValue FIRST_PERSON_BODY;

    public static final ForgeConfigSpec.BooleanValue DEATH_SEQUENCE;
    public static final ForgeConfigSpec.IntValue LOST_MEDIA_FLASHES;
    public static final ForgeConfigSpec.IntValue BLACKOUT_TICKS;
    public static final ForgeConfigSpec.IntValue RESTART_DELAY_TICKS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("camera_overlay");
        VHS_ENABLED = builder.define("vhsEnabled", true);
        VHS_INTENSITY = builder.defineInRange("vhsIntensity", 0.55, 0.0, 1.0);
        SCANLINES = builder.define("scanlines", true);
        NOISE = builder.define("noise", true);
        CHROMATIC_BLEED = builder.define("chromaticBleed", true);
        CHROMATIC_INTENSITY = builder.defineInRange("chromaticIntensity", 0.35, 0.0, 1.0);
        VIGNETTE = builder.define("vignette", true);
        CAMCORDER_HUD = builder.define("camcorderHud", true);
        RENDER_THIRD_PERSON = builder.define("renderThirdPerson", false);
        RENDER_OVER_GUI = builder.define("renderOverGui", false);
        builder.pop();

        builder.push("camera_motion");
        CAMERA_SHAKE = builder.define("cameraShake", true);
        WALK_SHAKE = builder.defineInRange("walkShake", 0.45, 0.0, 2.0);
        SPRINT_MULTIPLIER = builder.defineInRange("sprintMultiplier", 1.55, 1.0, 3.0);
        DAMAGE_JOLT = builder.define("damageJolt", true);
        DAMAGE_JOLT_INTENSITY = builder.defineInRange("damageJoltIntensity", 0.8, 0.0, 2.0);
        MOTION_TRIGGERS = builder.define("motionTriggers", true);
        JUMP_KICK = builder.defineInRange("jumpKick", 0.75, 0.0, 2.0);
        FALL_SWAY = builder.defineInRange("fallSway", 0.65, 0.0, 2.0);
        SWIM_SWAY = builder.defineInRange("swimSway", 0.65, 0.0, 2.0);
        builder.pop();

        builder.push("damage_glass");
        CRACKS_ENABLED = builder.define("cracksEnabled", true);
        CRACK_INTENSITY = builder.defineInRange("crackIntensity", 0.9, 0.0, 1.5);
        CRACK_START_HEARTS = builder.defineInRange("crackStartHearts", 8.0, 0.5, 100.0);
        HEAVY_CRACK_HEARTS = builder.defineInRange("heavyCrackHearts", 5.0, 0.5, 100.0);
        CRITICAL_HEARTS = builder.defineInRange("criticalHearts", 3.0, 0.5, 100.0);
        CRACK_SOUND = builder.define("crackSound", true);
        CRACK_SOUND_VOLUME = builder.defineInRange("crackSoundVolume", 0.75, 0.0, 1.5);
        PAIN_VIGNETTE = builder.define("painVignette", true);
        PAIN_INTENSITY = builder.defineInRange("painIntensity", 0.8, 0.0, 1.5);
        PAIN_PULSE = builder.define("painPulse", true);
        HEARTBEAT = builder.define("lowHealthHeartbeat", true);
        HEARTBEAT_VOLUME = builder.defineInRange("heartbeatVolume", 0.8, 0.0, 1.5);
        CRITICAL_HEART_ICON = builder.define("criticalHeartIcon", true);
        RESPAWN_HEART_ICON = builder.define("respawnHeartIcon", true);
        builder.pop();

        builder.push("atmosphere");
        REAL_DARKNESS = builder.define("realDarkness", true);
        DARKNESS_STRENGTH = builder.defineInRange("darknessStrength", 0.72, 0.0, 1.0);
        NIGHT_DARKNESS = builder.defineInRange("nightDarkness", 0.42, 0.0, 1.0);
        AMBIENT_HUM = builder.define("ambientElectricalHum", true);
        AMBIENT_HUM_VOLUME = builder.defineInRange("ambientHumVolume", 0.2, 0.0, 1.0);
        RANDOM_SILENCE = builder.define("randomSilence", true);
        SILENCE_MIN_SECONDS = builder.defineInRange("silenceMinSeconds", 3, 1, 30);
        SILENCE_MAX_SECONDS = builder.defineInRange("silenceMaxSeconds", 8, 1, 60);
        SILENCE_GAP_MIN_SECONDS = builder.defineInRange("silenceGapMinSeconds", 25, 5, 600);
        SILENCE_GAP_MAX_SECONDS = builder.defineInRange("silenceGapMaxSeconds", 70, 5, 1200);
        THREAT_DETECTION = builder.define("threatDetection", true);
        THREAT_RADIUS = builder.defineInRange("threatRadius", 24.0, 4.0, 96.0);
        THREAT_HEARTBEAT = builder.define("threatHeartbeat", true);
        builder.pop();

        builder.push("immersion");
        FIRST_PERSON_BODY = builder.define("firstPersonBody", true);
        builder.pop();

        builder.push("death");
        DEATH_SEQUENCE = builder.define("deathSequence", true);
        LOST_MEDIA_FLASHES = builder.defineInRange("lostMediaFlashes", 5, 1, 12);
        BLACKOUT_TICKS = builder.defineInRange("blackoutTicks", 18, 1, 100);
        RESTART_DELAY_TICKS = builder.defineInRange("restartDelayTicks", 72, 20, 200);
        builder.pop();

        SPEC = builder.build();
    }

    private ClientConfig() {}
}
