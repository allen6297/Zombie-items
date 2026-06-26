package com.kalob.apocalypse_items;

import com.kalob.apocalypse_items.item.BandageItem;
import net.minecraft.world.item.Item;

public class FirstAidCompat {

    public static Item createBandage() {
        return new BandageItem(new Item.Properties());
    }
}
