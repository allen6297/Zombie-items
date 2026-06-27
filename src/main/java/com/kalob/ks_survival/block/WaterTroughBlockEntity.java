package com.kalob.ks_survival.block;

import com.kalob.ks_survival.init.SurvivalBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
            return stack.getFluid().isSame(Fluids.WATER);
        }

        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    public WaterTroughBlockEntity(BlockPos pos, BlockState state) {
        super(SurvivalBlockEntities.WATER_TROUGH.get(), pos, state);
    }

    public IFluidHandler getFluidHandler() {
        return tank;
    }

    public boolean hasWater() {
        return !tank.getFluid().isEmpty() && tank.getFluid().getFluid().isSame(Fluids.WATER);
    }

    public boolean drain(int amount) {
        if (!hasWater()) return false;
        tank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
        return true;
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
