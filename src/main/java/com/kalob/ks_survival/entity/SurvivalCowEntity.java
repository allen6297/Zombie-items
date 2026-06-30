package com.kalob.ks_survival.entity;

import com.kalob.ks_survival.init.SurvivalEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SurvivalCowEntity extends SurvivalAnimalEntity {

    public SurvivalCowEntity(EntityType<? extends SurvivalCowEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBaseAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }


    @Override
    public @Nullable SurvivalCowEntity getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob mate) {
        return SurvivalEntities.COW.get().create(level);
    }

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.COW_AMBIENT; }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) { return SoundEvents.COW_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.COW_DEATH; }

    @Override
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState state) {
        playSound(SoundEvents.COW_STEP, 0.15f, 1.0f);
    }
}
