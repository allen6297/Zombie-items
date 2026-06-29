package com.kalob.ks_survival.farming.goal;

import com.kalob.ks_survival.block.FoodTroughBlockEntity;
import com.kalob.ks_survival.farming.FarmAnimalData;
import com.kalob.ks_survival.farming.FarmAnimalSyncPacket;
import com.kalob.ks_survival.init.ModAttachments;
import com.kalob.ks_survival.init.SurvivalBlocks;
import com.kalob.ks_survival.init.SurvivalConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;

public class SeekFoodTroughGoal extends Goal {

    private final PathfinderMob mob;
    private final int searchRadius;
    private final double speed;
    private BlockPos targetPos;
    private int searchCooldown = 0;

    public SeekFoodTroughGoal(PathfinderMob mob, int searchRadius, double speed) {
        this.mob = mob;
        this.searchRadius = searchRadius;
        this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    private Set<TagKey<Item>> getDietTags() {
        return SurvivalConfig.getDietTags(mob);
    }

    @Override
    public boolean canUse() {
        if (searchCooldown > 0) { searchCooldown--; return false; }
        FarmAnimalData data = mob.getData(ModAttachments.FARM_ANIMAL.get());
        if (!data.isHungry()) return false;
        targetPos = findNearestTrough();
        searchCooldown = 100;
        return targetPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (targetPos == null) return false;
        if (!mob.level().getBlockState(targetPos).is(SurvivalBlocks.FOOD_TROUGH.get())) return false;
        if (mob.level().getBlockEntity(targetPos) instanceof FoodTroughBlockEntity trough
                && !trough.hasValidFood(getDietTags())) return false;
        return mob.getData(ModAttachments.FARM_ANIMAL.get()).isHungry();
    }

    @Override
    public void start() {
        mob.getNavigation().moveTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, speed);
    }

    @Override
    public void tick() {
        if (targetPos == null) return;
        mob.getLookControl().setLookAt(targetPos.getX(), targetPos.getY(), targetPos.getZ());

        if (!mob.getNavigation().isInProgress()) {
            mob.getNavigation().moveTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, speed);
        }

        if (mob.distanceToSqr(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5) < 2.25) {
            if (mob.level().getBlockEntity(targetPos) instanceof FoodTroughBlockEntity trough) {
                FarmAnimalData data = mob.getData(ModAttachments.FARM_ANIMAL.get());
                if (data.isHungry() && trough.consumeValidFood(getDietTags())) {
                    data.feed();
                    mob.setData(ModAttachments.FARM_ANIMAL.get(), data);
                    if (mob.level() instanceof ServerLevel sl) {
                        sl.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                mob.getX(), mob.getY() + mob.getBbHeight(), mob.getZ(),
                                5, 0.3, 0.3, 0.3, 0.0);
                    }
                    mob.level().sendBlockUpdated(targetPos, mob.level().getBlockState(targetPos),
                            mob.level().getBlockState(targetPos), 3);
                    PacketDistributor.sendToPlayersTrackingEntity(mob,
                            new FarmAnimalSyncPacket(mob.getId(), data));
                }
            }
        }
    }

    @Override
    public void stop() {
        targetPos = null;
        mob.getNavigation().stop();
    }

    private BlockPos findNearestTrough() {
        return BlockPos.betweenClosedStream(
                mob.blockPosition().offset(-searchRadius, -2, -searchRadius),
                mob.blockPosition().offset(searchRadius, 2, searchRadius)
        ).filter(pos -> mob.level().getBlockState(pos).is(SurvivalBlocks.FOOD_TROUGH.get()))
                .filter(pos -> mob.level().getBlockEntity(pos) instanceof FoodTroughBlockEntity t && t.hasValidFood(getDietTags()))
                .map(BlockPos::immutable)
                .min(Comparator.comparingDouble(pos -> pos.distSqr(mob.blockPosition())))
                .orElse(null);
    }
}
