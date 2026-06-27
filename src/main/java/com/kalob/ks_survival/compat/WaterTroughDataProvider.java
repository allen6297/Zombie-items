package com.kalob.ks_survival.compat;

import com.kalob.ks_survival.KsSurvival;
import com.kalob.ks_survival.block.WaterTroughBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class WaterTroughDataProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final WaterTroughDataProvider INSTANCE = new WaterTroughDataProvider();
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(KsSurvival.MODID, "water_trough");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof WaterTroughBlockEntity trough)) return;
        data.putBoolean("hasWater", trough.hasWater());
        data.putInt("amount", trough.getFluidHandler().getFluidInTank(0).getAmount());
        data.putInt("capacity", WaterTroughBlockEntity.CAPACITY);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains("amount")) return;
        int amount = data.getInt("amount");
        int capacity = data.getInt("capacity");
        ChatFormatting color = amount == 0 ? ChatFormatting.RED : amount >= capacity ? ChatFormatting.AQUA : ChatFormatting.YELLOW;
        tooltip.add(Component.literal("Water: " + amount + "/" + capacity + " mB").withStyle(color));
    }
}
