package com.kalob.ks_survival.item;

import com.kalob.ks_survival.health.HealingAction;
import com.kalob.ks_survival.health.Wound;

public class SplintItem extends BodyHealingItem {

    public SplintItem(Properties properties) {
        super(properties);
    }

    @Override
    public HealingAction getAction() {
        return HealingAction.builder(80)
                .targets(Wound.FRACTURE)
                .removes(Wound.FRACTURE)
                .restores(8)
                .build();
    }
}
