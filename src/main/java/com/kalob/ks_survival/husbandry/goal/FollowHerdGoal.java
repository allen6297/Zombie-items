package com.kalob.ks_survival.husbandry.goal;

import com.kalob.ks_survival.husbandry.FarmAnimalData;
import com.kalob.ks_survival.init.ModAttachments;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;

import java.util.EnumSet;
import java.util.List;

public class FollowHerdGoal extends Goal {

    private final PathfinderMob mob;
    private final double speed;
    private final int searchRadius;
    private final int stopDistance;
    private Animal leader;

    public FollowHerdGoal(PathfinderMob mob, double speed, int searchRadius, int stopDistance) {
        this.mob = mob;
        this.speed = speed;
        this.searchRadius = searchRadius;
        this.stopDistance = stopDistance;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        FarmAnimalData data = mob.getData(ModAttachments.FARM_ANIMAL.get());
        // Don't follow when hungry, thirsty, sick, or panicking — other goals take priority
        if (data.isHungry() || data.isThirsty() || data.isSick() || data.isPanicking()) return false;

        @SuppressWarnings("unchecked")
        Class<Animal> mobClass = (Class<Animal>) mob.getClass();
        List<Animal> nearby = mob.level()
                .getEntitiesOfClass(mobClass, mob.getBoundingBox().inflate(searchRadius))
                .stream()
                .filter(a -> a != mob)
                .filter(a -> {
                    FarmAnimalData d = a.getData(ModAttachments.FARM_ANIMAL.get());
                    return d.isWellFed() && !d.isStressed() && !d.isPanicking();
                })
                .toList();

        if (nearby.isEmpty()) return false;

        // Follow the most domestic calm animal
        leader = nearby.stream()
                .max((a, b) -> Integer.compare(
                        a.getData(ModAttachments.FARM_ANIMAL.get()).getTameness(),
                        b.getData(ModAttachments.FARM_ANIMAL.get()).getTameness()))
                .orElse(null);
        return leader != null && mob.distanceToSqr(leader) > (double) (stopDistance * stopDistance);
    }

    @Override
    public boolean canContinueToUse() {
        if (leader == null || !leader.isAlive()) return false;
        FarmAnimalData data = mob.getData(ModAttachments.FARM_ANIMAL.get());
        if (data.isHungry() || data.isThirsty() || data.isPanicking()) return false;
        return mob.distanceToSqr(leader) > (double) (stopDistance * stopDistance);
    }

    @Override
    public void start() {
        mob.getNavigation().moveTo(leader, speed);
    }

    @Override
    public void tick() {
        if (leader != null) {
            mob.getLookControl().setLookAt(leader);
            mob.getNavigation().moveTo(leader, speed);
        }
    }

    @Override
    public void stop() {
        leader = null;
        mob.getNavigation().stop();
    }
}
