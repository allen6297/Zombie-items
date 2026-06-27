package com.kalob.ks_survival.init;

import com.kalob.ks_survival.KsSurvival;
import com.kalob.ks_survival.block.WaterTroughBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SurvivalBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(KsSurvival.MODID);

    public static final DeferredBlock<WaterTroughBlock> WATER_TROUGH = BLOCKS.register("water_trough",
            () -> new WaterTroughBlock(BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.WOOD).noOcclusion()));
}
