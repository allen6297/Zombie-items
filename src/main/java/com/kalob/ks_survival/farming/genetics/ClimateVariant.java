package com.kalob.ks_survival.farming.genetics;

import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;

public enum ClimateVariant {
    TEMPERATE(0),
    COLD(1),
    ARID(2),
    TROPICAL(3);

    public final int id;

    ClimateVariant(int id) { this.id = id; }

    public static ClimateVariant byId(int id) {
        for (ClimateVariant v : values()) if (v.id == id) return v;
        return TEMPERATE;
    }

    public static ClimateVariant fromBiome(Holder<Biome> biome) {
        float temp = biome.value().getBaseTemperature();
        if (temp <= 0.2f) return COLD;
        if (biome.is(BiomeTags.IS_JUNGLE)) return TROPICAL;
        if (temp >= 1.5f) return ARID;
        return TEMPERATE;
    }

    public String displayName() {
        return switch (this) {
            case COLD -> "Cold";
            case ARID -> "Arid";
            case TROPICAL -> "Tropical";
            default -> "Temperate";
        };
    }
}
