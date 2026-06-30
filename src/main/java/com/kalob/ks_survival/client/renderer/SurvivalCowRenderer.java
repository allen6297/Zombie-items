package com.kalob.ks_survival.client.renderer;

import com.kalob.ks_survival.client.model.AurochsModel;
import com.kalob.ks_survival.entity.CowEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SurvivalCowRenderer extends SurvivalAnimalRenderer<CowEntity> {

    public SurvivalCowRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new AurochsModel());
    }
}
