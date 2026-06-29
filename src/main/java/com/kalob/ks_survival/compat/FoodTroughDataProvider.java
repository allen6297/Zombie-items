package com.kalob.ks_survival.compat;

import com.kalob.ks_survival.KsSurvival;
import com.kalob.ks_survival.block.FoodTroughBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class FoodTroughDataProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final FoodTroughDataProvider INSTANCE = new FoodTroughDataProvider();
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(KsSurvival.MODID, "food_trough");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof FoodTroughBlockEntity trough)) return;
        data.putInt("food", trough.getTotalFood());
        data.putInt("maxFood", trough.getMaxFood());
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains("food")) return;
        int food = data.getInt("food");
        int max = data.getInt("maxFood");
        ChatFormatting color = food == 0 ? ChatFormatting.RED : food >= max ? ChatFormatting.GREEN : ChatFormatting.YELLOW;
        tooltip.add(Component.literal("Food: " + food + "/" + max).withStyle(color));
    }
}
