package com.kalob.ks_survival.client.layer;

import com.kalob.ks_survival.husbandry.FarmAnimalData;
import com.kalob.ks_survival.husbandry.genetics.ClimateVariant;
import com.kalob.ks_survival.init.ModAttachments;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Animal;
import org.jetbrains.annotations.NotNull;

public class AnimalAppendageLayer<T extends Animal, M extends EntityModel<T>> extends RenderLayer<T, M> {

    // Horn/tusk color: warm ivory-tan. Artists can swap per species by extending this layer.
    private static final int HORN_ARGB  = 0xFF_C8_AA_78; // tan horn
    private static final int TUSK_ARGB  = 0xFF_F0_E6_C8; // ivory tusk

    private static final ResourceLocation WHITE =
            ResourceLocation.fromNamespaceAndPath("ks_survival", "textures/entity/coat/white.png");

    public AnimalAppendageLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight,
                       T entity, float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.hasData(ModAttachments.FARM_ANIMAL.get())) return;
        FarmAnimalData data = entity.getData(ModAttachments.FARM_ANIMAL.get());
        ClimateVariant climate = data.getExpressedClimate();
        if (climate == ClimateVariant.TEMPERATE) return;

        ModelPart appendage = AppendageModels.get(entity.getType(), climate);
        if (appendage == null) return;

        int color = isPig(entity) ? TUSK_ARGB : HORN_ARGB;
        var consumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(WHITE));
        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0f);

        // Child to head bone so appendages follow head rotation
        ModelPart head = getHeadPart(getParentModel());
        if (head != null) {
            poseStack.pushPose();
            head.translateAndRotate(poseStack);
            appendage.render(poseStack, consumer, packedLight, overlay, color);
            poseStack.popPose();
        } else {
            // Fallback: render at model root if head bone not found
            appendage.render(poseStack, consumer, packedLight, overlay, color);
        }
    }

    private static ModelPart getHeadPart(EntityModel<?> model) {
        if (!(model instanceof HierarchicalModel<?> h)) return null;
        try {
            return h.root().getChild("head");
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isPig(Animal animal) {
        return animal.getType() == net.minecraft.world.entity.EntityType.PIG;
    }
}
