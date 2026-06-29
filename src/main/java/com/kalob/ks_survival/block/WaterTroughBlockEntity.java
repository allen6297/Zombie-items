package com.kalob.ks_survival.block;

import com.kalob.ks_survival.init.SurvivalBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class WaterTroughBlockEntity extends BlockEntity {

    public static final int CAPACITY = 4000;

    private final FluidTank tank = new FluidTank(CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid().is(FluidTags.WATER);
        }

        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    public WaterTroughBlockEntity(BlockPos pos, BlockState state) {
        super(SurvivalBlockEntities.WATER_TROUGH.get(), pos, state);
    }

    public IFluidHandler getFluidHandler() {
        return tank;
    }

    public boolean hasWater() {
        return !tank.getFluid().isEmpty() && tank.getFluid().getFluid().is(FluidTags.WATER);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WaterTroughBlockEntity be) {
        if (!level.isRaining()) return;
        if (level.getGameTime() % 20 != 0) return;
        if (!level.canSeeSky(pos.above())) return;
        if (be.tank.getFluidAmount() >= CAPACITY) return;
        be.tank.fill(new FluidStack(Fluids.WATER, 5), IFluidHandler.FluidAction.EXECUTE);
        level.sendBlockUpdated(pos, state, state, 3);
    }

    public boolean drain(int amount) {
        FluidStack drained = tank.drain(amount, IFluidHandler.FluidAction.SIMULATE);
        if (drained.getAmount() < amount) return false;
        tank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
        return true;
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("fluid", tank.writeToNBT(registries, new CompoundTag()));
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("fluid", tank.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("fluid")) {
            tank.readFromNBT(registries, tag.getCompound("fluid"));
        }
    }
}
