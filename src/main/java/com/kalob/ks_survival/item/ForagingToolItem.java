package com.kalob.ks_survival.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ForagingToolItem extends Item {

    private static final int COOLDOWN_TICKS = 40;

    public ForagingToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();

        if (!isForageable(state)) return InteractionResult.PASS;
        if (player == null) return InteractionResult.PASS;

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        ServerLevel serverLevel = (ServerLevel) level;
        dropForageItems(serverLevel, pos, player);
        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                4, 0.4, 0.2, 0.4, 0.01);

        return InteractionResult.SUCCESS;
    }

    private static boolean isForageable(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.SHORT_GRASS)
                || state.is(Blocks.TALL_GRASS)
                || state.is(Blocks.FERN)
                || state.is(Blocks.LARGE_FERN)
                || state.is(BlockTags.LEAVES);
    }

    private static void dropForageItems(ServerLevel level, BlockPos pos, Player player) {
        var rng = level.getRandom();
        // Which pool to pull from depends on the block type
        BlockState state = level.getBlockState(pos);
        List<Item> pool = state.is(BlockTags.LEAVES)
                ? List.of(Items.STICK, Items.STRING, Items.APPLE, Items.STICK, Items.STICK)
                : List.of(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS,
                           Items.STICK, Items.STRING, Items.SWEET_BERRIES,
                           Items.WHEAT_SEEDS, Items.WHEAT_SEEDS);

        int count = 1 + rng.nextInt(2);
        for (int i = 0; i < count; i++) {
            Item item = pool.get(rng.nextInt(pool.size()));
            ItemEntity ie = new ItemEntity(level,
                    pos.getX() + 0.5 + rng.nextGaussian() * 0.1,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5 + rng.nextGaussian() * 0.1,
                    new ItemStack(item, 1));
            level.addFreshEntity(ie);
        }
    }
}
