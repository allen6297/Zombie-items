package com.kalob.ks_survival.init;

import com.kalob.ks_survival.KsSurvival;
import com.kalob.ks_survival.item.FeedBagItem;
import com.kalob.ks_survival.item.MedicineItem;
import com.kalob.ks_survival.item.WaterFlaskFilledItem;
import com.kalob.ks_survival.item.WaterFlaskItem;
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

    public static final DeferredItem<BlockItem> WATER_TROUGH = ITEMS.registerSimpleBlockItem(SurvivalBlocks.WATER_TROUGH);
    public static final DeferredItem<BlockItem> FOOD_TROUGH = ITEMS.registerSimpleBlockItem(SurvivalBlocks.FOOD_TROUGH);

}
