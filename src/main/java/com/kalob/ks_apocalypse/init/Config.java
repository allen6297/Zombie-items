package com.kalob.ks_apocalypse.init;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Bandage
    public static final ModConfigSpec.DoubleValue BANDAGE_HEAL_AMOUNT = BUILDER
            .comment("How much health the bandage restores")
            .translation("config.ks_apocalypse.bandageHealAmount")
            .defineInRange("bandageHealAmount", 6.0, 0.5, 20.0);

    public static final ModConfigSpec.IntValue BANDAGE_USE_DURATION = BUILDER
            .comment("How long it takes to apply a bandage in ticks (20 ticks = 1 second)")
            .translation("config.ks_apocalypse.bandageUseDuration")
            .defineInRange("bandageUseDuration", 60, 20, 200);

    // Cans
    public static final ModConfigSpec.IntValue CAN1_THIRST = BUILDER
            .comment("Thirst restored by the small can")
            .translation("config.ks_apocalypse.can1Thirst")
            .defineInRange("can1Thirst", 3, 1, 10);

    public static final ModConfigSpec.IntValue CAN1_QUENCH = BUILDER
            .comment("Quench value of the small can")
            .translation("config.ks_apocalypse.can1Quench")
            .defineInRange("can1Quench", 1, 0, 5);

    public static final ModConfigSpec.IntValue CAN2_THIRST = BUILDER
            .comment("Thirst restored by the big can")
            .translation("config.ks_apocalypse.can2Thirst")
            .defineInRange("can2Thirst", 6, 1, 10);

    public static final ModConfigSpec.IntValue CAN2_QUENCH = BUILDER
            .comment("Quench value of the big can")
            .translation("config.ks_apocalypse.can2Quench")
            .defineInRange("can2Quench", 2, 0, 5);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
