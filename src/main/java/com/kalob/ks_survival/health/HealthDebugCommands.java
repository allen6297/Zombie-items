package com.kalob.ks_survival.health;

import com.kalob.ks_survival.init.ModAttachments;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class HealthDebugCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ks")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("injury")
                        .then(Commands.literal("heal")
                                .executes(context -> heal(context.getSource().getPlayerOrException())))
                        .then(Commands.literal("set")
                                .then(Commands.argument("part", StringArgumentType.word())
                                        .then(Commands.argument("hp", IntegerArgumentType.integer(0))
                                                .executes(context -> setHp(
                                                        context.getSource().getPlayerOrException(),
                                                        StringArgumentType.getString(context, "part"),
                                                        IntegerArgumentType.getInteger(context, "hp"))))))
                        .then(Commands.literal("wound")
                                .then(Commands.argument("part", StringArgumentType.word())
                                        .then(Commands.argument("wound", StringArgumentType.word())
                                                .executes(context -> wound(
                                                        context.getSource().getPlayerOrException(),
                                                        StringArgumentType.getString(context, "part"),
                                                        StringArgumentType.getString(context, "wound"))))))
                        .then(Commands.literal("fallstun")
                                .then(Commands.argument("seconds", IntegerArgumentType.integer(0, 120))
                                        .executes(context -> fallStun(
                                                context.getSource().getPlayerOrException(),
                                                IntegerArgumentType.getInteger(context, "seconds")))))));
    }

    private static int heal(ServerPlayer player) {
        BodyPartData data = new BodyPartData();
        player.setData(ModAttachments.BODY_PARTS.get(), data);
        sync(player, data);
        player.sendSystemMessage(Component.literal("Reset body-part health."));
        return 1;
    }

    private static int setHp(ServerPlayer player, String partName, int hp) {
        BodyPart part = parsePart(partName);
        if (part == null) {
            player.sendSystemMessage(Component.literal("Unknown body part: " + partName));
            return 0;
        }

        BodyPartData data = player.getData(ModAttachments.BODY_PARTS.get());
        data.setHp(part, hp);
        player.setData(ModAttachments.BODY_PARTS.get(), data);
        sync(player, data);
        player.sendSystemMessage(Component.literal("Set " + part.getSerializedName() + " to " + data.getHp(part) + " HP."));
        return 1;
    }

    private static int wound(ServerPlayer player, String partName, String woundName) {
        BodyPart part = parsePart(partName);
        Wound wound = parseWound(woundName);
        if (part == null) {
            player.sendSystemMessage(Component.literal("Unknown body part: " + partName));
            return 0;
        }
        if (wound == null) {
            player.sendSystemMessage(Component.literal("Unknown wound: " + woundName));
            return 0;
        }

        BodyPartData data = player.getData(ModAttachments.BODY_PARTS.get());
        data.addWound(part, wound);
        player.setData(ModAttachments.BODY_PARTS.get(), data);
        sync(player, data);
        player.sendSystemMessage(Component.literal("Added " + wound.getSerializedName() + " to " + part.getSerializedName() + "."));
        return 1;
    }

    private static int fallStun(ServerPlayer player, int seconds) {
        BodyPartData data = player.getData(ModAttachments.BODY_PARTS.get());
        data.lockMovementFor(seconds * 20);
        player.setData(ModAttachments.BODY_PARTS.get(), data);
        sync(player, data);
        player.sendSystemMessage(Component.literal("Movement locked for " + seconds + " seconds."));
        return 1;
    }

    private static BodyPart parsePart(String name) {
        for (BodyPart part : BodyPart.values()) {
            if (part.getSerializedName().equalsIgnoreCase(name) || part.name().equalsIgnoreCase(name)) {
                return part;
            }
        }
        return null;
    }

    private static Wound parseWound(String name) {
        for (Wound wound : Wound.values()) {
            if (wound.getSerializedName().equalsIgnoreCase(name) || wound.name().equalsIgnoreCase(name)) {
                return wound;
            }
        }
        return null;
    }

    private static void sync(ServerPlayer player, BodyPartData data) {
        PacketDistributor.sendToPlayer(player, new BodyPartSyncPacket(player.getId(), data));
    }
}
