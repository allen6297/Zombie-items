package com.kalob.ks_survival;

import com.kalob.ks_survival.farming.FarmAnimalSyncPacket;
import com.kalob.ks_survival.farming.FarmingEvents;
import com.kalob.ks_survival.health.BodyPartSyncPacket;
import com.kalob.ks_survival.health.HealthEvents;
import com.kalob.ks_survival.init.ModAttachments;
import com.kalob.ks_survival.init.SurvivalBlockEntities;
import com.kalob.ks_survival.init.SurvivalBlocks;
import com.kalob.ks_survival.init.SurvivalConfig;
import com.kalob.ks_survival.init.SurvivalCreativeTabs;
import com.kalob.ks_survival.init.SurvivalItems;
import com.kalob.ks_survival.init.SurvivalLootModifiers;
import com.kalob.ks_survival.init.SurvivalMenus;
import com.kalob.ks_survival.compat.SurvivalThirstCompat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(KsSurvival.MODID)
public class KsSurvival {

    public static final String MODID = "ks_survival";

    public KsSurvival(IEventBus modEventBus, ModContainer modContainer) {
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        SurvivalLootModifiers.LOOT_MODIFIERS.register(modEventBus);
        SurvivalMenus.MENU_TYPES.register(modEventBus);
        SurvivalBlocks.BLOCKS.register(modEventBus);
        SurvivalBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        SurvivalItems.ITEMS.register(modEventBus);
        SurvivalCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        NeoForge.EVENT_BUS.register(FarmingEvents.class);
        NeoForge.EVENT_BUS.register(HealthEvents.class);
        if (ModList.get().isLoaded("thirst")) {
            NeoForge.EVENT_BUS.register(SurvivalThirstCompat.class);
        }

        modContainer.registerConfig(ModConfig.Type.COMMON, SurvivalConfig.SPEC);
        modEventBus.addListener(this::onConfigReload);
        modEventBus.addListener(this::onRegisterPayloads);
    }

    private void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(FarmAnimalSyncPacket.TYPE, FarmAnimalSyncPacket.STREAM_CODEC, FarmAnimalSyncPacket::handle);
        registrar.playToClient(BodyPartSyncPacket.TYPE, BodyPartSyncPacket.STREAM_CODEC, BodyPartSyncPacket::handle);
    }

    @SubscribeEvent
    private void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SurvivalConfig.SPEC) {
            SurvivalConfig.invalidateCache();
        }
    }
}
