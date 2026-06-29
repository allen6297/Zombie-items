package com.kalob.ks_survival.item;

import com.kalob.ks_survival.farming.FarmAnimalData;
import com.kalob.ks_survival.farming.FarmAnimalSyncPacket;
import com.kalob.ks_survival.init.ModAttachments;
import com.kalob.ks_survival.init.SurvivalConfig;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FeedBagItem extends Item {

    public FeedBagItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        if (!(entity instanceof Animal animal)) return InteractionResult.PASS;
        if (!SurvivalConfig.isTrackedAnimal(animal)) return InteractionResult.PASS;
        if (animal.level().isClientSide()) return InteractionResult.SUCCESS;

        FarmAnimalData data = animal.getData(ModAttachments.FARM_ANIMAL.get());
        data.feed();
        animal.setData(ModAttachments.FARM_ANIMAL.get(), data);
        PacketDistributor.sendToPlayersTrackingEntity(animal, new FarmAnimalSyncPacket(animal.getId(), data));
        animal.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        animal.level().broadcastEntityEvent(animal, (byte) 18);
        return InteractionResult.SUCCESS;
    }
}
