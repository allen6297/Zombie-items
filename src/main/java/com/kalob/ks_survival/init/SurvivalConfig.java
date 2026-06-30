package com.kalob.ks_survival.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SurvivalConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Animal needs
    public static final ModConfigSpec.IntValue ANIMAL_TICK_INTERVAL = BUILDER
            .comment("How often animal hunger/thirst decrements in ticks (20 ticks = 1 second)")
            .translation("config.ks_survival.animalTickInterval")
            .defineInRange("animalTickInterval", 200, 20, 6000);

    public static final ModConfigSpec.IntValue STRESS_THRESHOLD = BUILDER
            .comment("Hunger/thirst level below which an animal becomes stressed (0-100)")
            .translation("config.ks_survival.stressThreshold")
            .defineInRange("stressThreshold", 20, 0, 100);

    public static final ModConfigSpec.IntValue WELL_FED_THRESHOLD = BUILDER
            .comment("Hunger/thirst level above which an animal is considered well-fed (0-100)")
            .translation("config.ks_survival.wellFedThreshold")
            .defineInRange("wellFedThreshold", 70, 0, 100);

    // Productivity
    public static final ModConfigSpec.IntValue PRODUCTIVITY_CAP_TICKS = BUILDER
            .comment("Well-fed ticks required to reach 100% productivity")
            .translation("config.ks_survival.productivityCapTicks")
            .defineInRange("productivityCapTicks", 2000, 100, 100000);

    // Crowding
    public static final ModConfigSpec.IntValue MUTATION_CHANCE = BUILDER
            .comment("Percent chance (0-100) that a genetics allele mutates to a random value on birth")
            .translation("config.ks_survival.mutationChance")
            .defineInRange("mutationChance", 5, 0, 100);

    public static final ModConfigSpec.IntValue FLEE_RADIUS = BUILDER
            .comment("Distance (blocks) at which wild/panicking animals flee nearby players")
            .translation("config.ks_survival.fleeRadius")
            .defineInRange("fleeRadius", 5, 1, 32);

    public static final ModConfigSpec.IntValue CROWDING_LIMIT = BUILDER
            .comment("Max animals of the same type within radius before crowding stress applies")
            .translation("config.ks_survival.crowdingLimit")
            .defineInRange("crowdingLimit", 4, 1, 50);

    public static final ModConfigSpec.IntValue CROWDING_RADIUS = BUILDER
            .comment("Block radius checked for crowding")
            .translation("config.ks_survival.crowdingRadius")
            .defineInRange("crowdingRadius", 8, 1, 32);

    // Illness
    public static final ModConfigSpec.IntValue SICKNESS_THRESHOLD = BUILDER
            .comment("Consecutive stressed ticks before an animal becomes sick")
            .translation("config.ks_survival.sicknessThreshold")
            .defineInRange("sicknessThreshold", 2000, 20, 100000);

    // Tameness
    public static final ModConfigSpec.IntValue TAMENESS_WILD_THRESHOLD = BUILDER
            .comment("Tameness level below which an animal is considered wild (0-100)")
            .translation("config.ks_survival.tamenessWildThreshold")
            .defineInRange("tamenessWildThreshold", 25, 0, 100);

    public static final ModConfigSpec.IntValue TAMENESS_DOMESTIC_THRESHOLD = BUILDER
            .comment("Tameness level above which an animal is considered domestic (0-100)")
            .translation("config.ks_survival.tamenessDomesticThreshold")
            .defineInRange("tamenessDomesticThreshold", 60, 0, 100);

    // Overfeeding
    public static final ModConfigSpec.IntValue OVERFEEDING_THRESHOLD = BUILDER
            .comment("Ticks at max hunger and thirst before overfeeding penalty applies")
            .translation("config.ks_survival.overfeedingThreshold")
            .defineInRange("overfeedingThreshold", 2000, 100, 100000);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ANIMALS = BUILDER
            .comment("List of entities that will be affected by husbandry mechanics")
            .translation("config.ks_survival.animals")
            .defineList("animals", new ArrayList<>(List.of("minecraft:cow", "minecraft:pig", "minecraft:sheep", "minecraft:goat", "minecraft:chicken", "ks_survival:aurochs")),
                    entry -> entry instanceof String s && s.matches("[a-z0-9_.-]+:[a-z0-9_./-]+"));

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ANIMAL_DIET = BUILDER
            .comment("Food tags each animal will eat from the food trough (format: namespace:entity=tag1,tag2)")
            .translation("config.ks_survival.animalDiet")
            .defineList("animalDiet", new ArrayList<>(List.of(
                    "minecraft:cow=c:foods/vegetable,c:crops",
                    "minecraft:pig=c:foods/vegetable,c:foods/fruit,c:crops",
                    "minecraft:sheep=c:foods/vegetable,c:crops",
                    "minecraft:goat=c:foods/vegetable,c:crops",
                    "minecraft:chicken=c:seeds,c:foods/berry"
            )), entry -> entry instanceof String s && s.matches("[a-z0-9_.-]+:[a-z0-9_./-]+=.+"));

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ANIMAL_BREEDING_SEASONS = BUILDER
            .comment("Seasons each animal can breed in (format: namespace:entity=SPRING,SUMMER). Domestic animals ignore this restriction. Valid seasons: SPRING, SUMMER, AUTUMN, WINTER. Unlisted animals can breed year-round.")
            .translation("config.ks_survival.animalBreedingSeasons")
            .defineList("animalBreedingSeasons", new ArrayList<>(List.of(
                    "minecraft:cow=SPRING,SUMMER",
                    "minecraft:pig=SPRING,SUMMER,AUTUMN",
                    "minecraft:sheep=SPRING",
                    "minecraft:goat=AUTUMN,WINTER",
                    "minecraft:chicken=SPRING,SUMMER"
            )), entry -> entry instanceof String s && s.matches("[a-z0-9_.-]+:[a-z0-9_./-]+=.+"));

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ANIMAL_WATER_NEEDS = BUILDER
            .comment("Water consumed per drink per entity type (format: namespace:entity=mb). Unlisted animals use the default of 250 mB.")
            .translation("config.ks_survival.animalWaterNeeds")
            .defineList("animalWaterNeeds", new ArrayList<>(List.of(
                    "minecraft:chicken=100",
                    "minecraft:cow=300",
                    "minecraft:pig=200",
                    "minecraft:sheep=200",
                    "minecraft:goat=150"
            )), entry -> entry instanceof String s && s.matches("[a-z0-9_.-]+:[a-z0-9_./-]+=\\d+"));

    // Bandage
    public static final ModConfigSpec.DoubleValue BANDAGE_HEAL_AMOUNT = BUILDER
            .comment("How much health the bandage restores")
            .translation("config.ks_survival.bandageHealAmount")
            .defineInRange("bandageHealAmount", 6.0, 0.5, 20.0);

    public static final ModConfigSpec.IntValue BANDAGE_USE_DURATION = BUILDER
            .comment("How long it takes to apply a bandage in ticks (20 ticks = 1 second)")
            .translation("config.ks_survival.bandageUseDuration")
            .defineInRange("bandageUseDuration", 60, 20, 200);

    // Body-part health
    public static final ModConfigSpec.IntValue BLEED_INTERVAL = BUILDER
            .comment("How often bleeding damage is applied, in ticks (20 ticks = 1 second)")
            .translation("config.ks_survival.bleedInterval")
            .defineInRange("bleedInterval", 40, 10, 1200);

    public static final ModConfigSpec.DoubleValue BLEED_DAMAGE = BUILDER
            .comment("Vanilla health damage dealt by normal bleeding each bleed interval")
            .translation("config.ks_survival.bleedDamage")
            .defineInRange("bleedDamage", 1.0, 0.0, 20.0);

    public static final ModConfigSpec.DoubleValue SEVERE_BLEED_DAMAGE = BUILDER
            .comment("Vanilla health damage dealt by severe bleeding each bleed interval")
            .translation("config.ks_survival.severeBleedDamage")
            .defineInRange("severeBleedDamage", 2.0, 0.0, 20.0);

    public static final ModConfigSpec.DoubleValue HEAD_DAMAGE_MULTIPLIER = BUILDER
            .comment("Multiplier applied to vanilla damage when the routed body part is the head")
            .translation("config.ks_survival.headDamageMultiplier")
            .defineInRange("headDamageMultiplier", 2.0, 1.0, 10.0);

    public static final ModConfigSpec.IntValue FALL_MOVEMENT_LOCK_TICKS = BUILDER
            .comment("Movement lock duration after taking fall damage, in ticks")
            .translation("config.ks_survival.fallMovementLockTicks")
            .defineInRange("fallMovementLockTicks", 15 * 20, 0, 20 * 120);

    public static final ModConfigSpec.IntValue NATURAL_HEAL_INTERVAL = BUILDER
            .comment("How often natural body-part recovery runs, in ticks")
            .translation("config.ks_survival.naturalHealInterval")
            .defineInRange("naturalHealInterval", 20 * 30, 20, 20 * 600);

    public static final ModConfigSpec.IntValue NATURAL_HEAL_AMOUNT = BUILDER
            .comment("Body-part HP restored by each natural recovery tick")
            .translation("config.ks_survival.naturalHealAmount")
            .defineInRange("naturalHealAmount", 1, 0, 20);

    public static final ModConfigSpec.IntValue NATURAL_HEAL_MIN_FOOD = BUILDER
            .comment("Minimum food level required for natural body-part recovery")
            .translation("config.ks_survival.naturalHealMinFood")
            .defineInRange("naturalHealMinFood", 16, 0, 20);

    public static final ModConfigSpec.IntValue HEAD_MAX_HP = BUILDER
            .comment("Maximum body-part HP for the head")
            .translation("config.ks_survival.headMaxHp")
            .defineInRange("headMaxHp", 10, 1, 100);

    public static final ModConfigSpec.IntValue TORSO_MAX_HP = BUILDER
            .comment("Maximum body-part HP for the torso")
            .translation("config.ks_survival.torsoMaxHp")
            .defineInRange("torsoMaxHp", 30, 1, 200);

    public static final ModConfigSpec.IntValue ARM_MAX_HP = BUILDER
            .comment("Maximum body-part HP for each arm")
            .translation("config.ks_survival.armMaxHp")
            .defineInRange("armMaxHp", 15, 1, 100);

    public static final ModConfigSpec.IntValue LEG_MAX_HP = BUILDER
            .comment("Maximum body-part HP for each leg")
            .translation("config.ks_survival.legMaxHp")
            .defineInRange("legMaxHp", 20, 1, 120);

    public static final ModConfigSpec.IntValue TRAUMA_KIT_USE_DURATION = BUILDER
            .comment("How long it takes to apply a trauma kit in ticks")
            .translation("config.ks_survival.traumaKitUseDuration")
            .defineInRange("traumaKitUseDuration", 120, 20, 600);

    public static final ModConfigSpec.IntValue TRAUMA_KIT_HEAL_AMOUNT = BUILDER
            .comment("Body-part HP restored to all parts by a trauma kit")
            .translation("config.ks_survival.traumaKitHealAmount")
            .defineInRange("traumaKitHealAmount", 10, 1, 100);

    public static final ModConfigSpec.DoubleValue BODY_ARMOR_DAMAGE_MULTIPLIER = BUILDER
            .comment("Multiplier applied to body-part damage when matching armor is equipped")
            .translation("config.ks_survival.bodyArmorDamageMultiplier")
            .defineInRange("bodyArmorDamageMultiplier", 0.65, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue BODY_ARMOR_DURABILITY_DAMAGE_RATIO = BUILDER
            .comment("How much durability matching armor loses relative to body-part damage")
            .translation("config.ks_survival.bodyArmorDurabilityDamageRatio")
            .defineInRange("bodyArmorDurabilityDamageRatio", 0.5, 0.0, 5.0);

    public static final ModConfigSpec.IntValue PAIN_SHOCK_CONFUSION_TICKS = BUILDER
            .comment("Nausea duration after a body part drops to critical HP, in ticks")
            .translation("config.ks_survival.painShockConfusionTicks")
            .defineInRange("painShockConfusionTicks", 80, 0, 1200);

    public static final ModConfigSpec.IntValue PAIN_SHOCK_DARKNESS_TICKS = BUILDER
            .comment("Darkness duration after a body part drops to critical HP, in ticks")
            .translation("config.ks_survival.painShockDarknessTicks")
            .defineInRange("painShockDarknessTicks", 40, 0, 1200);

    public static final ModConfigSpec.DoubleValue DOMESTIC_ANIMAL_KICK_CHANCE = BUILDER
            .comment("Chance that a domestic tracked animal kicks the player when hurt")
            .translation("config.ks_survival.domesticAnimalKickChance")
            .defineInRange("domesticAnimalKickChance", 0.35, 0.0, 1.0);

    public static final ModConfigSpec.IntValue DOMESTIC_ANIMAL_KICK_DAMAGE = BUILDER
            .comment("Body-part damage dealt to a player's leg by domestic animal retaliation")
            .translation("config.ks_survival.domesticAnimalKickDamage")
            .defineInRange("domesticAnimalKickDamage", 4, 0, 50);

    // Cans
    public static final ModConfigSpec.IntValue CAN1_THIRST = BUILDER
            .comment("Thirst restored by the small can")
            .translation("config.ks_survival.can1Thirst")
            .defineInRange("can1Thirst", 3, 1, 10);

    public static final ModConfigSpec.IntValue CAN1_QUENCH = BUILDER
            .comment("Quench value of the small can")
            .translation("config.ks_survival.can1Quench")
            .defineInRange("can1Quench", 1, 0, 5);

    public static final ModConfigSpec.IntValue CAN2_THIRST = BUILDER
            .comment("Thirst restored by the big can")
            .translation("config.ks_survival.can2Thirst")
            .defineInRange("can2Thirst", 6, 1, 10);

    public static final ModConfigSpec.IntValue CAN2_QUENCH = BUILDER
            .comment("Quench value of the big can")
            .translation("config.ks_survival.can2Quench")
            .defineInRange("can2Quench", 2, 0, 5);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private static volatile Map<String, Integer> drinkAmountCache = null;
    private static volatile Set<String> trackedAnimalCache = null;
    private static volatile Map<String, Set<TagKey<Item>>> dietCache = null;
    private static volatile Set<TagKey<Item>> allDietTagsCache = null;
    private static volatile Map<String, Set<String>> breedingSeasonCache = null;

    public static void invalidateCache() {
        drinkAmountCache = null;
        trackedAnimalCache = null;
        dietCache = null;
        allDietTagsCache = null;
        breedingSeasonCache = null;
    }

    /** Union of every diet tag across all configured animals — used for trough slot validation. */
    public static Set<TagKey<Item>> getAllDietTags() {
        if (allDietTagsCache == null) {
            Set<TagKey<Item>> all = new HashSet<>();
            for (String entry : ANIMAL_DIET.get()) {
                String[] parts = entry.split("=", 2);
                if (parts.length != 2) continue;
                Arrays.stream(parts[1].split(","))
                        .map(String::trim)
                        .map(tag -> ItemTags.create(ResourceLocation.parse(tag)))
                        .forEach(all::add);
            }
            allDietTagsCache = all;
        }
        return allDietTagsCache;
    }

    private static Map<String, Integer> drinkAmounts() {
        if (drinkAmountCache == null) {
            Map<String, Integer> map = new HashMap<>();
            for (String entry : ANIMAL_WATER_NEEDS.get()) {
                String[] parts = entry.split("=", 2);
                if (parts.length == 2) map.put(parts[0], Integer.parseInt(parts[1]));
            }
            drinkAmountCache = map;
        }
        return drinkAmountCache;
    }

    public static boolean isTrackedAnimal(net.minecraft.world.entity.Entity entity) {
        Set<String> cache = trackedAnimalCache;
        if (cache == null) {
            Set<String> built = new HashSet<>();
            for (String s : ANIMALS.get()) built.add(s);
            trackedAnimalCache = cache = built;
        }
        String id = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE
                .getKey(entity.getType()).toString();
        return cache.contains(id);
    }

    public static Set<TagKey<Item>> getDietTags(net.minecraft.world.entity.Entity entity) {
        if (dietCache == null) {
            Map<String, Set<TagKey<Item>>> map = new HashMap<>();
            for (String entry : ANIMAL_DIET.get()) {
                String[] parts = entry.split("=", 2);
                if (parts.length != 2) continue;
                Set<TagKey<Item>> tags = Arrays.stream(parts[1].split(","))
                        .map(String::trim)
                        .map(tag -> ItemTags.create(ResourceLocation.parse(tag)))
                        .collect(Collectors.toSet());
                map.put(parts[0].trim(), tags);
            }
            dietCache = map;
        }
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
        return dietCache.getOrDefault(id, Set.of());
    }

    public static Set<String> getBreedingSeasons(net.minecraft.world.entity.Entity entity) {
        if (breedingSeasonCache == null) {
            Map<String, Set<String>> map = new HashMap<>();
            for (String entry : ANIMAL_BREEDING_SEASONS.get()) {
                String[] parts = entry.split("=", 2);
                if (parts.length != 2) continue;
                Set<String> seasons = Arrays.stream(parts[1].split(","))
                        .map(String::trim).map(String::toUpperCase)
                        .collect(Collectors.toSet());
                map.put(parts[0].trim(), seasons);
            }
            breedingSeasonCache = map;
        }
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
        return breedingSeasonCache.getOrDefault(id, Set.of());
    }

    public static int getDrinkAmount(net.minecraft.world.entity.Entity entity) {
        String id = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE
                .getKey(entity.getType()).toString();
        return drinkAmounts().getOrDefault(id, 250);
    }
}
