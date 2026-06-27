package com.kalob.ks_survival;

import com.kalob.ks_survival.farming.FarmingEvents;
import com.kalob.ks_survival.init.ModAttachments;
import com.kalob.ks_survival.init.SurvivalConfig;
import com.kalob.ks_survival.init.SurvivalCreativeTabs;
import com.kalob.ks_survival.init.SurvivalItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(KsSurvival.MODID)
public class KsSurvival {

    public static final String MODID = "ks_survival";

    public KsSurvival(IEventBus modEventBus, ModContainer modContainer) {
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        SurvivalItems.ITEMS.register(modEventBus);
        SurvivalCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        NeoForge.EVENT_BUS.register(FarmingEvents.class);

        modContainer.registerConfig(ModConfig.Type.COMMON, SurvivalConfig.SPEC);
    }
}
