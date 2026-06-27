package com.kalob.ks_apocalypse.compat;

import com.kalob.ks_apocalypse.init.Config;
import com.kalob.ks_apocalypse.init.ModItems;
import dev.ghen.thirst.foundation.common.event.RegisterThirstValueEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class ThirstCompat {

    @SubscribeEvent
    public static void onRegisterThirstValues(RegisterThirstValueEvent event) {
        event.addDrink(ModItems.CAN1.get(), Config.CAN1_THIRST.get(), Config.CAN1_QUENCH.get());
        event.addDrink(ModItems.CAN2.get(), Config.CAN2_THIRST.get(), Config.CAN2_QUENCH.get());
    }
}
