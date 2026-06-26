package com.kalob.apocalypse_items.item;

import ichttt.mods.firstaid.api.damagesystem.AbstractPartHealer;
import ichttt.mods.firstaid.api.healing.ItemHealing;
import ichttt.mods.firstaid.common.damagesystem.PartHealer;
import net.minecraft.world.item.ItemStack;

public class BandageItem extends ItemHealing {

    public BandageItem(Properties properties) {
        super(properties.stacksTo(8), stack -> null, stack -> 0);
    }

    @Override
    public AbstractPartHealer createNewHealer(ItemStack stack) {
        // ticksPerHeal=20 (1 per second), maxHeal=6 pulses
        return new PartHealer(() -> 20, () -> 6, stack);
    }

    @Override
    public int getApplyTime(ItemStack stack) {
        return 120;
    }
}
