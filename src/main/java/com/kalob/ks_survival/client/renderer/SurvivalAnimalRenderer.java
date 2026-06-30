package com.kalob.ks_survival.client.renderer;

import com.kalob.ks_survival.entity.SurvivalAnimalEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Base renderer for all custom survival animals.
 * Extends GeoEntityRenderer so subclasses get GeckoLib animation support for free.
 */
public abstract class SurvivalAnimalRenderer<T extends SurvivalAnimalEntity>
        extends GeoEntityRenderer<T> {

    public SurvivalAnimalRenderer(EntityRendererProvider.Context ctx, GeoModel<T> model) {
        super(ctx, model);
    }
}
