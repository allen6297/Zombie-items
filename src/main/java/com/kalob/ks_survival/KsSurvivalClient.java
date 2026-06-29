package com.kalob.ks_survival;

import com.kalob.ks_survival.block.screen.FoodTroughScreen;
import com.kalob.ks_survival.client.layer.AnimalAppendageLayer;
import com.kalob.ks_survival.client.layer.AnimalCoatLayer;
import com.kalob.ks_survival.client.layer.AppendageModels;
import com.kalob.ks_survival.health.HealthHudOverlay;
import com.kalob.ks_survival.init.SurvivalMenus;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Objects;

@Mod(value = KsSurvival.MODID, dist = Dist.CLIENT)
public class KsSurvivalClient {

    public KsSurvivalClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        var bus = Objects.requireNonNull(container.getEventBus());
        bus.addListener(this::onRegisterMenuScreens);
        bus.addListener(this::onAddRenderLayers);
        bus.addListener(this::onRegisterGuiLayers);
        NeoForge.EVENT_BUS.addListener(this::onRenderGuiLayer);
    }

    private void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(SurvivalMenus.FOOD_TROUGH.get(), FoodTroughScreen::new);
    }

    private void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR,
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ks_survival", "health_hud"),
                HealthHudOverlay::onRenderHud);
    }

    private void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {
        if (event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)) {
            event.setCanceled(true);
        }
    }

    @SuppressWarnings("unchecked")
    private void onAddRenderLayers(EntityRenderersEvent.AddLayers event) {
        AppendageModels.init();
        for (EntityType<?> type : new EntityType<?>[]{ EntityType.COW, EntityType.PIG,
                EntityType.CHICKEN, EntityType.SHEEP, EntityType.GOAT }) {
            addLayers(event, (EntityType<Animal>) type);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Animal> void addLayers(EntityRenderersEvent.AddLayers event, EntityType<T> type) {
        var renderer = event.getRenderer(type);
        if (renderer instanceof net.minecraft.client.renderer.entity.LivingEntityRenderer<?, ?> ler) {
            var cast = (net.minecraft.client.renderer.entity.LivingEntityRenderer<T, EntityModel<T>>) ler;
            cast.addLayer(new AnimalCoatLayer<>(cast));
            cast.addLayer(new AnimalAppendageLayer<>(cast));
        }
    }
}
