package com.kalob.ks_survival.health;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import java.util.Optional;

public enum BodyPart implements StringRepresentable {
    HEAD("head",       10, 0.10f, true),
    TORSO("torso",     30, 0.40f, true),
    LEFT_ARM("left_arm",   15, 0.12f, false),
    RIGHT_ARM("right_arm", 15, 0.12f, false),
    LEFT_LEG("left_leg",   20, 0.13f, false),
    RIGHT_LEG("right_leg", 20, 0.13f, false);

    public final String id;
    /** Base max HP for this part. */
    public final int defaultMaxHp;
    /** Probability weight when randomly selecting a hit location. */
    public final float weight;
    /** Whether reaching 0 HP on this part is immediately lethal. */
    public final boolean lethal;

    BodyPart(String id, int defaultMaxHp, float weight, boolean lethal) {
        this.id = id;
        this.defaultMaxHp = defaultMaxHp;
        this.weight = weight;
        this.lethal = lethal;
    }

    /** Persistent MobEffect applied while this part is crippled (HP == 0, non-lethal parts only). */
    public Optional<MobEffect> crippleEffect() {
        return switch (this) {
            case LEFT_ARM, RIGHT_ARM -> Optional.of(MobEffects.WEAKNESS.value());
            case LEFT_LEG, RIGHT_LEG -> Optional.of(MobEffects.MOVEMENT_SLOWDOWN.value());
            default -> Optional.empty();
        };
    }

    @Override
    public String getSerializedName() { return id; }

    /** Select a body part from attacker→victim geometry.
     *  pitchDeg: positive = attacker looking up at victim (hits low), negative = looking down (hits high).
     *  yawDelta: attacker yaw minus victim yaw, normalised to [-180,180]. */
    public static BodyPart fromAngle(float pitchDeg, float yawDelta) {
        // Vertical zone
        if (pitchDeg < -40f) return HEAD;
        if (pitchDeg > 30f)  return (Math.abs(yawDelta) < 90) ? LEFT_LEG : RIGHT_LEG;

        // Mid-body: left vs right arm vs torso based on horizontal angle
        float absYaw = Math.abs(yawDelta);
        if (absYaw > 120f) return TORSO; // direct hit from behind → torso
        if (yawDelta < -30f) return LEFT_ARM;
        if (yawDelta >  30f) return RIGHT_ARM;
        return TORSO;
    }
}
