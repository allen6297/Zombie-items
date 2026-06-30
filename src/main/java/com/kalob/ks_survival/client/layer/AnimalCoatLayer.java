package com.kalob.ks_survival.client.layer;

import com.kalob.ks_survival.husbandry.FarmAnimalData;
import com.kalob.ks_survival.husbandry.genetics.Coat;
import com.kalob.ks_survival.husbandry.genetics.Pattern;
import com.kalob.ks_survival.init.ModAttachments;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Animal;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AnimalCoatLayer<T extends Animal, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation WHITE =
            ResourceLocation.fromNamespaceAndPath("ks_survival", "textures/entity/coat/white.png");

    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new HashMap<>();

    public AnimalCoatLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight,
                       T entity, float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.hasData(ModAttachments.FARM_ANIMAL.get())) return;
        FarmAnimalData data = entity.getData(ModAttachments.FARM_ANIMAL.get());
        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0f);

        // Coat color layer
        Coat coat = data.getExpressedCoat();
        if (coat != Coat.NORMAL) {
            ResourceLocation coatTex = resolveCoatTexture(entity, coat);
            getParentModel().renderToBuffer(poseStack,
                    bufferSource.getBuffer(RenderType.entityTranslucentCull(coatTex)),
                    packedLight, overlay, coat.getARGB());
        }

        // Pattern layer rendered on top of the coat
        Pattern pattern = data.getExpressedPattern();
        if (pattern != Pattern.SOLID) {
            ResourceLocation patternTex = resolvePatternTexture(entity, pattern);
            getParentModel().renderToBuffer(poseStack,
                    bufferSource.getBuffer(RenderType.entityTranslucentCull(patternTex)),
                    packedLight, overlay, pattern.getARGB());
        }
    }

    // Looks for textures/entity/coat/{species}/{coat_lower}.png, falls back to white tint.
    private static <T extends Animal> ResourceLocation resolveCoatTexture(T entity, Coat coat) {
        String species = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getPath();
        String key = "coat/" + species + "/" + coat.name().toLowerCase();
        return TEXTURE_CACHE.computeIfAbsent(key, k -> resolve("textures/entity/" + k + ".png"));
    }

    // Looks for textures/entity/pattern/{species}/{pattern_lower}.png,
    // then textures/entity/pattern/{pattern_lower}.png, then falls back to white tint.
    private static <T extends Animal> ResourceLocation resolvePatternTexture(T entity, Pattern pattern) {
        String species = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getPath();
        String patName = pattern.name().toLowerCase();
        String speciesKey = "pattern/" + species + "/" + patName;
        return TEXTURE_CACHE.computeIfAbsent(speciesKey, k -> {
            ResourceLocation specific = resolve("textures/entity/" + k + ".png");
            if (specific != WHITE) return specific;
            // Fall back to generic pattern texture
            return resolve("textures/entity/pattern/" + patName + ".png");
        });
    }

    private static ResourceLocation resolve(String path) {
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("ks_survival", path);
        var manager = net.minecraft.client.Minecraft.getInstance().getResourceManager();
        return manager.getResource(loc).isPresent() ? loc : WHITE;
    }
}
