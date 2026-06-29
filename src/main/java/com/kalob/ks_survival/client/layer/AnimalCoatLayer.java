package com.kalob.ks_survival.client.layer;

import com.kalob.ks_survival.farming.FarmAnimalData;
import com.kalob.ks_survival.farming.genetics.Coat;
import com.kalob.ks_survival.init.ModAttachments;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Animal;

public class AnimalCoatLayer<T extends Animal, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation OVERLAY =
            ResourceLocation.fromNamespaceAndPath("ks_survival", "textures/entity/coat/white.png");

    public AnimalCoatLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       T entity, float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.hasData(ModAttachments.FARM_ANIMAL.get())) return;
        FarmAnimalData data = entity.getData(ModAttachments.FARM_ANIMAL.get());
        Coat coat = data.getExpressedCoat();
        if (coat == Coat.NORMAL) return;

        getParentModel().renderToBuffer(
                poseStack,
                bufferSource.getBuffer(RenderType.entityTranslucentCull(OVERLAY)),
                packedLight,
                LivingEntityRenderer.getOverlayCoords(entity, 0.0f),
                coat.getARGB());
    }
}
