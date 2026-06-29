package com.kalob.ks_survival.health;

import com.kalob.ks_survival.init.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class HealthEvents {

    private static final int BLEED_INTERVAL = 40;
    private static final float BLEED_DAMAGE = 1f;
    private static final float HEAD_DAMAGE_MULTIPLIER = 2f;

    // --- Damage routing ---
    // We don't absorb vanilla damage. Instead we track body part HP in parallel.
    // Headshots multiply incoming damage; limbs accumulate damage for wound/debuff purposes.
    // Only lethal body-part state (limb at 0) causes wound effects — vanilla HP governs actual death.

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        BodyPart part   = resolveHitPart(event.getSource().getDirectEntity(), player);
        int      damage = Math.max(1, Math.round(event.getNewDamage()));

        BodyPartData data = player.getData(ModAttachments.BODY_PARTS.get());
        data.damage(part, damage);

        // Headshots deal extra vanilla damage
        if (part == BodyPart.HEAD) {
            event.setNewDamage(event.getNewDamage() * HEAD_DAMAGE_MULTIPLIER);
        }

        // Wounds from non-lethal hits
        if (!data.isCrippled(part)) {
            if (isBleeding(event.getSource())) data.addWound(part, Wound.BLEEDING);
            if (isCrush(event.getSource()))    data.addWound(part, Wound.FRACTURE);
        }

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

        if (age % BLEED_INTERVAL == 0 && data.hasAnyWound(Wound.BLEEDING)) {
            // Directly reduce health to bypass our own damage handler
            float next = player.getHealth() - BLEED_DAMAGE;
            if (next <= 0) {
                player.kill();
            } else {
                player.setHealth(next);
                player.hurtMarked = true; // triggers client-side hurt flash
            }
            dirty = true;
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

    // --- Helpers ---

    private static BodyPart resolveHitPart(Entity attacker, Player victim) {
        if (attacker == null) return BodyPart.TORSO;

        Vec3  aPos  = attacker.getEyePosition();
        Vec3  vPos  = victim.getEyePosition();
        Vec3  delta = vPos.subtract(aPos);

        double horizontalDist = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        float  pitch          = (float) Math.toDegrees(Math.atan2(delta.y, horizontalDist));
        float  toVictimYaw    = (float) Math.toDegrees(Math.atan2(-delta.x, delta.z));
        float  yawDelta       = wrapDegrees(attacker.getYRot() - toVictimYaw);

        return BodyPart.fromAngle(pitch, yawDelta);
    }

    private static void applyCrippleEffects(ServerPlayer player, BodyPartData data) {
        for (BodyPart part : BodyPart.values()) {
            part.crippleEffect().ifPresent(effect -> {
                if (data.isCrippled(part)) {
                    player.addEffect(new MobEffectInstance(
                            net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                                    .wrapAsHolder(effect),
                            60, 0, false, false, true));
                }
            });
        }
    }

    private static boolean isBleeding(net.minecraft.world.damagesource.DamageSource src) {
        return src.is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE)
                || src.getDirectEntity() instanceof net.minecraft.world.entity.LivingEntity;
    }

    private static boolean isCrush(net.minecraft.world.damagesource.DamageSource src) {
        return src.is(net.minecraft.tags.DamageTypeTags.IS_FALL)
                || src.is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR);
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
