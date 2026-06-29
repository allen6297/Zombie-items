package com.kalob.ks_survival.farming.genetics;

import net.minecraft.util.RandomSource;

public enum Pattern {
    SOLID(0),    // dominant — no markings
    SPOTTED(1),  // semi-dominant
    STRIPED(2),  // semi-dominant
    PATCHY(3);   // most recessive — large irregular patches

    public final int id;

    Pattern(int id) { this.id = id; }

    public static Pattern byId(int id) {
        for (Pattern p : values()) if (p.id == id) return p;
        return SOLID;
    }

    // SOLID is dominant — lower ordinal wins
    public static Pattern expressed(Pattern a, Pattern b) {
        return a.ordinal() <= b.ordinal() ? a : b;
    }

    public static Pattern random(RandomSource rng, ClimateVariant climate) {
        int r = rng.nextInt(100);
        return switch (climate) {
            case COLD     -> r < 70 ? SOLID : r < 88 ? SPOTTED : r < 97 ? STRIPED : PATCHY;
            case ARID     -> r < 50 ? SOLID : r < 72 ? SPOTTED : r < 88 ? STRIPED : PATCHY;
            case TROPICAL -> r < 40 ? SOLID : r < 65 ? SPOTTED : r < 85 ? STRIPED : PATCHY;
            default       -> r < 60 ? SOLID : r < 85 ? SPOTTED : r < 95 ? STRIPED : PATCHY;
        };
    }

    // Distribution: 60% SOLID, 25% SPOTTED, 10% STRIPED, 5% PATCHY
    public static Pattern random(RandomSource rng) {
        int r = rng.nextInt(100);
        if (r < 60) return SOLID;
        if (r < 85) return SPOTTED;
        if (r < 95) return STRIPED;
        return PATCHY;
    }

    // Semi-transparent dark overlay; the texture shape defines where markings appear
    public int getARGB() {
        return switch (this) {
            case SPOTTED -> argb(160, 45, 28, 12);
            case STRIPED -> argb(150, 30, 20, 10);
            case PATCHY  -> argb(170, 25, 15, 8);
            default      -> 0;
        };
    }

    public String displayName() {
        return switch (this) {
            case SPOTTED -> "Spotted";
            case STRIPED -> "Striped";
            case PATCHY  -> "Patchy";
            default      -> "";
        };
    }

    private static int argb(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
