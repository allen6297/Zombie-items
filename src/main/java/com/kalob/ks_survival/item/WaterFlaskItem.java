package com.kalob.ks_survival.item;

import com.kalob.ks_survival.block.WaterTroughBlockEntity;
import com.kalob.ks_survival.init.SurvivalBlocks;
import com.kalob.ks_survival.init.SurvivalItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class WaterFlaskItem extends Item {

    public WaterFlaskItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HitResult hit = player.pick(4.0, 0.0f, true);
        if (hit.getType() != HitResult.Type.BLOCK) return InteractionResultHolder.pass(stack);

        BlockPos pos = ((BlockHitResult) hit).getBlockPos();

        // Water cauldron
        BlockState blockState = level.getBlockState(pos);
        if (blockState.is(Blocks.WATER_CAULDRON)) {
            if (level.isClientSide) return InteractionResultHolder.success(stack);
            int cauldronLevel = blockState.getValue(LayeredCauldronBlock.LEVEL);
            if (cauldronLevel > 1) {
                level.setBlock(pos, blockState.setValue(LayeredCauldronBlock.LEVEL, cauldronLevel - 1), 3);
            } else {
                level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
            }
            return doFill(level, player, stack);
        }

        // Water trough
        if (blockState.is(SurvivalBlocks.WATER_TROUGH.get())) {
            if (level.isClientSide) return InteractionResultHolder.success(stack);
            if (!(level.getBlockEntity(pos) instanceof WaterTroughBlockEntity trough) || !trough.drain(250))
                return InteractionResultHolder.fail(stack);
            return doFill(level, player, stack);
        }

        // Natural water source
        FluidState fluid = level.getFluidState(pos);
        if (fluid.is(FluidTags.WATER) && fluid.isSource()) {
            if (level.isClientSide) return InteractionResultHolder.success(stack);
            return doFill(level, player, stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    private InteractionResultHolder<ItemStack> doFill(Level level, Player player, ItemStack stack) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0f, 1.0f);
        ItemStack filled = new ItemStack(SurvivalItems.WATER_FLASK_FILLED.get());
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        if (!player.getInventory().add(filled)) player.drop(filled, false);
        return InteractionResultHolder.success(stack.isEmpty() ? filled : stack);
    }
}
