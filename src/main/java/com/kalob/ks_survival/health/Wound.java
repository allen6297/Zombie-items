package com.kalob.ks_survival.health;

import net.minecraft.util.StringRepresentable;

public enum Wound implements StringRepresentable {
    BLEEDING("bleeding"),
    FRACTURE("fracture"),
    INFECTION("infection");

    private final String name;

    Wound(String name) { this.name = name; }

    @Override
    public String getSerializedName() { return name; }
}
