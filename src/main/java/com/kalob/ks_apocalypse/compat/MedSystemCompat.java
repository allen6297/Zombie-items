package com.kalob.ks_apocalypse.compat;

import net.minecraft.world.item.Item;
import tnt.tarkovcraft.medsystem.api.heal.HealItemAttributes;
import tnt.tarkovcraft.medsystem.common.init.MedSystemItemComponents;
import tnt.tarkovcraft.medsystem.common.item.HealingItem;

public class MedSystemCompat {

    public static Item createBandage() {
        return new HealingItem(
            new Item.Properties()
                .stacksTo(8)
                .component(
                    MedSystemItemComponents.HEAL_ATTRIBUTES.get(),
                    HealItemAttributes.builder()
                        .healing(60, 1, 6.0f)  // 60-tick cycle, 1 cycle, 6 HP
                        .setMinUseTime(60)
                        .build()
                )
        );
    }
}
