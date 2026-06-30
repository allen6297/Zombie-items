package com.kalob.ks_survival.entity;

import com.kalob.ks_survival.farming.goal.FollowHerdGoal;
import com.kalob.ks_survival.farming.goal.SeekFoodTroughGoal;
import com.kalob.ks_survival.farming.goal.SeekWaterTroughGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class SurvivalAnimalEntity extends Animal implements GeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    protected SurvivalAnimalEntity(EntityType<? extends SurvivalAnimalEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createBaseAttributes() {
        return net.minecraft.world.entity.Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    /** The item(s) this animal is tempted by and can breed with. */
    protected abstract Ingredient getTemptFood();

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 2.0));
        goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        goalSelector.addGoal(3, new TemptGoal(this, 1.25, getTemptFood(), false));
        goalSelector.addGoal(4, new SeekWaterTroughGoal(this, 16, 1.0));
        goalSelector.addGoal(4, new SeekFoodTroughGoal(this, 16, 1.0));
        goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
        goalSelector.addGoal(6, new FollowHerdGoal(this, 1.0, 12, 4));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0f));
        goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    // --- GeckoLib ---

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, state -> {
            if (state.isMoving()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("animation.walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("animation.idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public boolean isFood(@NotNull ItemStack stack) {
        return false;
    }
}
