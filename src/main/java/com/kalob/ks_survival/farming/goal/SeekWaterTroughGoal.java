package com.kalob.ks_survival.farming.goal;

import com.kalob.ks_survival.block.WaterTroughBlockEntity;
import com.kalob.ks_survival.farming.FarmAnimalData;
import com.kalob.ks_survival.init.ModAttachments;
import com.kalob.ks_survival.init.SurvivalBlocks;
import com.kalob.ks_survival.init.SurvivalConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.Comparator;
import java.util.EnumSet;

public class SeekWaterTroughGoal extends Goal {

    private final PathfinderMob mob;
    private final int searchRadius;
    private final double speed;
    private BlockPos targetPos;
    private int searchCooldown = 0;

    public SeekWaterTroughGoal(PathfinderMob mob, int searchRadius, double speed) {
        this.mob = mob;
        this.searchRadius = searchRadius;
        this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (searchCooldown > 0) { searchCooldown--; return false; }
        FarmAnimalData data = mob.getData(ModAttachments.FARM_ANIMAL.get());
        if (!data.isThirsty()) return false;
        targetPos = findNearestTrough();
        searchCooldown = 100;
        return targetPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (targetPos == null) return false;
        if (!mob.level().getBlockState(targetPos).is(SurvivalBlocks.WATER_TROUGH.get())) return false;
        if (mob.level().getBlockEntity(targetPos) instanceof WaterTroughBlockEntity trough && !trough.hasWater()) return false;
        return mob.getData(ModAttachments.FARM_ANIMAL.get()).isThirsty();
    }

    @Override
    public void start() {
        mob.getNavigation().moveTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, speed);
    }

    @Override
    public void tick() {
        if (targetPos == null) return;
        mob.getLookControl().setLookAt(targetPos.getX(), targetPos.getY(), targetPos.getZ());

        if (mob.distanceToSqr(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5) < 2.25) {
            if (mob.level().getBlockEntity(targetPos) instanceof WaterTroughBlockEntity trough) {
                FarmAnimalData data = mob.getData(ModAttachments.FARM_ANIMAL.get());
                if (data.isThirsty() && trough.drain(SurvivalConfig.getDrinkAmount(mob))) {
                    data.water();
                    mob.setData(ModAttachments.FARM_ANIMAL.get(), data);
                    mob.level().broadcastEntityEvent(mob, (byte) 18);
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
        ).filter(pos -> mob.level().getBlockState(pos).is(SurvivalBlocks.WATER_TROUGH.get()))
                .map(BlockPos::immutable)
                .min(Comparator.comparingDouble(pos -> pos.distSqr(mob.blockPosition())))
                .orElse(null);
    }
}
