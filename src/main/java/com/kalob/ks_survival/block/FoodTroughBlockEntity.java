package com.kalob.ks_survival.block;

import com.kalob.ks_survival.block.menu.FoodTroughMenu;
import com.kalob.ks_survival.init.SurvivalBlockEntities;
import com.kalob.ks_survival.init.SurvivalConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FoodTroughBlockEntity extends BlockEntity implements MenuProvider {

    public static final int SLOTS = 9;

    private final ItemStackHandler inventory = new ItemStackHandler(SLOTS) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (stack.getFoodProperties(null) != null) return true;
            return SurvivalConfig.getAllDietTags().stream().anyMatch(stack::is);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public FoodTroughBlockEntity(BlockPos pos, BlockState state) {
        super(SurvivalBlockEntities.FOOD_TROUGH.get(), pos, state);
    }

    public IItemHandler getItemHandler() {
        return inventory;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.ks_survival.food_trough");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new FoodTroughMenu(containerId, playerInventory, this);
    }

    public void writeScreenOpeningData(FriendlyByteBuf buf) {
        buf.writeBlockPos(getBlockPos());
    }

    public boolean hasFood() {
        for (int i = 0; i < SLOTS; i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) return true;
        }
        return false;
    }

    public boolean hasValidFood(Set<TagKey<Item>> dietTags) {
        if (dietTags.isEmpty()) return hasFood();
        for (int i = 0; i < SLOTS; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && dietTags.stream().anyMatch(stack::is)) return true;
        }
        return false;
    }

    public boolean consumeFood() {
        for (int i = 0; i < SLOTS; i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) {
                inventory.extractItem(i, 1, false);
                setChanged();
                return true;
            }
        }
        return false;
    }

    public boolean consumeValidFood(Set<TagKey<Item>> dietTags) {
        if (dietTags.isEmpty()) return consumeFood();
        for (int i = 0; i < SLOTS; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && dietTags.stream().anyMatch(stack::is)) {
                inventory.extractItem(i, 1, false);
                setChanged();
                return true;
            }
        }
        return false;
    }

    public int getTotalFood() {
        int total = 0;
        for (int i = 0; i < SLOTS; i++) total += inventory.getStackInSlot(i).getCount();
        return total;
    }

    public int getMaxFood() {
        return SLOTS * 64;
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("inventory", inventory.serializeNBT(registries));
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            CompoundTag inv = tag.getCompound("inventory");
            int savedSize = inv.getInt("Size");
            if (savedSize == SLOTS) {
                inventory.deserializeNBT(registries, inv);
            } else {
                // Slot count changed — migrate items into new handler, drop any that don't fit
                ItemStackHandler old = new ItemStackHandler(savedSize);
                old.deserializeNBT(registries, inv);
                for (int i = 0; i < Math.min(savedSize, SLOTS); i++) {
                    inventory.setStackInSlot(i, old.getStackInSlot(i));
                }
            }
        }
    }
}
