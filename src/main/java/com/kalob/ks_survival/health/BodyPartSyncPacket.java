package com.kalob.ks_survival.health;

import com.kalob.ks_survival.init.ModAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record BodyPartSyncPacket(int entityId, BodyPartData data) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BodyPartSyncPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("ks_survival", "body_part_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BodyPartSyncPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, BodyPartSyncPacket::entityId,
                    BodyPartData.STREAM_CODEC, BodyPartSyncPacket::data,
                    BodyPartSyncPacket::new
            );

    @Override
    public CustomPacketPayload.Type<BodyPartSyncPacket> type() { return TYPE; }

    @OnlyIn(Dist.CLIENT)
    public static void handle(BodyPartSyncPacket packet, net.neoforged.neoforge.network.handling.IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;
            var entity = mc.level.getEntity(packet.entityId());
            if (entity instanceof net.minecraft.world.entity.player.Player player) {
                player.setData(ModAttachments.BODY_PARTS.get(), packet.data());
            }
        });
    }
}
