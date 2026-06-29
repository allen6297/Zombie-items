package com.kalob.ks_apocalypse.init;

import com.kalob.common.CommonItems;
import com.kalob.ks_apocalypse.KsApocalypse;
import com.kalob.ks_apocalypse.compat.MedSystemCompat;
import com.kalob.ks_apocalypse.item.BandageItem;
import com.kalob.ks_apocalypse.item.ZombieKnifeItem;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(KsApocalypse.MODID);

    // Drinks
    public static final DeferredItem<Item> CAN1 = CommonItems.registerFood(ITEMS, "can1", 3, 0.3f, 16);
    public static final DeferredItem<Item> CAN2 = CommonItems.registerFood(ITEMS, "can2", 6, 0.6f, 16);

    // Weapons
    public static final DeferredItem<Item> ZOMBIE_KNIFE = ITEMS.register("zombie_knife",
            () -> new ZombieKnifeItem(new Item.Properties()));

    // Healing
    public static final DeferredItem<Item> BANDAGE = ITEMS.register("bandage",
            () -> ModList.get().isLoaded("medsystem")
                    ? MedSystemCompat.createBandage()
                    : new BandageItem(new Item.Properties().stacksTo(8)));
}
