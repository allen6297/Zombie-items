package com.kalob.ks_survival.item;

import com.kalob.ks_survival.health.HealingAction;
import com.kalob.ks_survival.health.Wound;
import com.kalob.ks_survival.init.SurvivalConfig;

public class TraumaKitItem extends BodyHealingItem {

    public TraumaKitItem(Properties properties) {
        super(properties);
    }

    @Override
    public HealingAction getAction() {
        return HealingAction.builder(SurvivalConfig.TRAUMA_KIT_USE_DURATION.get())
                .allParts()
                .removes(Wound.BLEEDING)
                .removes(Wound.SEVERE_BLEEDING)
                .removes(Wound.FRACTURE)
                .restores(SurvivalConfig.TRAUMA_KIT_HEAL_AMOUNT.get())
                .build();
    }
}
