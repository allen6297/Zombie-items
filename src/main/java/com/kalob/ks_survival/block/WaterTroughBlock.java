package com.kalob.ks_survival.block;

import com.kalob.ks_survival.init.SurvivalBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WaterTroughBlock extends BaseEntityBlock {

    public static final MapCodec<WaterTroughBlock> CODEC = simpleCodec(WaterTroughBlock::new);

    public WaterTroughBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new WaterTroughBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, SurvivalBlockEntities.WATER_TROUGH.get(), WaterTroughBlockEntity::serverTick);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected net.minecraft.world.@NotNull ItemInteractionResult useItemOn(net.minecraft.world.item.@NotNull ItemStack stack, @NotNull BlockState state,
                                                                           Level level, @NotNull BlockPos pos, @NotNull Player player,
                                                                           net.minecraft.world.@NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof WaterTroughBlockEntity trough) {
            if (FluidUtil.interactWithFluidHandler(player, hand, trough.getFluidHandler())) {
                return net.minecraft.world.ItemInteractionResult.SUCCESS;
            }
        }
        return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
