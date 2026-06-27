package com.kalob.ks_apocalypse;

import com.kalob.ks_apocalypse.compat.ThirstCompat;
import com.kalob.ks_apocalypse.init.Config;
import com.kalob.ks_apocalypse.init.ModBlocks;
import com.kalob.ks_apocalypse.init.ModCreativeTabs;
import com.kalob.ks_apocalypse.init.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(KsApocalypse.MODID)
public class KsApocalypse {

    public static final String MODID = "ks_apocalypse";

    public KsApocalypse(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        if (ModList.get().isLoaded("thirst")) {
            NeoForge.EVENT_BUS.register(ThirstCompat.class);
        }

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
