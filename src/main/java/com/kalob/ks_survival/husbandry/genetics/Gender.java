package com.kalob.ks_survival.husbandry.genetics;

import net.minecraft.util.RandomSource;

public enum Gender {
    MALE(0),
    FEMALE(1);

    public final int id;

    Gender(int id) { this.id = id; }

    public static Gender byId(int id) {
        return id == 1 ? FEMALE : MALE;
    }

    public static Gender random(RandomSource rng) {
        return rng.nextBoolean() ? MALE : FEMALE;
    }

    public String symbol() {
        return this == MALE ? "♂" : "♀";
    }

    public String displayName() {
        return this == MALE ? "Male" : "Female";
    }
}
