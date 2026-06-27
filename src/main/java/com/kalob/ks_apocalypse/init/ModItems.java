package com.kalob.ks_apocalypse.init;

import com.kalob.ks_apocalypse.KsApocalypse;
import com.kalob.ks_apocalypse.compat.MedSystemCompat;
import com.kalob.ks_apocalypse.item.BandageItem;
import com.kalob.ks_apocalypse.item.ZombieKnifeItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(KsApocalypse.MODID);

    // Block items
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(
            "example_block", ModBlocks.EXAMPLE_BLOCK);

    // Drinks
    public static final DeferredItem<Item> CAN1 = ITEMS.registerSimpleItem("can1",
            new Item.Properties().food(new FoodProperties.Builder()
                    .alwaysEdible().nutrition(1).saturationModifier(2f).build()).stacksTo(16));

    public static final DeferredItem<Item> CAN2 = ITEMS.registerSimpleItem("can2",
            new Item.Properties().food(new FoodProperties.Builder()
                    .alwaysEdible().nutrition(1).saturationModifier(2f).build()).stacksTo(16));

    // Weapons
    public static final DeferredItem<Item> ZOMBIE_KNIFE = ITEMS.register("zombie_knife",
            () -> new ZombieKnifeItem(new Item.Properties()));

    // Healing
    public static final DeferredItem<Item> BANDAGE = ITEMS.register("bandage",
            () -> ModList.get().isLoaded("medsystem")
                    ? MedSystemCompat.createBandage()
                    : new BandageItem(new Item.Properties().stacksTo(8)));
}
