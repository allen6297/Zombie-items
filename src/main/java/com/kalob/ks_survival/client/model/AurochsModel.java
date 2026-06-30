package com.kalob.ks_survival.client.model;

import com.kalob.ks_survival.KsSurvival;
import com.kalob.ks_survival.entity.SurvivalCowEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedGeoModel;

public class AurochsModel extends DefaultedGeoModel<SurvivalCowEntity> {

    public AurochsModel() {
        super(ResourceLocation.fromNamespaceAndPath(KsSurvival.MODID, "cow"));
    }

    @Override
    protected String subtype() {
        return "";
    }
}
