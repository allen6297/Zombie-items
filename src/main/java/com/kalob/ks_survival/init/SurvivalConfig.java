package com.kalob.ks_survival.init;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

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

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ANIMALS = BUILDER
            .comment("List of entities that will be affected by husbandry mechanics")
            .defineList("animals", new ArrayList<>(List.of("minecraft:cow", "minecraft:pig", "minecraft:sheep", "minecraft:goat", "minecraft:chicken")),
                    entry -> entry instanceof String s && s.matches("[a-z0-9_.-]+:[a-z0-9_./-]+"));

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ANIMAL_WATER_NEEDS = BUILDER
            .comment("Water consumed per drink per entity type (format: namespace:entity=mb). Unlisted animals use the default of 250 mB.")
            .defineList("animalWaterNeeds", new ArrayList<>(List.of(
                    "minecraft:chicken=100",
                    "minecraft:cow=300",
                    "minecraft:pig=200",
                    "minecraft:sheep=200",
                    "minecraft:goat=150"
            )), entry -> entry instanceof String s && s.matches("[a-z0-9_.-]+:[a-z0-9_./-]+=\\d+"));

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean isTrackedAnimal(net.minecraft.world.entity.Entity entity) {
        String id = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE
                .getKey(entity.getType()).toString();
        return ANIMALS.get().contains(id);
    }

    public static int getDrinkAmount(net.minecraft.world.entity.Entity entity) {
        String id = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE
                .getKey(entity.getType()).toString();
        for (String entry : ANIMAL_WATER_NEEDS.get()) {
            String[] parts = entry.split("=");
            if (parts[0].equals(id)) return Integer.parseInt(parts[1]);
        }
        return 250;
    }
}
