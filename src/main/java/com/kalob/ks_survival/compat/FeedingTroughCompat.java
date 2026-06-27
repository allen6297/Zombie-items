package com.kalob.ks_survival.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.block.Block;

public class FeedingTroughCompat {

    private static Block troughBlock;
    private static boolean resolved = false;

    private static Block getTroughBlock() {
        if (!resolved) {
            resolved = true;
            troughBlock = BuiltInRegistries.BLOCK
                    .getOptional(ResourceLocation.fromNamespaceAndPath("animal_feeding_trough", "feeding_trough"))
                    .orElse(null);
        }
        return troughBlock;
    }

    public static boolean isNearby(Animal animal) {
        Block trough = getTroughBlock();
        if (trough == null) return false;
        return BlockPos.betweenClosedStream(
                animal.blockPosition().offset(-5, -1, -5),
                animal.blockPosition().offset(5, 1, 5)
        ).anyMatch(pos -> animal.level().getBlockState(pos).is(trough));
    }
}
