package com.kalob.ks_survival.client.model;

import com.kalob.ks_survival.KsSurvival;
import com.kalob.ks_survival.entity.CowEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AurochsModel extends GeoModel<CowEntity> {

    @Override
    public ResourceLocation getModelResource(CowEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(KsSurvival.MODID, "geo/entity/aurochs.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CowEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(KsSurvival.MODID, "textures/entity/aurochs/aurochs.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CowEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(KsSurvival.MODID, "animations/entity/aurochs.animation.json");
    }
}
