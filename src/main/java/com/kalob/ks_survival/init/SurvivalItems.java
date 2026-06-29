package com.kalob.ks_survival.init;

import com.kalob.ks_survival.KsSurvival;
import com.kalob.ks_survival.item.BandageItem;
import com.kalob.ks_survival.item.FeedBagItem;
import com.kalob.ks_survival.item.MedicineItem;
import com.kalob.ks_survival.item.SplintItem;
import com.kalob.ks_survival.item.TraumaKitItem;
import com.kalob.ks_survival.item.WaterFlaskFilledItem;
import com.kalob.ks_survival.item.WaterFlaskItem;
import com.kalob.ks_survival.item.ZombieKnifeItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SurvivalItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(KsSurvival.MODID);

    public static final DeferredItem<Item> FEED_BAG = ITEMS.register("feed_bag",
            () -> new FeedBagItem(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> WATER_FLASK = ITEMS.register("water_flask",
            () -> new WaterFlaskItem(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> WATER_FLASK_FILLED = ITEMS.register("water_flask_filled",
            () -> new WaterFlaskFilledItem(new Item.Properties().stacksTo(4)));

    public static final DeferredItem<Item> MEDICINE = ITEMS.register("medicine",
            () -> new MedicineItem(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> BANDAGE = ITEMS.register("bandage",
            () -> new BandageItem(new Item.Properties().stacksTo(8)));

    public static final DeferredItem<Item> SPLINT = ITEMS.register("splint",
            () -> new SplintItem(new Item.Properties().stacksTo(8)));

    public static final DeferredItem<Item> TRAUMA_KIT = ITEMS.register("trauma_kit",
            () -> new TraumaKitItem(new Item.Properties().stacksTo(4)));

    public static final DeferredItem<Item> CAN1 = ITEMS.register("can1",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(3).saturationModifier(0.3f).build()).stacksTo(16)));

    public static final DeferredItem<Item> CAN2 = ITEMS.register("can2",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(6).saturationModifier(0.6f).build()).stacksTo(16)));

    public static final DeferredItem<Item> ZOMBIE_KNIFE = ITEMS.register("zombie_knife",
            () -> new ZombieKnifeItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<BlockItem> WATER_TROUGH = ITEMS.registerSimpleBlockItem(SurvivalBlocks.WATER_TROUGH);
    public static final DeferredItem<BlockItem> FOOD_TROUGH = ITEMS.registerSimpleBlockItem(SurvivalBlocks.FOOD_TROUGH);
}
