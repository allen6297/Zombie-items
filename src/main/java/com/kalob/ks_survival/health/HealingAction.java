package com.kalob.ks_survival.health;

import java.util.EnumSet;
import java.util.Set;

/**
 * Describes what a single use of a healing item does.
 * Call {@link #selectPart} to pick the best target, then {@link #apply} to mutate data.
 */
public final class HealingAction {

    public final int useDuration;
    public final int hpRestore;
    public final Set<Wound> removes;
    public final Set<Wound> adds;
    /** Which wound types to prioritise when selecting a body part. Empty = lowest HP. */
    public final Set<Wound> targetWounds;
    /** If true, the action is applied to ALL parts (e.g. systemic infection cure). */
    public final boolean allParts;

    private HealingAction(Builder b) {
        this.useDuration  = b.useDuration;
        this.hpRestore    = b.hpRestore;
        this.removes      = b.removes.isEmpty() ? Set.of() : EnumSet.copyOf(b.removes);
        this.adds         = b.adds.isEmpty() ? Set.of() : EnumSet.copyOf(b.adds);
        this.targetWounds = b.targetWounds.isEmpty() ? Set.of() : EnumSet.copyOf(b.targetWounds);
        this.allParts     = b.allParts;
    }

    /** Returns the best body part to apply this action to, or null if allParts. */
    public BodyPart selectPart(BodyPartData data) {
        if (allParts) return null;

        if (!targetWounds.isEmpty()) {
            // Prefer the wounded part with lowest remaining HP (most urgent)
            BodyPart best = null;
            int lowestHp = Integer.MAX_VALUE;
            for (BodyPart part : BodyPart.values()) {
                if (targetWounds.stream().anyMatch(wound -> data.hasWound(part, wound))) {
                    int hp = data.getHp(part);
                    if (hp < lowestHp) { lowestHp = hp; best = part; }
                }
            }
            if (best != null) return best;
        }

        // Fallback: part with lowest HP that isn't full
        BodyPart worst = BodyPart.TORSO;
        int lowestHp = Integer.MAX_VALUE;
        for (BodyPart part : BodyPart.values()) {
            int hp = data.getHp(part);
            if (hp < lowestHp && hp < data.getMaxHp(part)) {
                lowestHp = hp; worst = part;
            }
        }
        return worst;
    }

    /**
     * Returns true if this action has anything useful to do given the current data.
     * Items should refuse to use if this returns false.
     */
    public boolean hasTarget(BodyPartData data) {
        if (allParts) {
            if (removes.isEmpty()) return true;
            for (Wound w : removes) if (data.hasAnyWound(w)) return true;
            return false;
        }
        if (!targetWounds.isEmpty() && targetWounds.stream().noneMatch(data::hasAnyWound)) return false;
        return selectPart(data) != null;
    }

    /** Apply to a single part (or all parts if {@link #allParts}). */
    public void apply(BodyPartData data, BodyPart part) {
        if (allParts) {
            for (BodyPart p : BodyPart.values()) applyOne(data, p);
        } else if (part != null) {
            applyOne(data, part);
        }
    }

    private void applyOne(BodyPartData data, BodyPart part) {
        if (hpRestore > 0) data.heal(part, hpRestore);
        for (Wound w : removes) data.removeWound(part, w);
        for (Wound w : adds) data.addWound(part, w);
    }

    // --- Builder ---

    public static Builder builder(int useDuration) {
        return new Builder(useDuration);
    }

    public static final class Builder {
        private final int useDuration;
        private int hpRestore   = 0;
        private final Set<Wound> removes = EnumSet.noneOf(Wound.class);
        private final Set<Wound> targetWounds = EnumSet.noneOf(Wound.class);
        private boolean allParts   = false;

        private Builder(int useDuration) { this.useDuration = useDuration; }

        private final Set<Wound> adds = EnumSet.noneOf(Wound.class);

        public Builder restores(int hp)        { this.hpRestore = hp;   return this; }
        public Builder removes(Wound w)        { this.removes.add(w);   return this; }
        public Builder adds(Wound w)           { this.adds.add(w);      return this; }
        public Builder targets(Wound w)        { this.targetWounds.add(w);  return this; }
        public Builder allParts()              { this.allParts = true;  return this; }
        public HealingAction build()           { return new HealingAction(this); }
    }
}
