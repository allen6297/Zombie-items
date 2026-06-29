package com.kalob.ks_survival.health;

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
            WOUND_MAP_CODEC.fieldOf("wounds").forGetter(d -> d.wounds)
    ).apply(inst, BodyPartData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BodyPartData> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    private final Map<BodyPart, Integer> hp;
    private final Map<BodyPart, Set<Wound>> wounds;

    public BodyPartData() {
        this.hp = new EnumMap<>(BodyPart.class);
        this.wounds = new EnumMap<>(BodyPart.class);
        for (BodyPart part : BodyPart.values()) {
            hp.put(part, part.defaultMaxHp);
            wounds.put(part, EnumSet.noneOf(Wound.class));
        }
    }

    private BodyPartData(Map<BodyPart, Integer> hp, Map<BodyPart, Set<Wound>> wounds) {
        this.hp = new EnumMap<>(hp);
        this.wounds = new EnumMap<>(BodyPart.class);
        for (BodyPart part : BodyPart.values()) {
            Set<Wound> w = wounds.get(part);
            this.wounds.put(part, w != null && !w.isEmpty()
                    ? EnumSet.copyOf(w) : EnumSet.noneOf(Wound.class));
        }
    }

    public int getHp(BodyPart part) {
        return hp.getOrDefault(part, part.defaultMaxHp);
    }

    public int getMaxHp(BodyPart part) {
        return part.defaultMaxHp;
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

    /** Returns true if this hit should kill the entity. */
    public boolean damage(BodyPart part, int amount) {
        int current = getHp(part);
        int next = Math.max(0, current - amount);
        hp.put(part, next);
        if (next == 0 && part.lethal) return true;
        if (next == 0 && current > 0) {
            addWound(part, Wound.FRACTURE);
        }
        return false;
    }

    public void heal(BodyPart part, int amount) {
        hp.put(part, Math.min(part.defaultMaxHp, getHp(part) + amount));
    }

    public void healAll(int amount) {
        for (BodyPart part : BodyPart.values()) heal(part, amount);
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
        }
        return copy;
    }

    Map<BodyPart, Integer> hp() { return hp; }
    Map<BodyPart, Set<Wound>> wounds() { return wounds; }
}
