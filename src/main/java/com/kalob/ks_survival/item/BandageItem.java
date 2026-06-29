package com.kalob.ks_survival.item;

import com.kalob.ks_survival.health.HealingAction;
import com.kalob.ks_survival.health.Wound;
import com.kalob.ks_survival.init.SurvivalConfig;

public class BandageItem extends BodyHealingItem {

    public BandageItem(Properties properties) {
        super(properties);
    }

    @Override
    public HealingAction getAction() {
        return HealingAction.builder(SurvivalConfig.BANDAGE_USE_DURATION.get())
                .targets(Wound.BLEEDING)
                .removes(Wound.BLEEDING)
                .restores(SurvivalConfig.BANDAGE_HEAL_AMOUNT.get().intValue())
                .build();
    }
}
