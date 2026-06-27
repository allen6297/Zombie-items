package com.kalob.ks_survival.init;

import com.kalob.ks_survival.KsSurvival;
import com.kalob.ks_survival.block.WaterTroughBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SurvivalBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, KsSurvival.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WaterTroughBlockEntity>> WATER_TROUGH =
            BLOCK_ENTITY_TYPES.register("water_trough", () ->
                    BlockEntityType.Builder.of(WaterTroughBlockEntity::new, SurvivalBlocks.WATER_TROUGH.get()).build(null));
}
