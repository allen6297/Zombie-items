package com.kalob.ks_survival.farming;

import com.kalob.ks_survival.compat.SereneSeasonsCompat;
import com.kalob.ks_survival.farming.genetics.ClimateVariant;
import com.kalob.ks_survival.farming.genetics.Gender;
import com.kalob.ks_survival.farming.genetics.Trait;
import com.kalob.ks_survival.farming.goal.FollowHerdGoal;
import com.kalob.ks_survival.farming.goal.SeekFoodTroughGoal;
import com.kalob.ks_survival.farming.goal.SeekWaterTroughGoal;
import com.kalob.ks_survival.init.ModAttachments;
import com.kalob.ks_survival.init.SurvivalConfig;
import com.kalob.ks_survival.item.MedicineItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class FarmingEvents {

    // -
    // Entity join — register AI goals and assign random genetics to new spawns
    // -

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof Animal animal)) return;
        if (!SurvivalConfig.isTrackedAnimal(animal)) return;

        animal.goalSelector.addGoal(4, new SeekWaterTroughGoal(animal, 16, 1.0));
        animal.goalSelector.addGoal(4, new SeekFoodTroughGoal(animal, 16, 1.0));
        animal.goalSelector.addGoal(6, new FollowHerdGoal(animal, 1.0, 12, 4));

        // Only assign genetics on first spawn, not on chunk load
        if (!event.loadedFromDisk()) {
            ClimateVariant climate = ClimateVariant.fromBiome(
                    animal.level().getBiome(animal.blockPosition()));
            FarmAnimalData data = animal.getData(ModAttachments.FARM_ANIMAL.get());
            data.setRandomAlleles(animal.level().getRandom(), climate);
            animal.setData(ModAttachments.FARM_ANIMAL.get(), data);
        }
    }

    // -
    // Per-tick update — runs every tickInterval ticks (default 200 = 10 sec)
    // -

    @SubscribeEvent
    public static void onAnimalTick(EntityTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof Animal animal)) return;
        if (animal.tickCount % FarmAnimalData.tickInterval() != 0) return;
        if (!SurvivalConfig.isTrackedAnimal(animal)) return;

        // - Environment checks -
        boolean nearWater = BlockPos.betweenClosedStream(
                animal.blockPosition().offset(-5, -1, -5),
                animal.blockPosition().offset(5, 1, 5)
        ).anyMatch(pos -> animal.level().getFluidState(pos).is(FluidTags.WATER));

        int extraHungerDrain = SereneSeasonsCompat.extraHungerDrain(animal.level());
        int extraThirstDrain = SereneSeasonsCompat.extraThirstDrain(animal.level());

        // - Herd detection -
        int radius = SurvivalConfig.CROWDING_RADIUS.get();
        @SuppressWarnings("unchecked")
        Class<Animal> animalClass = (Class<Animal>) animal.getClass();
        List<Animal> herd = animal.level()
                .getEntitiesOfClass(animalClass, animal.getBoundingBox().inflate(radius))
                .stream().filter(e -> e != animal).toList();
        long herdCount = herd.size();
        int crowdingLimit = SurvivalConfig.CROWDING_LIMIT.get();
        // Being in a small herd suppresses mild stress (safety in numbers)
        boolean safetyBonus = herdCount >= 1 && herdCount < crowdingLimit;

        // - Core stat update -
        FarmAnimalData data = animal.getData(ModAttachments.FARM_ANIMAL.get());
        data.tick(nearWater, extraHungerDrain, extraThirstDrain, safetyBonus);

        // Tameness ticks up when healthy; being near a domestic animal gives an extra tick
        boolean nearDomestic = herd.stream().anyMatch(a ->
                a.getData(ModAttachments.FARM_ANIMAL.get()).isDomestic());
        data.tickTameness(!data.isStressed() && !data.isSick());
        if (nearDomestic && !data.isDomestic()) data.tickTameness(true);

        animal.setData(ModAttachments.FARM_ANIMAL.get(), data);

        // - Panic spread -
        // A panicking animal triggers panic in calm herd members nearby
        if (data.isPanicking()) {
            herd.forEach(nearby -> {
                FarmAnimalData nearbyData = nearby.getData(ModAttachments.FARM_ANIMAL.get());
                if (!nearbyData.isPanicking()) {
                    nearbyData.panicFor(600); // 30 seconds
                    nearby.setData(ModAttachments.FARM_ANIMAL.get(), nearbyData);
                    nearby.playAmbientSound();
                    syncToTracking(nearby, nearbyData);
                }
            });
        }

        // - Flee behaviour -
        // Wild or panicking animals flee players within 5 blocks; if panicking with no nearby
        // player, scatter in a random direction so they don't stand still
        if (data.isWild() || data.isPanicking()) {
            boolean fleeingPlayer = false;
            double fleeRadiusSq = Math.pow(SurvivalConfig.FLEE_RADIUS.get(), 2);
            for (var player : animal.level().players()) {
                if (player.distanceToSqr(animal) < fleeRadiusSq) {
                    data.panicFor(1200); // 60 seconds
                    double fleeX = animal.getX() + (animal.getX() - player.getX()) * 2;
                    double fleeZ = animal.getZ() + (animal.getZ() - player.getZ()) * 2;
                    animal.getNavigation().moveTo(fleeX, animal.getY(), fleeZ, 1.4);
                    fleeingPlayer = true;
                    break;
                }
            }
            if (!fleeingPlayer && data.isPanicking()) {
                double angle = animal.level().getRandom().nextDouble() * Math.PI * 2;
                animal.getNavigation().moveTo(
                        animal.getX() + Math.cos(angle) * 8,
                        animal.getY(),
                        animal.getZ() + Math.sin(angle) * 8,
                        1.4);
            }
        }

        syncToTracking(animal, data);

        // - Status effects -
        // Effects last slightly beyond the next tick so there's no visible gap
        int effectDuration = FarmAnimalData.tickInterval() + 20;
        if (data.isSick()) {
            animal.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, effectDuration, 1, false, false, true));
            animal.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, effectDuration, 0, false, false, true));
            if (animal.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        animal.getX(), animal.getY() + animal.getBbHeight() / 2, animal.getZ(),
                        3, 0.2, 0.2, 0.2, 0.01);
            }
        } else if (data.isOverfed() || data.isStressed()) {
            animal.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, effectDuration, 0, false, false, true));
        } else if (data.isWellFed()) {
            animal.addEffect(new MobEffectInstance(MobEffects.REGENERATION, effectDuration, 0, false, false, true));
        }

        if (herdCount >= crowdingLimit) {
            animal.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, effectDuration, 0, false, false, true));
        }
    }

    // -
    // Loot drops — productivity bonus duplicates random drops on kill
    // -

    @SubscribeEvent
    public static void onLivingDrop(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Animal animal)) return;
        if (!SurvivalConfig.isTrackedAnimal(animal)) return;

        FarmAnimalData data = animal.getData(ModAttachments.FARM_ANIMAL.get());
        int productivity = data.getProductivity();
        if (productivity <= 0) return;

        float chance = productivity / 100f;
        List<ItemEntity> drops = List.copyOf(event.getDrops());
        for (ItemEntity drop : drops) {
            if (animal.level().getRandom().nextFloat() < chance) {
                event.getDrops().add(new ItemEntity(animal.level(),
                        drop.getX(), drop.getY(), drop.getZ(),
                        drop.getItem().copyWithCount(1)));
            }
        }
    }

    // -
    // Player hits domestic animal — damages tameness and triggers panic
    // -

    @SubscribeEvent
    public static void onAnimalHurt(LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof Animal animal)) return;
        if (!SurvivalConfig.isTrackedAnimal(animal)) return;
        if (!(event.getSource().getEntity() instanceof Player)) return;

        FarmAnimalData data = animal.getData(ModAttachments.FARM_ANIMAL.get());
        if (!data.isDomestic()) return;

        data.damageTameness(20);
        data.panicFor(600); // 30 seconds
        animal.setData(ModAttachments.FARM_ANIMAL.get(), data);
        syncToTracking(animal, data);
    }

    // -
    // Medicine item interaction
    // -

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        MedicineItem.onEntityInteract(event);
    }

    // -
    // Baby spawn — season gate, stress penalty, genetics inheritance
    // -

    @SubscribeEvent
    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        AgeableMob child = event.getChild();
        if (child == null) return;
        if (!(event.getParentA() instanceof Animal parentA)) return;
        if (!SurvivalConfig.isTrackedAnimal(parentA)) return;

        FarmAnimalData dataA = parentA.getData(ModAttachments.FARM_ANIMAL.get());
        FarmAnimalData dataB2 = (event.getParentB() instanceof Animal pb) ? pb.getData(ModAttachments.FARM_ANIMAL.get()) : null;

        // Require opposite genders
        if (dataB2 != null && dataA.getExpressedGender() == dataB2.getExpressedGender()) {
            event.setCanceled(true);
            return;
        }

        // Domestic animals can breed year-round; wild/adjusting animals respect seasons
        var seasons = SurvivalConfig.getBreedingSeasons(parentA);
        if (!SereneSeasonsCompat.isBreedingSeason(parentA.level(), parentA, seasons, dataA.isDomestic())) {
            event.setCanceled(true);
            return;
        }

        FarmAnimalData dataB = dataB2;

        // FECUND trait halves growth time; stressed parents slow it down
        boolean fecund = dataA.getExpressedTrait() == Trait.FECUND
                || (dataB != null && dataB.getExpressedTrait() == Trait.FECUND);
        boolean stressed = dataA.isStressed() || (dataB != null && dataB.isStressed());
        if (!stressed) {
            child.setAge(fecund ? -6000 : -12000);
        }

        if (dataB != null && child instanceof Animal childAnimal) {
            FarmAnimalData childData = childAnimal.getData(ModAttachments.FARM_ANIMAL.get());
            childData.inheritTameness(dataA, dataB);
            childData.inheritGenetics(dataA, dataB, child.level().getRandom());
            childAnimal.setData(ModAttachments.FARM_ANIMAL.get(), childData);
        }
    }

    // -
    // Helpers
    // -

    private static void syncToTracking(Animal animal, FarmAnimalData data) {
        PacketDistributor.sendToPlayersTrackingEntity(animal, new FarmAnimalSyncPacket(animal.getId(), data));
    }
}
