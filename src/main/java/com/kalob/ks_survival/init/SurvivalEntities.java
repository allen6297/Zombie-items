package com.kalob.ks_survival.init;

import com.kalob.ks_survival.KsSurvival;
import com.kalob.ks_survival.entity.CowEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

public class SurvivalEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, KsSurvival.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<CowEntity>> COW =
            ENTITY_TYPES.register("aurochs", () -> EntityType.Builder
                    .<CowEntity>of(CowEntity::new, MobCategory.CREATURE)
                    .sized(0.9f, 1.4f)
                    .clientTrackingRange(10)
                    .build("ks_survival:aurochs"));
}
