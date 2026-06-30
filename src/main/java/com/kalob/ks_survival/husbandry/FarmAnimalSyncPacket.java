package com.kalob.ks_survival.husbandry;

import com.kalob.ks_survival.init.ModAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record FarmAnimalSyncPacket(int entityId, FarmAnimalData data) implements CustomPacketPayload {

    public static final Type<FarmAnimalSyncPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("ks_survival", "farm_animal_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FarmAnimalSyncPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, FarmAnimalSyncPacket::entityId,
                    FarmAnimalData.STREAM_CODEC, FarmAnimalSyncPacket::data,
                    FarmAnimalSyncPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(FarmAnimalSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(packet.entityId());
            if (entity instanceof Animal animal) {
                animal.setData(ModAttachments.FARM_ANIMAL.get(), packet.data());
            }
        });
    }
}
