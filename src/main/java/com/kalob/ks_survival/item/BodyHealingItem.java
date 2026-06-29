package com.kalob.ks_survival.item;

import com.kalob.ks_survival.health.BodyPart;
import com.kalob.ks_survival.health.BodyPartData;
import com.kalob.ks_survival.health.BodyPartSyncPacket;
import com.kalob.ks_survival.health.HealingAction;
import com.kalob.ks_survival.init.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for any item that applies a {@link HealingAction} to the player's body parts.
 * Subclasses only need to supply the action via {@link #getAction()}.
 */
public abstract class BodyHealingItem extends Item {

    public BodyHealingItem(Properties properties) {
        super(properties);
    }

    public abstract HealingAction getAction();

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return getAction().useDuration;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        BodyPartData data = player.getData(ModAttachments.BODY_PARTS.get());
        if (!getAction().hasTarget(data)) return InteractionResultHolder.fail(player.getItemInHand(hand));
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, Level level, @NotNull LivingEntity entity) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            HealingAction   action = getAction();
            BodyPartData    data   = player.getData(ModAttachments.BODY_PARTS.get());
            BodyPart        part   = action.selectPart(data);

            action.apply(data, part);

            player.setData(ModAttachments.BODY_PARTS.get(), data);
            PacketDistributor.sendToPlayer(player, new BodyPartSyncPacket(player.getId(), data));

            if (!player.getAbilities().instabuild) stack.shrink(1);
        }
        return stack;
    }
}
