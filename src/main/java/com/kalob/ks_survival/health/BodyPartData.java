package com.kalob.ks_survival.health;

import com.kalob.ks_survival.init.SurvivalConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class BodyPartData {

    private static final Codec<BodyPart> PART_CODEC =
            StringRepresentable.fromEnum(BodyPart::values);

    private static final Codec<Wound> WOUND_CODEC =
            StringRepresentable.fromEnum(Wound::values);

    private static final Codec<Set<Wound>> WOUND_SET_CODEC =
            WOUND_CODEC.listOf().xmap(
                    list -> list.isEmpty() ? EnumSet.noneOf(Wound.class) : EnumSet.copyOf(list),
                    set  -> set.stream().toList()
            );

    private static final Codec<Map<BodyPart, Integer>> HP_MAP_CODEC =
            Codec.unboundedMap(PART_CODEC, Codec.INT);

    private static final Codec<Map<BodyPart, Set<Wound>>> WOUND_MAP_CODEC =
            Codec.unboundedMap(PART_CODEC, WOUND_SET_CODEC);

    public static final Codec<BodyPartData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            HP_MAP_CODEC.fieldOf("hp").forGetter(d -> d.hp),
            WOUND_MAP_CODEC.fieldOf("wounds").forGetter(d -> d.wounds),
            Codec.INT.optionalFieldOf("movementLockTicks", 0).forGetter(d -> d.movementLockTicks),
            PART_CODEC.optionalFieldOf("lastDamagedPart", BodyPart.TORSO).forGetter(d -> d.lastDamagedPart),
            Codec.INT.optionalFieldOf("damageFlashTicks", 0).forGetter(d -> d.damageFlashTicks)
    ).apply(inst, BodyPartData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BodyPartData> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    private final Map<BodyPart, Integer> hp;
    private final Map<BodyPart, Set<Wound>> wounds;
    private int movementLockTicks;
    private BodyPart lastDamagedPart;
    private int damageFlashTicks;

    public BodyPartData() {
        this.hp = new EnumMap<>(BodyPart.class);
        this.wounds = new EnumMap<>(BodyPart.class);
        for (BodyPart part : BodyPart.values()) {
            hp.put(part, configuredMaxHp(part));
            wounds.put(part, EnumSet.noneOf(Wound.class));
        }
        movementLockTicks = 0;
        lastDamagedPart = BodyPart.TORSO;
        damageFlashTicks = 0;
    }

    private BodyPartData(Map<BodyPart, Integer> hp, Map<BodyPart, Set<Wound>> wounds, int movementLockTicks,
                         BodyPart lastDamagedPart, int damageFlashTicks) {
        this.hp = new EnumMap<>(hp);
        this.wounds = new EnumMap<>(BodyPart.class);
        for (BodyPart part : BodyPart.values()) {
            Set<Wound> w = wounds.get(part);
            this.wounds.put(part, w != null && !w.isEmpty()
                    ? EnumSet.copyOf(w) : EnumSet.noneOf(Wound.class));
        }
        this.movementLockTicks = Math.max(0, movementLockTicks);
        this.lastDamagedPart = lastDamagedPart;
        this.damageFlashTicks = Math.max(0, damageFlashTicks);
    }

    public int getHp(BodyPart part) {
        return hp.getOrDefault(part, configuredMaxHp(part));
    }

    public int getMaxHp(BodyPart part) {
        return configuredMaxHp(part);
    }

    public boolean hasWound(BodyPart part, Wound wound) {
        return wounds.getOrDefault(part, EnumSet.noneOf(Wound.class)).contains(wound);
    }

    public boolean hasAnyWound(Wound wound) {
        for (BodyPart part : BodyPart.values()) {
            if (hasWound(part, wound)) return true;
        }
        return false;
    }

    public boolean isCrippled(BodyPart part) {
        return getHp(part) <= 0;
    }

    public BodyPart getLastDamagedPart() {
        return lastDamagedPart;
    }

    public int getDamageFlashTicks() {
        return damageFlashTicks;
    }

    public boolean tickDamageFlash() {
        if (damageFlashTicks <= 0) return false;
        damageFlashTicks--;
        return true;
    }

    public int getMovementLockTicks() {
        return movementLockTicks;
    }

    public boolean isMovementLocked() {
        return movementLockTicks > 0;
    }

    public void lockMovementFor(int ticks) {
        movementLockTicks = Math.max(movementLockTicks, ticks);
    }

    public boolean tickMovementLock() {
        if (movementLockTicks <= 0) return false;
        movementLockTicks--;
        return true;
    }

    /** Returns true if this hit should kill the entity. */
    public boolean damage(BodyPart part, int amount) {
        int current = getHp(part);
        int next = Math.max(0, current - amount);
        hp.put(part, next);
        flashDamage(part, 20);
        if (next == 0 && part.lethal) return true;
        if (next == 0 && current > 0) {
            addWound(part, Wound.FRACTURE);
        }
        return false;
    }

    public void heal(BodyPart part, int amount) {
        hp.put(part, Math.min(getMaxHp(part), getHp(part) + amount));
    }

    public void setHp(BodyPart part, int amount) {
        hp.put(part, Math.max(0, Math.min(getMaxHp(part), amount)));
        flashDamage(part, 20);
    }

    public void healAll(int amount) {
        for (BodyPart part : BodyPart.values()) heal(part, amount);
    }

    public boolean healMostDamaged(int amount) {
        BodyPart target = null;
        int lowestHp = Integer.MAX_VALUE;
        for (BodyPart part : BodyPart.values()) {
            int hpValue = getHp(part);
            if (hpValue > 0 && hpValue < getMaxHp(part) && hpValue < lowestHp) {
                target = part;
                lowestHp = hpValue;
            }
        }
        if (target == null) return false;
        heal(target, amount);
        return true;
    }

    public void addWound(BodyPart part, Wound wound) {
        wounds.computeIfAbsent(part, p -> EnumSet.noneOf(Wound.class)).add(wound);
    }

    public void removeWound(BodyPart part, Wound wound) {
        wounds.getOrDefault(part, EnumSet.noneOf(Wound.class)).remove(wound);
    }

    /** Returns a copy of this data with all HP halved — used as a death penalty on respawn. */
    public BodyPartData withHalvedHp() {
        BodyPartData copy = new BodyPartData();
        for (BodyPart part : BodyPart.values()) {
            copy.hp.put(part, Math.max(1, getHp(part) / 2));
            Set<Wound> existing = wounds.get(part);
            copy.wounds.put(part, existing != null && !existing.isEmpty()
                    ? EnumSet.copyOf(existing) : EnumSet.noneOf(Wound.class));
        }
        copy.movementLockTicks = movementLockTicks;
        copy.lastDamagedPart = lastDamagedPart;
        copy.damageFlashTicks = damageFlashTicks;
        return copy;
    }

    private void flashDamage(BodyPart part, int ticks) {
        lastDamagedPart = part;
        damageFlashTicks = Math.max(damageFlashTicks, ticks);
    }

    public static int configuredMaxHp(BodyPart part) {
        return switch (part) {
            case HEAD -> SurvivalConfig.HEAD_MAX_HP.get();
            case TORSO -> SurvivalConfig.TORSO_MAX_HP.get();
            case LEFT_ARM, RIGHT_ARM -> SurvivalConfig.ARM_MAX_HP.get();
            case LEFT_LEG, RIGHT_LEG -> SurvivalConfig.LEG_MAX_HP.get();
        };
    }

    Map<BodyPart, Integer> hp() { return hp; }
    Map<BodyPart, Set<Wound>> wounds() { return wounds; }
}
