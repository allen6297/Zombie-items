package com.kalob.ks_survival.client.model;

import com.kalob.ks_survival.KsSurvival;
import com.kalob.ks_survival.entity.SurvivalCowEntity;
import com.kalob.ks_survival.husbandry.FarmAnimalData;
import com.kalob.ks_survival.husbandry.genetics.Gender;
import com.kalob.ks_survival.init.ModAttachments;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedGeoModel;

public class SurvivalCow extends DefaultedGeoModel<SurvivalCowEntity> {

    public SurvivalCow() {
        super(ResourceLocation.fromNamespaceAndPath(KsSurvival.MODID, "cow"));
    }

    @Override
    protected String subtype() {
        return "cow";
    }

    @Override
    public ResourceLocation getTextureResource(SurvivalCowEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(KsSurvival.MODID, "textures/entity/cow/cow.png");
    }

    @Override
    public void setCustomAnimations(SurvivalCowEntity animatable, long instanceId,
            AnimationState<SurvivalCowEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        if (!animatable.hasData(ModAttachments.FARM_ANIMAL.get())) return;
        FarmAnimalData data = animatable.getData(ModAttachments.FARM_ANIMAL.get());
        boolean isFemale = data.getExpressedGender() == Gender.FEMALE;

        setBoneHidden("udder", !isFemale);
        setBoneHidden("horn_left", isFemale);
        setBoneHidden("horn_right", isFemale);
    }

    private void setBoneHidden(String boneName, boolean hidden) {
        getBone(boneName).ifPresent(bone -> bone.setHidden(hidden));
    }
}
