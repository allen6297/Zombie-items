package com.kalob.ks_survival.init;

import com.kalob.ks_survival.KsSurvival;
import com.kalob.ks_survival.farming.FarmAnimalData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, KsSurvival.MODID);

    public static final Supplier<AttachmentType<FarmAnimalData>> FARM_ANIMAL =
        ATTACHMENT_TYPES.register("farm_animal",
            () -> AttachmentType.builder(FarmAnimalData::new)
                .serialize(FarmAnimalData.CODEC)
                .build());
}
