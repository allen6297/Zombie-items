package com.kalob.ks_survival.compat;

import com.kalob.ks_survival.init.SurvivalConfig;
import com.kalob.ks_survival.init.SurvivalItems;
import dev.ghen.thirst.foundation.common.event.RegisterThirstValueEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class SurvivalThirstCompat {

    @SubscribeEvent
    public static void onRegisterThirstValues(RegisterThirstValueEvent event) {
        event.addDrink(SurvivalItems.CAN1.get(), SurvivalConfig.CAN1_THIRST.get(), SurvivalConfig.CAN1_QUENCH.get());
        event.addDrink(SurvivalItems.CAN2.get(), SurvivalConfig.CAN2_THIRST.get(), SurvivalConfig.CAN2_QUENCH.get());
    }
}
