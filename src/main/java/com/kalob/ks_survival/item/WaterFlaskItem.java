package com.kalob.ks_survival.item;

import com.kalob.ks_survival.init.ModAttachments;
import com.kalob.ks_survival.farming.FarmAnimalData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WaterFlaskItem extends Item {

    public WaterFlaskItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        if (!(entity instanceof Animal animal)) return InteractionResult.PASS;
        if (animal.level().isClientSide()) return InteractionResult.SUCCESS;

        FarmAnimalData data = animal.getData(ModAttachments.FARM_ANIMAL.get());
        data.water();
        animal.setData(ModAttachments.FARM_ANIMAL.get(), data);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        animal.level().broadcastEntityEvent(animal, (byte) 18); // heart particles
        return InteractionResult.SUCCESS;
    }
}
