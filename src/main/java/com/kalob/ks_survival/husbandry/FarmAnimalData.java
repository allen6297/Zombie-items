package com.kalob.ks_survival.husbandry;

import com.kalob.ks_survival.husbandry.genetics.ClimateVariant;
import com.kalob.ks_survival.husbandry.genetics.Coat;
import com.kalob.ks_survival.husbandry.genetics.Gender;
import com.kalob.ks_survival.husbandry.genetics.Pattern;
import com.kalob.ks_survival.husbandry.genetics.Trait;
import com.kalob.ks_survival.init.SurvivalConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

public class FarmAnimalData {

    public static final int MAX = 100;

    public static int tickInterval()     { return SurvivalConfig.ANIMAL_TICK_INTERVAL.get(); }
    public static int stressThreshold()  { return SurvivalConfig.STRESS_THRESHOLD.get(); }
    public static int wellFedThreshold() { return SurvivalConfig.WELL_FED_THRESHOLD.get(); }

    public static final Codec<FarmAnimalData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("hunger").forGetter(d -> d.hunger),
            Codec.INT.fieldOf("thirst").forGetter(d -> d.thirst),
            Codec.INT.optionalFieldOf("wellFedTicks", 0).forGetter(d -> d.wellFedTicks),
            Codec.INT.optionalFieldOf("stressTicks", 0).forGetter(d -> d.stressTicks),
            Codec.INT.optionalFieldOf("overfedTicks", 0).forGetter(d -> d.overfedTicks),
            Codec.INT.optionalFieldOf("tameness", 0).forGetter(d -> d.tameness),
            Codec.INT.optionalFieldOf("alleleA", 0).forGetter(d -> d.alleleA),
            Codec.INT.optionalFieldOf("alleleB", 0).forGetter(d -> d.alleleB),
            Codec.INT.optionalFieldOf("traitAlleleA", 0).forGetter(d -> d.traitAlleleA),
            Codec.INT.optionalFieldOf("traitAlleleB", 0).forGetter(d -> d.traitAlleleB),
            Codec.INT.optionalFieldOf("patternAlleleA", 0).forGetter(d -> d.patternAlleleA),
            Codec.INT.optionalFieldOf("patternAlleleB", 0).forGetter(d -> d.patternAlleleB),
            Codec.INT.optionalFieldOf("gender", 0).forGetter(d -> d.gender),
            Codec.INT.optionalFieldOf("climateVariant", 0).forGetter(d -> d.climateVariant),
            Codec.BOOL.optionalFieldOf("geneticsInitialized", false).forGetter(d -> d.geneticsInitialized)
        ).apply(instance, FarmAnimalData::fromCodec)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, FarmAnimalData> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    private int hunger;
    private int thirst;
    private int wellFedTicks;
    private int stressTicks;
    private int overfedTicks;
    private int tameness;
    private int panicTicks;
    private int alleleA;
    private int alleleB;
    private int traitAlleleA;
    private int traitAlleleB;
    private int patternAlleleA;
    private int patternAlleleB;
    private int gender;
    private int climateVariant;
    private boolean geneticsInitialized;

    public FarmAnimalData() {
        this.hunger = MAX;
        this.thirst = MAX;
        this.wellFedTicks = 0;
        this.stressTicks = 0;
        this.overfedTicks = 0;
        this.tameness = 0;
        this.panicTicks = 0;
        this.geneticsInitialized = false;
    }

    public static FarmAnimalData of(int hunger, int thirst, int wellFedTicks, int stressTicks, int overfedTicks,
                                    int tameness, int panicTicks, int alleleA, int alleleB,
                                    int traitAlleleA, int traitAlleleB, int patternAlleleA, int patternAlleleB,
                                    int gender, int climateVariant, boolean geneticsInitialized) {
        FarmAnimalData d = new FarmAnimalData();
        d.hunger = hunger;
        d.thirst = thirst;
        d.wellFedTicks = wellFedTicks;
        d.stressTicks = stressTicks;
        d.overfedTicks = overfedTicks;
        d.tameness = tameness;
        d.panicTicks = panicTicks;
        d.alleleA = alleleA;
        d.alleleB = alleleB;
        d.traitAlleleA = traitAlleleA;
        d.traitAlleleB = traitAlleleB;
        d.patternAlleleA = patternAlleleA;
        d.patternAlleleB = patternAlleleB;
        d.gender = gender;
        d.climateVariant = climateVariant;
        d.geneticsInitialized = geneticsInitialized;
        return d;
    }

    private static FarmAnimalData fromCodec(int hunger, int thirst, int wellFedTicks, int stressTicks, int overfedTicks,
                                            int tameness, int alleleA, int alleleB,
                                            int traitAlleleA, int traitAlleleB, int patternAlleleA, int patternAlleleB,
                                            int gender, int climateVariant, boolean geneticsInitialized) {
        return of(hunger, thirst, wellFedTicks, stressTicks, overfedTicks, tameness, 0,
                alleleA, alleleB, traitAlleleA, traitAlleleB, patternAlleleA, patternAlleleB, gender,
                climateVariant, geneticsInitialized);
    }

    public int getHunger()       { return hunger; }
    public int getThirst()       { return thirst; }
    public int getWellFedTicks() { return wellFedTicks; }
    public int getStressTicks()  { return stressTicks; }
    public int getOverfedTicks() { return overfedTicks; }
    public int getTameness()     { return tameness; }
    public int getPanicTicks()   { return panicTicks; }
    public int getAlleleA()        { return alleleA; }
    public int getAlleleB()        { return alleleB; }
    public int getTraitAlleleA()    { return traitAlleleA; }
    public int getTraitAlleleB()    { return traitAlleleB; }
    public int getPatternAlleleA()  { return patternAlleleA; }
    public int getPatternAlleleB()  { return patternAlleleB; }
    public int getGender()                       { return gender; }
    public Gender getExpressedGender()           { return Gender.byId(gender); }
    public int getClimateVariant()               { return climateVariant; }
    public ClimateVariant getExpressedClimate()  { return ClimateVariant.byId(climateVariant); }
    public boolean hasInitializedGenetics()      { return geneticsInitialized; }

    public boolean hasStoredGenetics() {
        return alleleA != 0 || alleleB != 0
                || traitAlleleA != 0 || traitAlleleB != 0
                || patternAlleleA != 0 || patternAlleleB != 0
                || gender != 0 || climateVariant != 0;
    }

    public void markGeneticsInitialized() {
        this.geneticsInitialized = true;
    }

    public Coat getExpressedCoat() {
        return Coat.expressed(Coat.byId(alleleA), Coat.byId(alleleB));
    }

    public Trait getExpressedTrait() {
        return Trait.expressed(Trait.byId(traitAlleleA), Trait.byId(traitAlleleB));
    }

    public Pattern getExpressedPattern() {
        return Pattern.expressed(Pattern.byId(patternAlleleA), Pattern.byId(patternAlleleB));
    }

    public void setRandomAlleles(RandomSource rng, ClimateVariant climate) {
        this.climateVariant = climate.id;
        this.alleleA = Coat.random(rng, climate).id;
        this.alleleB = Coat.random(rng, climate).id;
        this.traitAlleleA = Trait.random(rng, climate).id;
        this.traitAlleleB = Trait.random(rng, climate).id;
        this.patternAlleleA = Pattern.random(rng, climate).id;
        this.patternAlleleB = Pattern.random(rng, climate).id;
        this.gender = Gender.random(rng).id;
        this.geneticsInitialized = true;
    }

    public void inheritGenetics(FarmAnimalData parentA, FarmAnimalData parentB, RandomSource rng) {
        float mutationChance = SurvivalConfig.MUTATION_CHANCE.get() / 100f;

        // Coat
        Coat coatA = rng.nextBoolean() ? Coat.byId(parentA.alleleA) : Coat.byId(parentA.alleleB);
        Coat coatB = rng.nextBoolean() ? Coat.byId(parentB.alleleA) : Coat.byId(parentB.alleleB);
        this.alleleA = (rng.nextFloat() < mutationChance) ? Coat.random(rng).id : coatA.id;
        this.alleleB = (rng.nextFloat() < mutationChance) ? Coat.random(rng).id : coatB.id;

        // Trait
        Trait traitA = rng.nextBoolean() ? Trait.byId(parentA.traitAlleleA) : Trait.byId(parentA.traitAlleleB);
        Trait traitB = rng.nextBoolean() ? Trait.byId(parentB.traitAlleleA) : Trait.byId(parentB.traitAlleleB);
        this.traitAlleleA = (rng.nextFloat() < mutationChance) ? Trait.random(rng).id : traitA.id;
        this.traitAlleleB = (rng.nextFloat() < mutationChance) ? Trait.random(rng).id : traitB.id;

        // Pattern
        Pattern patA = rng.nextBoolean() ? Pattern.byId(parentA.patternAlleleA) : Pattern.byId(parentA.patternAlleleB);
        Pattern patB = rng.nextBoolean() ? Pattern.byId(parentB.patternAlleleA) : Pattern.byId(parentB.patternAlleleB);
        this.patternAlleleA = (rng.nextFloat() < mutationChance) ? Pattern.random(rng).id : patA.id;
        this.patternAlleleB = (rng.nextFloat() < mutationChance) ? Pattern.random(rng).id : patB.id;
        this.gender = Gender.random(rng).id;
        this.climateVariant = parentA.climateVariant;
        this.geneticsInitialized = true;
    }

    public boolean isPanicking() { return panicTicks > 0; }

    public void panic(int ticks) { this.panicTicks = Math.max(this.panicTicks, ticks); }

    /** Panic for the given number of real game ticks, converted to tick-interval units. */
    public void panicFor(int gameTicks) {
        panic(Math.max(1, gameTicks / tickInterval()));
    }

    public boolean feed()  {
        if (hunger >= MAX) return false;
        this.hunger = MAX;
        return true;
    }

    public boolean water() {
        if (thirst >= MAX) return false;
        this.thirst = MAX;
        return true;
    }

    public void cure() {
        this.stressTicks = 0;
    }

    public void infect() {
        this.stressTicks = Math.max(this.stressTicks, SurvivalConfig.SICKNESS_THRESHOLD.get());
    }

    public boolean isWild()       { return tameness <= SurvivalConfig.TAMENESS_WILD_THRESHOLD.get(); }
    public boolean isAdjusting()  { return !isWild() && !isDomestic(); }
    public boolean isDomestic()   { return tameness > SurvivalConfig.TAMENESS_DOMESTIC_THRESHOLD.get(); }

    public int getTamenessCap() {
        if (isWild())      return 50;
        if (isAdjusting()) return 75;
        return 100;
    }

    /** Returns 0–100 representing how productive this animal currently is. */
    public int getProductivity() {
        int base;
        if (isOverfed()) {
            int excess = overfedTicks - SurvivalConfig.OVERFEEDING_THRESHOLD.get();
            base = Math.max(0, 100 - excess / 20);
        } else {
            base = Math.min(100, wellFedTicks * 100 / SurvivalConfig.PRODUCTIVITY_CAP_TICKS.get());
        }
        return Math.min(base, getTamenessCap());
    }

    public void damageTameness(int amount) {
        this.tameness = Math.max(0, this.tameness - amount);
    }

    public void tickTameness(boolean wellCared) {
        if (wellCared) {
            if (tameness < MAX) tameness = Math.min(MAX, tameness + 1);
        } else {
            // slow decay when neglected — only if not already wild
            if (tameness > 0) tameness = Math.max(0, tameness - 1);
        }
    }

    public void inheritTameness(FarmAnimalData parentA, FarmAnimalData parentB) {
        this.tameness = Math.min(MAX, (parentA.tameness + parentB.tameness) / 2);
    }

    // Base interval (ticks) that the drain values of 1 are calibrated for.
    // At larger tickIntervals the drain scales up so real-time depletion stays constant.
    private static final int BASE_INTERVAL = 200;

    public void tick(boolean nearWater, int extraHungerDrain, int extraThirstDrain, boolean safetyBonus) {
        if (panicTicks > 0) panicTicks--;
        int elapsedTicks = tickInterval();
        boolean fullyFedBeforeDrain = hunger >= MAX && thirst >= MAX;
        int drain = Math.max(1, tickInterval() / BASE_INTERVAL);
        // GLUTTONY animals drain (and eat) twice as fast
        int hungerDrain = getExpressedTrait() == Trait.GLUTTONY ? drain * 2 : drain;
        hunger = Math.max(0, hunger - hungerDrain - extraHungerDrain);
        // Thirst restored passively by natural water (rivers/ponds) or by SeekWaterTroughGoal
        if (nearWater) { if (thirst < MAX) thirst = Math.min(MAX, thirst + drain); }
        else { thirst = Math.max(0, thirst - drain - extraThirstDrain); }

        if (isWellFed()) {
            wellFedTicks += elapsedTicks;
            stressTicks = 0;
            if (fullyFedBeforeDrain) {
                overfedTicks += elapsedTicks;
            } else {
                overfedTicks = 0;
            }
        } else if (isStressed() && !safetyBonus) {
            wellFedTicks = 0;
            stressTicks += elapsedTicks;
            overfedTicks = 0;
        } else {
            overfedTicks = 0;
        }
    }

    public boolean isHungry() {
        return hunger < stressThreshold();
    }

    public boolean isThirsty() {
        return thirst < stressThreshold();
    }

    public boolean isStressed() {
        return hunger < stressThreshold() || thirst < stressThreshold();
    }

    public boolean isWellFed() {
        return hunger >= wellFedThreshold() && thirst >= wellFedThreshold();
    }

    public boolean isSick() {
        // HARDY animals can withstand twice as many stress ticks before getting sick
        int threshold = SurvivalConfig.SICKNESS_THRESHOLD.get();
        if (getExpressedTrait() == Trait.HARDY) threshold *= 2;
        return stressTicks >= threshold;
    }

    public boolean isOverfed() {
        return overfedTicks >= SurvivalConfig.OVERFEEDING_THRESHOLD.get();
    }

}
