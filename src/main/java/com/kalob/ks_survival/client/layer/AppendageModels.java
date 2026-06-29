package com.kalob.ks_survival.client.layer;

import com.kalob.ks_survival.farming.genetics.ClimateVariant;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AppendageModels {

    private static final Map<String, ModelPart> CACHE = new HashMap<>();

    public static void init() {
        // Cow
        put(EntityType.COW, ClimateVariant.COLD,     buildCowColdHorns());
        put(EntityType.COW, ClimateVariant.ARID,     buildCowAridHorns());
        put(EntityType.COW, ClimateVariant.TROPICAL, buildCowTropicalHorns());
        // Pig
        put(EntityType.PIG, ClimateVariant.ARID,     buildPigAridTusks());
        put(EntityType.PIG, ClimateVariant.TROPICAL, buildPigTropicalTusks());
        // Sheep
        put(EntityType.SHEEP, ClimateVariant.COLD,   buildSheepColdHorns());
        put(EntityType.SHEEP, ClimateVariant.ARID,   buildSheepAridHorns());
        // Goat appendages intentionally omitted — vanilla already renders goat horns
    }

    public static ModelPart get(EntityType<?> type, ClimateVariant climate) {
        return CACHE.get(key(type, climate));
    }

    private static void put(EntityType<?> type, ClimateVariant climate, ModelPart model) {
        CACHE.put(key(type, climate), model);
    }

    private static String key(EntityType<?> type, ClimateVariant climate) {
        return net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(type).toString()
                + "/" + climate.name();
    }

    // --- Baking helpers ---

    private static ModelPart bake(Consumer<PartDefinition> builder) {
        MeshDefinition mesh = new MeshDefinition();
        builder.accept(mesh.getRoot());
        return LayerDefinition.create(mesh, 16, 16).bakeRoot();
    }

    // All positions are HEAD-LOCAL (origin = head pivot after translateAndRotate).
    // Cow/sheep head cube ≈ (-4,-4,-6) to (4,4,0): top=y=-4, sides=x=±4, snout=z=-6.
    // Pig head cube ≈ (-4,-4,-8) to (4,4,0): snout tip at z=-8.

    // --- Cow ---

    private static ModelPart buildCowColdHorns() {
        return bake(root -> {
            root.addOrReplaceChild("horn_l", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-1f, -5f, -1f, 2, 5, 2),
                    PartPose.offsetAndRotation(-3.5f, -4f, -1f, 0f, 0f, -0.4f));
            root.addOrReplaceChild("horn_r", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-1f, -5f, -1f, 2, 5, 2),
                    PartPose.offsetAndRotation(3.5f, -4f, -1f, 0f, 0f, 0.4f));
        });
    }

    private static ModelPart buildCowAridHorns() {
        return bake(root -> {
            root.addOrReplaceChild("horn_l", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-1f, -8f, -1f, 2, 8, 2),
                    PartPose.offsetAndRotation(-3.5f, -4f, -1f, 0f, 0f, -0.15f));
            root.addOrReplaceChild("horn_r", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-1f, -8f, -1f, 2, 8, 2),
                    PartPose.offsetAndRotation(3.5f, -4f, -1f, 0f, 0f, 0.15f));
        });
    }

    private static ModelPart buildCowTropicalHorns() {
        return bake(root -> {
            root.addOrReplaceChild("horn_l", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-1f, -2f, -1f, 2, 2, 2),
                    PartPose.offsetAndRotation(-3f, -4f, -1f, 0f, 0f, -0.2f));
            root.addOrReplaceChild("horn_r", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-1f, -2f, -1f, 2, 2, 2),
                    PartPose.offsetAndRotation(3f, -4f, -1f, 0f, 0f, 0.2f));
        });
    }

    // --- Pig ---

    private static ModelPart buildPigAridTusks() {
        return bake(root -> {
            root.addOrReplaceChild("tusk_l", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-1f, 0f, -5f, 2, 2, 5),
                    PartPose.offsetAndRotation(-2f, 2f, -4f, 0.25f, -0.2f, 0f));
            root.addOrReplaceChild("tusk_r", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-1f, 0f, -5f, 2, 2, 5),
                    PartPose.offsetAndRotation(2f, 2f, -4f, 0.25f, 0.2f, 0f));
        });
    }

    private static ModelPart buildPigTropicalTusks() {
        return bake(root -> {
            root.addOrReplaceChild("tusk_l", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-0.5f, 0f, -3f, 1, 1, 3),
                    PartPose.offsetAndRotation(-2f, 2f, -4f, 0.1f, -0.1f, 0f));
            root.addOrReplaceChild("tusk_r", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-0.5f, 0f, -3f, 1, 1, 3),
                    PartPose.offsetAndRotation(2f, 2f, -4f, 0.1f, 0.1f, 0f));
        });
    }

    // --- Sheep ---

    private static ModelPart buildSheepColdHorns() {
        return bake(root -> {
            root.addOrReplaceChild("horn_l", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-1f, -4f, -1f, 2, 4, 2),
                    PartPose.offsetAndRotation(-3f, -2f, -1f, 0.2f, 0f, -0.5f));
            root.addOrReplaceChild("horn_r", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-1f, -4f, -1f, 2, 4, 2),
                    PartPose.offsetAndRotation(3f, -2f, -1f, 0.2f, 0f, 0.5f));
        });
    }

    private static ModelPart buildSheepAridHorns() {
        return bake(root -> {
            root.addOrReplaceChild("horn_l", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-1f, -5f, -1f, 2, 5, 2),
                    PartPose.offsetAndRotation(-2.5f, -3f, -1f, -0.15f, 0f, -0.2f));
            root.addOrReplaceChild("horn_r", CubeListBuilder.create()
                    .texOffs(0, 0).addBox(-1f, -5f, -1f, 2, 5, 2),
                    PartPose.offsetAndRotation(2.5f, -3f, -1f, -0.15f, 0f, 0.2f));
        });
    }
}
