package com.kalob.apocalypse_items;

import dev.ghen.thirst.foundation.common.event.RegisterThirstValueEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class ThirstCompat {

    @SubscribeEvent
    public static void onRegisterThirstValues(RegisterThirstValueEvent event) {
        event.addDrink(ModItems.CAN1.get(), 3, 1);
        event.addDrink(ModItems.CAN2.get(), 6, 2);
    }
}
