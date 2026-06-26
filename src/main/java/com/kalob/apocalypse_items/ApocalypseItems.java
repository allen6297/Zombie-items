package com.kalob.apocalypse_items;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ApocalypseItems.MODID)
public class ApocalypseItems {

    public static final String MODID = "apocalypse_items";

    public ApocalypseItems(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        if (ModList.get().isLoaded("thirst")) {
            NeoForge.EVENT_BUS.register(ThirstCompat.class);
        }

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
