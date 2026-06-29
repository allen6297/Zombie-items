package com.kalob.ks_survival;

import com.kalob.ks_survival.block.screen.FoodTroughScreen;
import com.kalob.ks_survival.client.layer.AnimalCoatLayer;
import com.kalob.ks_survival.init.SurvivalMenus;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.Objects;

@Mod(value = KsSurvival.MODID, dist = Dist.CLIENT)
public class KsSurvivalClient {

    public KsSurvivalClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        var bus = Objects.requireNonNull(container.getEventBus());
        bus.addListener(this::onRegisterMenuScreens);
        bus.addListener(this::onAddRenderLayers);
    }

    private void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(SurvivalMenus.FOOD_TROUGH.get(), FoodTroughScreen::new);
    }

    @SuppressWarnings("unchecked")
    private void onAddRenderLayers(EntityRenderersEvent.AddLayers event) {
        addCoatLayer(event, EntityType.COW);
        addCoatLayer(event, EntityType.PIG);
        addCoatLayer(event, EntityType.CHICKEN);
        addCoatLayer(event, EntityType.SHEEP);
        addCoatLayer(event, EntityType.GOAT);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Animal> void addCoatLayer(EntityRenderersEvent.AddLayers event, EntityType<T> type) {
        var renderer = event.getRenderer(type);
        if (renderer instanceof net.minecraft.client.renderer.entity.LivingEntityRenderer<?, ?> ler) {
            var cast = (net.minecraft.client.renderer.entity.LivingEntityRenderer<T, EntityModel<T>>) ler;
            cast.addLayer(new AnimalCoatLayer<>(cast));
        }
    }
}
