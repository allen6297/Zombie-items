package com.kalob.ks_survival.init;

import com.kalob.ks_survival.KsSurvival;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

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
                        output.accept(SurvivalItems.WATER_TROUGH.get());
                        output.accept(SurvivalItems.BUTCHER_KNIFE.get());
                        output.accept(SurvivalItems.SALT.get());
                        output.accept(SurvivalItems.SALTED_MEAT.get());
                        BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath("animal_feeding_trough", "feeding_trough"))
                                .ifPresent(output::accept);
                    }).build());
}
