package com.kalob.ks_survival.health;

import com.kalob.ks_survival.init.ModAttachments;
import com.kalob.ks_survival.init.SurvivalConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class HealthEvents {

    // --- Damage routing ---
    // We don't absorb vanilla damage. Instead we track body part HP in parallel.
    // Headshots multiply incoming damage; limbs accumulate damage for wound/debuff purposes.
    // Lethal body-part state forces the current hit to be fatal; vanilla HP still governs other deaths.

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        BodyPartData data = player.getData(ModAttachments.BODY_PARTS.get());
        BodyPart part   = resolveHitPart(event.getSource(), player, data);
        int      damage = applyBodyPartArmor(player, part, Math.max(1, Math.round(event.getNewDamage())));
        damageArmorForPart(player, part,
                Math.max(1, Math.round(damage * SurvivalConfig.BODY_ARMOR_DURABILITY_DAMAGE_RATIO.get().floatValue())));

        boolean lethalPartDestroyed = data.damage(part, damage);
        if (event.getSource().is(DamageTypeTags.IS_FALL)) {
            data.lockMovementFor(SurvivalConfig.FALL_MOVEMENT_LOCK_TICKS.get());
        }

        // Headshots deal extra vanilla damage
        if (part == BodyPart.HEAD) {
            event.setNewDamage((float) (event.getNewDamage() * SurvivalConfig.HEAD_DAMAGE_MULTIPLIER.get()));
        }

        if (lethalPartDestroyed) {
            event.setNewDamage(Math.max(event.getNewDamage(), player.getHealth()));
        } else if (!part.lethal && event.getNewDamage() >= player.getHealth()) {
            event.setNewDamage(Math.max(0f, player.getHealth() - 1f));
        }

        if (isBleeding(event.getSource())) {
            data.addWound(part, isSevereBleed(event.getSource(), damage) ? Wound.SEVERE_BLEEDING : Wound.BLEEDING);
        }
        if (isCrush(event.getSource()))    data.addWound(part, Wound.FRACTURE);
        applyPainShock(player, data, part);

        applyCrippleEffects(player, data);
        player.setData(ModAttachments.BODY_PARTS.get(), data);
        sync(player, data);
    }

    // --- Tick: bleed, infection ---

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        BodyPartData data  = player.getData(ModAttachments.BODY_PARTS.get());
        boolean      dirty = false;
        int          age   = player.tickCount;

        applyCrippleEffects(player, data);
        disableSprintWithBrokenLegs(player, data);

        if (data.tickDamageFlash()) {
            dirty = true;
        }

        if (tickMovementLock(player, data, age)) {
            dirty = true;
        } else if (bothLegsCrippled(data) && player.getDeltaMovement().y > 0) {
            Vec3 movement = player.getDeltaMovement();
            player.setDeltaMovement(movement.x, 0, movement.z);
            player.hurtMarked = true;
        }

        int bleedInterval = SurvivalConfig.BLEED_INTERVAL.get();
        if (bleedInterval > 0 && age % bleedInterval == 0
                && (data.hasAnyWound(Wound.BLEEDING) || data.hasAnyWound(Wound.SEVERE_BLEEDING))) {
            // Directly reduce health to bypass our own damage handler
            float bleedDamage = data.hasAnyWound(Wound.SEVERE_BLEEDING)
                    ? SurvivalConfig.SEVERE_BLEED_DAMAGE.get().floatValue()
                    : SurvivalConfig.BLEED_DAMAGE.get().floatValue();
            float next = player.getHealth() - bleedDamage;
            if (next <= 0) {
                player.kill();
            } else {
                player.setHealth(next);
                player.hurtMarked = true; // triggers client-side hurt flash
            }
            dirty = true;
        }

        int naturalHealInterval = SurvivalConfig.NATURAL_HEAL_INTERVAL.get();
        if (naturalHealInterval > 0 && age % naturalHealInterval == 0 && canNaturallyRecover(player, data)) {
            dirty = data.healMostDamaged(SurvivalConfig.NATURAL_HEAL_AMOUNT.get());
        }

        if (age % 100 == 0 && data.hasAnyWound(Wound.INFECTION)) {
            player.getFoodData().setFoodLevel(
                    Math.max(0, player.getFoodData().getFoodLevel() - 1));
            dirty = true;
        }

        if (dirty) {
            applyCrippleEffects(player, data);
            player.setData(ModAttachments.BODY_PARTS.get(), data);
            sync(player, data);
        }
    }

    // --- Death: halve HP as death penalty ---

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        BodyPartData penalty = player.getData(ModAttachments.BODY_PARTS.get()).withHalvedHp();
        player.setData(ModAttachments.BODY_PARTS.get(), penalty);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        sync(player, player.getData(ModAttachments.BODY_PARTS.get()));
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        sync(player, player.getData(ModAttachments.BODY_PARTS.get()));
    }

    @SubscribeEvent
    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        BodyPartData data = player.getData(ModAttachments.BODY_PARTS.get());
        if (!bothArmsCrippled(data)) return;

        ItemStack stack = event.getItem();
        if (stack.getItem() instanceof BowItem
                || stack.getItem() instanceof CrossbowItem
                || stack.getItem() instanceof ShieldItem) {
            event.setCanceled(true);
        }
    }

    // --- Helpers ---

    public static void damagePlayerBodyPart(ServerPlayer player, BodyPart preferredPart, int amount, Wound wound) {
        BodyPartData data = player.getData(ModAttachments.BODY_PARTS.get());
        BodyPart part = preferLivingPair(preferredPart, data, player);

        data.damage(part, Math.max(1, amount));
        if (wound != null) {
            data.addWound(part, wound);
        }

        applyPainShock(player, data, part);
        applyCrippleEffects(player, data);
        player.setData(ModAttachments.BODY_PARTS.get(), data);
        sync(player, data);
    }

    private static BodyPart resolveHitPart(DamageSource source, Player victim, BodyPartData data) {
        if (source.is(DamageTypeTags.IS_FALL)) {
            return chooseLeg((victim.tickCount & 1) == 0 ? BodyPart.LEFT_LEG : BodyPart.RIGHT_LEG, data, victim);
        }
        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            return randomWeightedPart(victim);
        }
        if (source.is(DamageTypeTags.IS_FIRE)) {
            return randomWeightedPart(victim);
        }
        if (source.is(DamageTypes.DROWN) || source.is(DamageTypes.IN_WALL)) {
            return BodyPart.HEAD;
        }
        if (source.is(DamageTypes.STARVE) || source.is(DamageTypes.MAGIC) || source.is(DamageTypes.WITHER)) {
            return BodyPart.TORSO;
        }
        if (source.is(DamageTypeTags.IS_PROJECTILE) && source.getDirectEntity() != null) {
            return resolveProjectileHitPart(source.getDirectEntity(), victim, data);
        }

        Entity attacker = source.getDirectEntity();
        if (attacker == null) return BodyPart.TORSO;

        Vec3  aPos  = attacker.getEyePosition();
        Vec3  vPos  = victim.getEyePosition();
        Vec3  delta = vPos.subtract(aPos);

        double horizontalDist = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        float  pitch          = (float) Math.toDegrees(Math.atan2(delta.y, horizontalDist));
        float  toVictimYaw    = (float) Math.toDegrees(Math.atan2(-delta.x, delta.z));
        float  yawDelta       = wrapDegrees(attacker.getYRot() - toVictimYaw);

        return preferLivingPair(BodyPart.fromAngle(pitch, yawDelta), data, victim);
    }

    private static BodyPart resolveProjectileHitPart(Entity projectile, Player victim, BodyPartData data) {
        double relativeY = (projectile.getY() - victim.getY()) / Math.max(1.0, victim.getBbHeight());
        if (relativeY > 0.75) return BodyPart.HEAD;
        if (relativeY < 0.35) {
            return chooseLeg(projectile.getX() < victim.getX() ? BodyPart.LEFT_LEG : BodyPart.RIGHT_LEG, data, victim);
        }
        if (relativeY < 0.55) return BodyPart.TORSO;
        return chooseArm(projectile.getX() < victim.getX() ? BodyPart.LEFT_ARM : BodyPart.RIGHT_ARM, data, victim);
    }

    private static BodyPart preferLivingPair(BodyPart part, BodyPartData data, Player player) {
        return switch (part) {
            case LEFT_LEG, RIGHT_LEG -> chooseHealthierLimb(part, BodyPart.LEFT_LEG, BodyPart.RIGHT_LEG, data, player);
            case LEFT_ARM, RIGHT_ARM -> chooseHealthierLimb(part, BodyPart.LEFT_ARM, BodyPart.RIGHT_ARM, data, player);
            default -> part;
        };
    }

    private static BodyPart chooseLeg(BodyPart preferred, BodyPartData data, Player player) {
        return chooseHealthierLimb(preferred, BodyPart.LEFT_LEG, BodyPart.RIGHT_LEG, data, player);
    }

    private static BodyPart chooseArm(BodyPart preferred, BodyPartData data, Player player) {
        return chooseHealthierLimb(preferred, BodyPart.LEFT_ARM, BodyPart.RIGHT_ARM, data, player);
    }

    private static BodyPart chooseHealthierLimb(BodyPart preferred, BodyPart left, BodyPart right,
                                                BodyPartData data, Player player) {
        int leftHp = data.getHp(left);
        int rightHp = data.getHp(right);
        if (leftHp > rightHp) return left;
        if (rightHp > leftHp) return right;
        if (player != null) return player.getRandom().nextBoolean() ? left : right;
        return preferred;
    }

    private static BodyPart randomWeightedPart(Player player) {
        float total = 0f;
        for (BodyPart part : BodyPart.values()) total += part.weight;
        float roll = player.getRandom().nextFloat() * total;
        for (BodyPart part : BodyPart.values()) {
            roll -= part.weight;
            if (roll <= 0f) return part;
        }
        return BodyPart.TORSO;
    }

    private static int applyBodyPartArmor(ServerPlayer player, BodyPart part, int damage) {
        if (hasBodyPartArmor(player, part)) {
            return Math.max(1, Math.round(damage * SurvivalConfig.BODY_ARMOR_DAMAGE_MULTIPLIER.get().floatValue()));
        }
        return damage;
    }

    private static boolean hasBodyPartArmor(ServerPlayer player, BodyPart part) {
        return switch (part) {
            case HEAD -> !player.getItemBySlot(EquipmentSlot.HEAD).isEmpty();
            case TORSO, LEFT_ARM, RIGHT_ARM -> !player.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
            case LEFT_LEG, RIGHT_LEG ->
                    !player.getItemBySlot(EquipmentSlot.LEGS).isEmpty()
                            || !player.getItemBySlot(EquipmentSlot.FEET).isEmpty();
        };
    }

    private static void damageArmorForPart(ServerPlayer player, BodyPart part, int amount) {
        for (EquipmentSlot slot : armorSlotsFor(part)) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                stack.hurtAndBreak(amount, player, slot);
                return;
            }
        }
    }

    private static EquipmentSlot[] armorSlotsFor(BodyPart part) {
        return switch (part) {
            case HEAD -> new EquipmentSlot[]{EquipmentSlot.HEAD};
            case TORSO, LEFT_ARM, RIGHT_ARM -> new EquipmentSlot[]{EquipmentSlot.CHEST};
            case LEFT_LEG, RIGHT_LEG -> new EquipmentSlot[]{EquipmentSlot.LEGS, EquipmentSlot.FEET};
        };
    }

    private static void applyPainShock(ServerPlayer player, BodyPartData data, BodyPart part) {
        if (data.getHp(part) > 0 && data.getHp(part) * 4 > data.getMaxHp(part)) return;
        int confusionTicks = SurvivalConfig.PAIN_SHOCK_CONFUSION_TICKS.get();
        if (confusionTicks > 0) {
            player.addEffect(new MobEffectInstance(
                    net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                            .wrapAsHolder(net.minecraft.world.effect.MobEffects.CONFUSION.value()),
                    confusionTicks, 0, false, false, true));
        }
        int darknessTicks = SurvivalConfig.PAIN_SHOCK_DARKNESS_TICKS.get();
        if (darknessTicks > 0) {
            player.addEffect(new MobEffectInstance(
                    net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                            .wrapAsHolder(net.minecraft.world.effect.MobEffects.DARKNESS.value()),
                    darknessTicks, 0, false, false, true));
        }
    }

    private static boolean canNaturallyRecover(ServerPlayer player, BodyPartData data) {
        return SurvivalConfig.NATURAL_HEAL_AMOUNT.get() > 0
                && player.getFoodData().getFoodLevel() >= SurvivalConfig.NATURAL_HEAL_MIN_FOOD.get()
                && !data.hasAnyWound(Wound.BLEEDING)
                && !data.hasAnyWound(Wound.SEVERE_BLEEDING)
                && !data.hasAnyWound(Wound.INFECTION);
    }

    private static boolean bothLegsCrippled(BodyPartData data) {
        return data.isCrippled(BodyPart.LEFT_LEG) && data.isCrippled(BodyPart.RIGHT_LEG);
    }

    private static boolean bothArmsCrippled(BodyPartData data) {
        return data.isCrippled(BodyPart.LEFT_ARM) && data.isCrippled(BodyPart.RIGHT_ARM);
    }

    private static boolean anyLegCrippled(BodyPartData data) {
        return data.isCrippled(BodyPart.LEFT_LEG) || data.isCrippled(BodyPart.RIGHT_LEG);
    }

    private static void disableSprintWithBrokenLegs(ServerPlayer player, BodyPartData data) {
        if (anyLegCrippled(data) && player.isSprinting()) {
            player.setSprinting(false);
        }
    }

    private static boolean tickMovementLock(ServerPlayer player, BodyPartData data, int age) {
        if (!data.isMovementLocked()) return false;

        Vec3 movement = player.getDeltaMovement();
        player.setDeltaMovement(0, Math.min(0, movement.y), 0);
        player.hurtMarked = true;

        boolean changed = data.tickMovementLock();
        if (changed && (age % 20 == 0 || !data.isMovementLocked())) {
            sync(player, data);
        }
        return changed;
    }

    private static void applyCrippleEffects(ServerPlayer player, BodyPartData data) {
        int brokenArms = 0;
        if (data.isCrippled(BodyPart.LEFT_ARM)) brokenArms++;
        if (data.isCrippled(BodyPart.RIGHT_ARM)) brokenArms++;
        if (brokenArms > 0) {
            player.addEffect(new MobEffectInstance(
                    net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                            .wrapAsHolder(net.minecraft.world.effect.MobEffects.WEAKNESS.value()),
                    60, brokenArms - 1, false, false, true));
            player.addEffect(new MobEffectInstance(
                    net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                            .wrapAsHolder(net.minecraft.world.effect.MobEffects.DIG_SLOWDOWN.value()),
                    60, brokenArms - 1, false, false, true));
        }

        int brokenLegs = 0;
        if (data.isCrippled(BodyPart.LEFT_LEG)) brokenLegs++;
        if (data.isCrippled(BodyPart.RIGHT_LEG)) brokenLegs++;
        if (brokenLegs > 0) {
            int amplifier = brokenLegs == 1 ? 0 : 4;
            player.addEffect(new MobEffectInstance(
                    net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                            .wrapAsHolder(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN.value()),
                    60, amplifier, false, false, true));
        }
    }

    private static boolean isBleeding(DamageSource src) {
        return src.is(DamageTypeTags.IS_PROJECTILE)
                || src.getDirectEntity() instanceof net.minecraft.world.entity.LivingEntity;
    }

    private static boolean isSevereBleed(DamageSource src, int damage) {
        return damage >= 6 || src.is(DamageTypeTags.IS_PROJECTILE);
    }

    private static boolean isCrush(DamageSource src) {
        return src.is(DamageTypeTags.IS_FALL)
                || src.is(DamageTypeTags.IS_EXPLOSION)
                || src.is(DamageTypeTags.BYPASSES_ARMOR);
    }

    private static void sync(ServerPlayer player, BodyPartData data) {
        PacketDistributor.sendToPlayer(player, new BodyPartSyncPacket(player.getId(), data));
    }

    private static float wrapDegrees(float deg) {
        deg %= 360f;
        if (deg > 180f)  deg -= 360f;
        if (deg < -180f) deg += 360f;
        return deg;
    }
}
