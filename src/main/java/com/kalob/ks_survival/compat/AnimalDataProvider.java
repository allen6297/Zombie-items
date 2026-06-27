package com.kalob.ks_survival.compat;

import com.kalob.ks_survival.KsSurvival;
import com.kalob.ks_survival.farming.FarmAnimalData;
import com.kalob.ks_survival.init.ModAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Animal;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class AnimalDataProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {

    public static final AnimalDataProvider INSTANCE = new AnimalDataProvider();
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(KsSurvival.MODID, "animal_data");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    // Called server-side: pack attachment data into the Jade lookup tag
    @Override
    public void appendServerData(CompoundTag data, EntityAccessor accessor) {
        if (!(accessor.getEntity() instanceof Animal animal)) return;
        FarmAnimalData farmData = animal.getData(ModAttachments.FARM_ANIMAL.get());
        data.putInt("hunger", farmData.getHunger());
        data.putInt("thirst", farmData.getThirst());
    }

    // Called client-side: read packed data and add tooltip lines
    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains("hunger")) return;
        tooltip.add(statusLine("Hunger", data.getInt("hunger")));
        tooltip.add(statusLine("Thirst", data.getInt("thirst")));
    }

    private static Component statusLine(String label, int value) {
        ChatFormatting color = value < FarmAnimalData.stressThreshold() ? ChatFormatting.RED
                : value >= FarmAnimalData.wellFedThreshold() ? ChatFormatting.GREEN
                : ChatFormatting.YELLOW;
        return Component.literal(label + ": " + value + "/" + FarmAnimalData.MAX).withStyle(color);
    }
}
