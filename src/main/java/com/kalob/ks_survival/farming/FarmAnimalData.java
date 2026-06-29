package com.kalob.ks_survival.farming;

import com.kalob.ks_survival.farming.genetics.Coat;
import com.kalob.ks_survival.init.SurvivalConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
            Codec.INT.optionalFieldOf("alleleB", 0).forGetter(d -> d.alleleB)
        ).apply(instance, FarmAnimalData::fromCodec)
    );

    private int hunger;
    private int thirst;
    private int wellFedTicks;
    private int stressTicks;
    private int overfedTicks;
    private int tameness;
    private int panicTicks;
    private int alleleA;
    private int alleleB;

    public FarmAnimalData() {
        this.hunger = MAX;
        this.thirst = MAX;
        this.wellFedTicks = 0;
        this.stressTicks = 0;
        this.overfedTicks = 0;
        this.tameness = 0;
        this.panicTicks = 0;
    }

    public static FarmAnimalData of(int hunger, int thirst, int wellFedTicks, int stressTicks, int overfedTicks, int tameness, int panicTicks, int alleleA, int alleleB) {
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
        return d;
    }

    private static FarmAnimalData fromCodec(int hunger, int thirst, int wellFedTicks, int stressTicks, int overfedTicks, int tameness, int alleleA, int alleleB) {
        return of(hunger, thirst, wellFedTicks, stressTicks, overfedTicks, tameness, 0, alleleA, alleleB);
    }

    public int getHunger()       { return hunger; }
    public int getThirst()       { return thirst; }
    public int getWellFedTicks() { return wellFedTicks; }
    public int getStressTicks()  { return stressTicks; }
    public int getOverfedTicks() { return overfedTicks; }
    public int getTameness()     { return tameness; }
    public int getPanicTicks()   { return panicTicks; }
    public int getAlleleA()       { return alleleA; }
    public int getAlleleB()       { return alleleB; }

    public Coat getExpressedCoat() {
        return Coat.expressed(Coat.byId(alleleA), Coat.byId(alleleB));
    }

    public void setRandomAlleles(RandomSource rng) {
        this.alleleA = Coat.random(rng).id;
        this.alleleB = Coat.random(rng).id;
    }

    public void inheritGenetics(FarmAnimalData parentA, FarmAnimalData parentB, RandomSource rng) {
        this.alleleA = rng.nextBoolean() ? parentA.alleleA : parentA.alleleB;
        this.alleleB = rng.nextBoolean() ? parentB.alleleA : parentB.alleleB;
    }

    public boolean isPanicking() { return panicTicks > 0; }

    public void panic(int ticks) { this.panicTicks = Math.max(this.panicTicks, ticks); }

    /** Panic for the given number of real game ticks, converted to tick-interval units. */
    public void panicFor(int gameTicks) {
        panic(Math.max(1, gameTicks / tickInterval()));
    }

    public void feed()  { this.hunger = MAX; }
    public void water() { this.thirst = MAX; }

    public void cure() {
        this.stressTicks = 0;
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

    public void tick(boolean nearWater, boolean nearFeedingTrough, int extraHungerDrain, int extraThirstDrain, boolean safetyBonus) {
        if (panicTicks > 0) panicTicks--;
        int drain = Math.max(1, tickInterval() / BASE_INTERVAL);
        if (nearFeedingTrough) { if (hunger < MAX) hunger = Math.min(MAX, hunger + drain); }
        else { hunger = Math.max(0, hunger - drain - extraHungerDrain); }
        if (nearWater) { if (thirst < MAX) thirst = Math.min(MAX, thirst + drain); }
        else { thirst = Math.max(0, thirst - drain - extraThirstDrain); }

        if (isWellFed()) {
            wellFedTicks++;
            stressTicks = 0;
            overfedTicks++;
        } else if (isStressed() && !safetyBonus) {
            wellFedTicks = 0;
            stressTicks++;
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
        return stressTicks >= SurvivalConfig.SICKNESS_THRESHOLD.get();
    }

    public boolean isOverfed() {
        return overfedTicks >= SurvivalConfig.OVERFEEDING_THRESHOLD.get();
    }

}
