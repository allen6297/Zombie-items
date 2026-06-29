package com.kalob.ks_survival.farming;

import com.kalob.ks_survival.init.ModAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record FarmAnimalSyncPacket(int entityId, int hunger, int thirst, int wellFedTicks, int stressTicks, int overfedTicks, int tameness, int panicTicks, int alleleA, int alleleB) implements CustomPacketPayload {

    public static final Type<FarmAnimalSyncPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("ks_survival", "farm_animal_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FarmAnimalSyncPacket> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public @NotNull FarmAnimalSyncPacket decode(RegistryFriendlyByteBuf buf) {
                    return new FarmAnimalSyncPacket(
                            buf.readInt(), buf.readInt(), buf.readInt(),
                            buf.readInt(), buf.readInt(), buf.readInt(),
                            buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
                }
                @Override
                public void encode(RegistryFriendlyByteBuf buf, FarmAnimalSyncPacket p) {
                    buf.writeInt(p.entityId()); buf.writeInt(p.hunger()); buf.writeInt(p.thirst());
                    buf.writeInt(p.wellFedTicks()); buf.writeInt(p.stressTicks());
                    buf.writeInt(p.overfedTicks()); buf.writeInt(p.tameness()); buf.writeInt(p.panicTicks());
                    buf.writeInt(p.alleleA()); buf.writeInt(p.alleleB());
                }
            };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(FarmAnimalSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(packet.entityId());
            if (entity instanceof Animal animal) {
                animal.setData(ModAttachments.FARM_ANIMAL.get(),
                        FarmAnimalData.of(packet.hunger(), packet.thirst(), packet.wellFedTicks(),
                                packet.stressTicks(), packet.overfedTicks(), packet.tameness(), packet.panicTicks(),
                                packet.alleleA(), packet.alleleB()));
            }
        });
    }
}
