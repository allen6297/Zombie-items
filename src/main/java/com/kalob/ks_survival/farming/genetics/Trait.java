package com.kalob.ks_survival.farming.genetics;

import net.minecraft.util.RandomSource;

public enum Trait {
    NONE(0),
    HARDY(1),    // dominant: resists stress buildup
    FECUND(2),   // dominant: offspring grow faster
    GLUTTONY(3); // recessive: drains hunger twice as fast, but also feeds twice as fast

    public final int id;

    Trait(int id) { this.id = id; }

    public static Trait byId(int id) {
        for (Trait t : values()) if (t.id == id) return t;
        return NONE;
    }

    // NONE is always recessive — any non-NONE trait shows over it.
    // Among two non-NONE traits, lower ordinal wins (HARDY > FECUND > GLUTTONY).
    public static Trait expressed(Trait a, Trait b) {
        if (a == NONE) return b;
        if (b == NONE) return a;
        return a.ordinal() <= b.ordinal() ? a : b;
    }

    public static Trait random(RandomSource rng, ClimateVariant climate) {
        int r = rng.nextInt(100);
        return switch (climate) {
            case COLD     -> r < 20 ? NONE : r < 70 ? HARDY  : r < 90 ? FECUND : GLUTTONY;
            case ARID     -> r < 40 ? NONE : r < 60 ? HARDY  : r < 75 ? FECUND : GLUTTONY;
            case TROPICAL -> r < 30 ? NONE : r < 50 ? HARDY  : r < 85 ? FECUND : GLUTTONY;
            default       -> r < 50 ? NONE : r < 80 ? HARDY  : r < 95 ? FECUND : GLUTTONY;
        };
    }

    // Distribution: 50% NONE, 30% HARDY, 15% FECUND, 5% GLUTTONY
    public static Trait random(RandomSource rng) {
        int r = rng.nextInt(100);
        if (r < 50) return NONE;
        if (r < 80) return HARDY;
        if (r < 95) return FECUND;
        return GLUTTONY;
    }

    public String displayName() {
        return switch (this) {
            case HARDY -> "Hardy";
            case FECUND -> "Fecund";
            case GLUTTONY -> "Gluttony";
            default -> "";
        };
    }
}
