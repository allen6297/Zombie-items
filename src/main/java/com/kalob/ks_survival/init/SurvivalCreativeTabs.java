package com.kalob.ks_survival.init;

import com.kalob.ks_survival.KsSurvival;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import vazkii.patchouli.api.PatchouliAPI;

public class SurvivalCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KsSurvival.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SURVIVAL_TAB =
            CREATIVE_MODE_TABS.register("ks_survival", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ks_survival"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> SurvivalItems.FEED_BAG.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(SurvivalItems.FEED_BAG.get());
                        output.accept(SurvivalItems.WATER_FLASK.get());
                        output.accept(SurvivalItems.WATER_FLASK_FILLED.get());
                        output.accept(SurvivalItems.MEDICINE.get());
                        output.accept(SurvivalItems.WATER_TROUGH.get());
                        output.accept(SurvivalItems.FOOD_TROUGH.get());
                        output.accept(SurvivalItems.CAN1.get());
                        output.accept(SurvivalItems.CAN2.get());
                        output.accept(SurvivalItems.BANDAGE.get());
                        output.accept(SurvivalItems.ZOMBIE_KNIFE.get());
                        output.accept(SurvivalItems.SPLINT.get());
                        if (ModList.get().isLoaded("patchouli")) {
                            output.accept(makeSurvivalGuide());
                        }
                    }).build());

    private static ItemStack makeSurvivalGuide() {
        return PatchouliAPI.get().getBookStack(
                ResourceLocation.fromNamespaceAndPath(KsSurvival.MODID, "survival_guide"));
    }
}
