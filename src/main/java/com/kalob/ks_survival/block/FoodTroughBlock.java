package com.kalob.ks_survival.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FoodTroughBlock extends BaseEntityBlock {

    public static final MapCodec<FoodTroughBlock> CODEC = simpleCodec(FoodTroughBlock::new);

    public FoodTroughBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new FoodTroughBlockEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state,
                                                       @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player,
                                                       @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof FoodTroughBlockEntity trough && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(trough, trough::writeScreenOpeningData);
        }
        return ItemInteractionResult.SUCCESS;
    }
}
