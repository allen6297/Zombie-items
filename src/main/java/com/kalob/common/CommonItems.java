package com.kalob.common;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CommonItems {

    public static DeferredItem<Item> registerFood(DeferredRegister.Items items, String name,
                                                   int nutrition, float saturation, int stackSize) {
        return items.registerSimpleItem(name, new Item.Properties()
                .food(new FoodProperties.Builder()
                        .alwaysEdible()
                        .nutrition(nutrition)
                        .saturationModifier(saturation)
                        .build())
                .stacksTo(stackSize));
    }

    public static DeferredItem<Item> registerSimple(DeferredRegister.Items items, String name, int stackSize) {
        return items.registerSimpleItem(name, new Item.Properties().stacksTo(stackSize));
    }
}
