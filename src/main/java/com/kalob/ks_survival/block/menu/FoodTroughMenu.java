package com.kalob.ks_survival.block.menu;

import com.kalob.ks_survival.block.FoodTroughBlockEntity;
import com.kalob.ks_survival.init.SurvivalConfig;
import com.kalob.ks_survival.init.SurvivalMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class FoodTroughMenu extends AbstractContainerMenu {

    private final FoodTroughBlockEntity blockEntity;

    public FoodTroughMenu(int containerId, Inventory playerInventory, FoodTroughBlockEntity blockEntity) {
        super(SurvivalMenus.FOOD_TROUGH.get(), containerId);
        this.blockEntity = blockEntity;

        // 3 food trough slots — centered horizontally in 176px wide GUI
        for (int i = 0; i < FoodTroughBlockEntity.SLOTS; i++) {
            addSlot(new SlotItemHandler(blockEntity.getItemHandler(), i, 8 + i * 18, 20));
        }

        // Player inventory (3 rows)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 51 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 109));
        }
    }

    public static FoodTroughMenu fromNetwork(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        var pos = buf.readBlockPos();
        var blockEntity = (FoodTroughBlockEntity) playerInventory.player.level().getBlockEntity(pos);
        return new FoodTroughMenu(containerId, playerInventory, blockEntity);
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(
                blockEntity.getBlockPos().getX() + 0.5,
                blockEntity.getBlockPos().getY() + 0.5,
                blockEntity.getBlockPos().getZ() + 0.5) <= 64;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return result;

        ItemStack stack = slot.getItem();
        result = stack.copy();

        int troughSlots = FoodTroughBlockEntity.SLOTS;
        int invEnd = troughSlots + 27 + 9;

        if (index < troughSlots) {
            // Trough → player inventory
            if (!moveItemStackTo(stack, troughSlots, invEnd, true)) return ItemStack.EMPTY;
        } else {
            // Player inventory → trough (food items or items matching any configured diet tag)
            if (stack.getFoodProperties(null) != null || SurvivalConfig.getAllDietTags().stream().anyMatch(stack::is)) {
                if (!moveItemStackTo(stack, 0, troughSlots, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return result;
    }
}
