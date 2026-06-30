package com.kalob.ks_survival.client.renderer;

import com.kalob.ks_survival.client.model.SurvivalCow;
import com.kalob.ks_survival.entity.SurvivalCowEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SurvivalCowRenderer extends SurvivalAnimalRenderer<SurvivalCowEntity> {

    public SurvivalCowRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SurvivalCow());
    }
}
