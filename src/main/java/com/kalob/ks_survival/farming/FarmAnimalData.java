package com.kalob.ks_survival.farming;

import com.kalob.ks_survival.init.SurvivalConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class FarmAnimalData {

    public static final int MAX = 100;

    public static int tickInterval()     { return SurvivalConfig.ANIMAL_TICK_INTERVAL.get(); }
    public static int stressThreshold()  { return SurvivalConfig.STRESS_THRESHOLD.get(); }
    public static int wellFedThreshold() { return SurvivalConfig.WELL_FED_THRESHOLD.get(); }

    public static final Codec<FarmAnimalData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("hunger").forGetter(d -> d.hunger),
            Codec.INT.fieldOf("thirst").forGetter(d -> d.thirst)
        ).apply(instance, FarmAnimalData::fromCodec)
    );

    private int hunger;
    private int thirst;

    public FarmAnimalData() {
        this.hunger = MAX;
        this.thirst = MAX;
    }

    private static FarmAnimalData fromCodec(int hunger, int thirst) {
        FarmAnimalData d = new FarmAnimalData();
        d.hunger = hunger;
        d.thirst = thirst;
        return d;
    }

    public int getHunger() { return hunger; }
    public int getThirst() { return thirst; }

    public void feed() { this.hunger = MAX; }
    public void water() { this.thirst = MAX; }

    public void tick() {
        if (hunger > 0) hunger--;
        if (thirst > 0) thirst--;
    }

    public boolean isStressed() {
        return hunger < stressThreshold() || thirst < stressThreshold();
    }

    public boolean isWellFed() {
        return hunger >= wellFedThreshold() && thirst >= wellFedThreshold();
    }
}
