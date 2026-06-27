package com.kalob.ks_survival.init;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SurvivalConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Animal needs
    public static final ModConfigSpec.IntValue ANIMAL_TICK_INTERVAL = BUILDER
            .comment("How often animal hunger/thirst decrements in ticks (20 ticks = 1 second)")
            .defineInRange("animalTickInterval", 1200, 20, 2000);

    public static final ModConfigSpec.IntValue STRESS_THRESHOLD = BUILDER
            .comment("Hunger/thirst level below which an animal becomes stressed (0-100)")
            .defineInRange("stressThreshold", 20, 0, 100);

    public static final ModConfigSpec.IntValue WELL_FED_THRESHOLD = BUILDER
            .comment("Hunger/thirst level above which an animal is considered well-fed (0-100)")
            .defineInRange("wellFedThreshold", 70, 0, 100);

    // Drops
    public static final ModConfigSpec.DoubleValue BONUS_DROP_CHANCE = BUILDER
            .comment("Chance for each drop to be duplicated when killing a well-fed animal or using a butcher knife (0.0-1.0)")
            .defineInRange("bonusDropChance", 0.5, 0.0, 1.0);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
