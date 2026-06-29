package com.kalob.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class AddItemsModifier extends LootModifier {

    public static final MapCodec<AddItemsModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
        codecStart(inst).and(inst.group(
            ResourceLocation.CODEC.fieldOf("item").forGetter(m -> m.item),
            MapCodec.unit(1.0f).codec().optionalFieldOf("chance", 1.0f).forGetter(m -> m.chance),
            MapCodec.unit(1).codec().optionalFieldOf("min_count", 1).forGetter(m -> m.minCount),
            MapCodec.unit(1).codec().optionalFieldOf("max_count", 1).forGetter(m -> m.maxCount)
        )).apply(inst, AddItemsModifier::new)
    );

    private final ResourceLocation item;
    private final float chance;
    private final int minCount;
    private final int maxCount;

    public AddItemsModifier(LootItemCondition[] conditions, ResourceLocation item, float chance, int minCount, int maxCount) {
        super(conditions);
        this.item = item;
        this.chance = chance;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() < chance) {
            int count = minCount == maxCount ? minCount
                    : minCount + context.getRandom().nextInt(maxCount - minCount + 1);
            var entry = BuiltInRegistries.ITEM.getOptional(item);
            entry.ifPresent(i -> generatedLoot.add(new ItemStack(i, count)));
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
