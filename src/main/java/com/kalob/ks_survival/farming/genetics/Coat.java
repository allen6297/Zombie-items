package com.kalob.ks_survival.farming.genetics;

import net.minecraft.util.RandomSource;

public enum Coat {
    NORMAL(0),   // dominant
    DARK(1),     // semi-dominant — darker, muted tones
    CREAM(2),    // recessive — warm cream/light
    ALBINO(3);   // most recessive — near white/pink

    public final int id;

    Coat(int id) { this.id = id; }

    public static Coat byId(int id) {
        return switch (id) {
            case 1 -> DARK;
            case 2 -> CREAM;
            case 3 -> ALBINO;
            default -> NORMAL;
        };
    }

    /** Lower ordinal = more dominant. */
    public static Coat expressed(Coat a, Coat b) {
        return a.ordinal() <= b.ordinal() ? a : b;
    }

    public static Coat random(RandomSource rng) {
        int roll = rng.nextInt(100);
        if (roll < 5)  return ALBINO;   // 5%
        if (roll < 18) return CREAM;    // 13%
        if (roll < 38) return DARK;     // 20%
        return NORMAL;                  // 62%
    }

    /** ARGB32 overlay color for the render layer. */
    public int getARGB() {
        return switch (this) {
            case DARK   -> argb(155, 45, 30, 15);
            case CREAM  -> argb(110, 230, 205, 150);
            case ALBINO -> argb(175, 245, 235, 225);
            default     -> 0;
        };
    }

    private static int argb(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
