package com.kalob.ks_survival.compat;

import com.kalob.ks_survival.KsSurvival;
import com.kalob.ks_survival.compat.SereneSeasonsCompat;
import com.kalob.ks_survival.farming.FarmAnimalData;
import com.kalob.ks_survival.farming.genetics.Coat;
import com.kalob.ks_survival.init.ModAttachments;
import com.kalob.ks_survival.init.SurvivalConfig;
import com.kalob.ks_survival.init.SurvivalConfig;
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

    @Override
    public void appendServerData(CompoundTag data, EntityAccessor accessor) {
        if (!(accessor.getEntity() instanceof Animal animal)) return;
        if (!SurvivalConfig.isTrackedAnimal(animal)) return;
        FarmAnimalData d = animal.getData(ModAttachments.FARM_ANIMAL.get());
        data.putInt("hunger", d.getHunger());
        data.putInt("thirst", d.getThirst());
        data.putInt("productivity", d.getProductivity());
        data.putBoolean("stressed", d.isStressed());
        data.putBoolean("wellFed", d.isWellFed());
        data.putBoolean("sick", d.isSick());
        data.putBoolean("overfed", d.isOverfed());
        data.putBoolean("panicking", d.isPanicking());
        data.putInt("tameness", d.getTameness());
        data.putString("tamenessLabel", d.isWild() ? "Wild" : d.isAdjusting() ? "Adjusting" : "Domestic");
        data.putString("coat", d.getExpressedCoat().name());
        var seasons = SurvivalConfig.getBreedingSeasons(animal);
        boolean canBreed = SereneSeasonsCompat.isBreedingSeason(animal.level(), animal, seasons, d.isDomestic());
        data.putBoolean("canBreed", canBreed);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains("hunger")) return;

        tooltip.add(statusLine("Hunger", data.getInt("hunger")));
        tooltip.add(statusLine("Thirst", data.getInt("thirst")));

        int productivity = data.getInt("productivity");
        ChatFormatting prodColor = productivity >= 75 ? ChatFormatting.GREEN
                : productivity >= 40 ? ChatFormatting.YELLOW : ChatFormatting.RED;
        tooltip.add(Component.literal("Productivity: " + productivity + "%").withStyle(prodColor));

        int tameness = data.getInt("tameness");
        String tamenessLabel = data.getString("tamenessLabel");
        ChatFormatting tamenessColor = tameness <= 25 ? ChatFormatting.RED
                : tameness <= 60 ? ChatFormatting.YELLOW : ChatFormatting.GREEN;
        tooltip.add(Component.literal("Tameness: " + tameness + "% (" + tamenessLabel + ")").withStyle(tamenessColor));

        if (data.contains("canBreed") && !data.getBoolean("canBreed")) {
            tooltip.add(Component.literal("✗ Out of breeding season").withStyle(ChatFormatting.DARK_GRAY));
        }

        if (data.contains("coat")) {
            Coat coat = Coat.valueOf(data.getString("coat"));
            if (coat != Coat.NORMAL) {
                String label = switch (coat) {
                    case DARK -> "Dark";
                    case CREAM -> "Cream";
                    case ALBINO -> "Albino";
                    default -> "";
                };
                tooltip.add(Component.literal("Coat: " + label).withStyle(ChatFormatting.GRAY));
            }
        }

        if (data.getBoolean("panicking")) {
            tooltip.add(Component.literal("⚠ Panicking").withStyle(ChatFormatting.YELLOW));
        } else if (data.getBoolean("sick")) {
            tooltip.add(Component.literal("⚠ Sick").withStyle(ChatFormatting.DARK_RED));
        } else if (data.getBoolean("overfed")) {
            tooltip.add(Component.literal("Overfed").withStyle(ChatFormatting.GOLD));
        } else if (data.getBoolean("wellFed")) {
            tooltip.add(Component.literal("Well Fed").withStyle(ChatFormatting.GREEN));
        } else if (data.getBoolean("stressed")) {
            tooltip.add(Component.literal("Stressed").withStyle(ChatFormatting.RED));
        }
    }

    private static Component statusLine(String label, int value) {
        ChatFormatting color = value < FarmAnimalData.stressThreshold() ? ChatFormatting.RED
                : value >= FarmAnimalData.wellFedThreshold() ? ChatFormatting.GREEN
                : ChatFormatting.YELLOW;
        return Component.literal(label + ": " + value + "/" + FarmAnimalData.MAX).withStyle(color);
    }
}
