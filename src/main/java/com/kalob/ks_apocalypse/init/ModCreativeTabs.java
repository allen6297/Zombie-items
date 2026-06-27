package com.kalob.ks_apocalypse.init;

import com.kalob.ks_apocalypse.KsApocalypse;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KsApocalypse.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> APOCALYPSE_ITEMS_TAB =
            CREATIVE_MODE_TABS.register("ks_apocalypse", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ks_apocalypse"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> ModItems.CAN1.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.CAN1.get());
                        output.accept(ModItems.CAN2.get());
                        output.accept(ModItems.BANDAGE.get());
                        output.accept(ModBlocks.EXAMPLE_BLOCK.get());
                    }).build());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> APOCALYPSE_WEAPONS_TAB=
            CREATIVE_MODE_TABS.register("apocalypse_weapons", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.apocalypse_weapons"))
                    .withTabsBefore(APOCALYPSE_ITEMS_TAB.getKey())
                    .icon(() -> ModItems.ZOMBIE_KNIFE.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.ZOMBIE_KNIFE.get());
                    }).build());
}
