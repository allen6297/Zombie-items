package com.kalob.ks_survival.init;

import com.kalob.ks_survival.KsSurvival;
import com.kalob.ks_survival.item.ButcherKnifeItem;
import com.kalob.ks_survival.item.FeedBagItem;
import com.kalob.ks_survival.item.WaterFlaskItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SurvivalItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(KsSurvival.MODID);

    public static final DeferredItem<Item> FEED_BAG = ITEMS.register("feed_bag",
            () -> new FeedBagItem(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> WATER_FLASK = ITEMS.register("water_flask",
            () -> new WaterFlaskItem(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> BUTCHER_KNIFE = ITEMS.register("butcher_knife",
            () -> new ButcherKnifeItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> SALT = ITEMS.registerSimpleItem("salt",
            new Item.Properties().stacksTo(64));

    public static final DeferredItem<Item> SALTED_MEAT = ITEMS.registerSimpleItem("salted_meat",
            new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(4).saturationModifier(0.6f).build()).stacksTo(16));
}
