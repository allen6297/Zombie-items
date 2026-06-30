package com.kalob.ks_survival.health;

import com.kalob.ks_survival.init.ModAttachments;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class HealthDebugCommands {

    private static final List<String> PART_NAMES = List.of(
            "head", "torso", "left_arm", "right_arm", "left_leg", "right_leg");
    private static final List<String> WOUND_NAMES = List.of(
            "bleeding", "severe_bleeding", "fracture", "infection");

    private static final SuggestionProvider<net.minecraft.commands.CommandSourceStack> SUGGEST_PARTS =
            (ctx, builder) -> SharedSuggestionProvider.suggest(PART_NAMES, builder);
    private static final SuggestionProvider<net.minecraft.commands.CommandSourceStack> SUGGEST_WOUNDS =
            (ctx, builder) -> SharedSuggestionProvider.suggest(WOUND_NAMES, builder);

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ks")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("injury")
                        .then(Commands.literal("heal")
                                .executes(context -> heal(context.getSource().getPlayerOrException())))
                        .then(Commands.literal("set")
                                .then(Commands.argument("part", StringArgumentType.word())
                                        .suggests(SUGGEST_PARTS)
                                        .then(Commands.argument("hp", IntegerArgumentType.integer(0))
                                                .executes(context -> setHp(
                                                        context.getSource().getPlayerOrException(),
                                                        StringArgumentType.getString(context, "part"),
                                                        IntegerArgumentType.getInteger(context, "hp"))))))
                        .then(Commands.literal("wound")
                                .then(Commands.argument("part", StringArgumentType.word())
                                        .suggests(SUGGEST_PARTS)
                                        .then(Commands.argument("wound", StringArgumentType.word())
                                                .suggests(SUGGEST_WOUNDS)
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
        String n = name.toLowerCase().replace(".", "_").replace(" ", "_");
        for (BodyPart part : BodyPart.values()) {
            if (part.getSerializedName().equalsIgnoreCase(n) || part.name().equalsIgnoreCase(n)) {
                return part;
            }
        }
        // Common shorthand aliases
        return switch (n) {
            case "l_arm", "larm", "lf_arm"  -> BodyPart.LEFT_ARM;
            case "r_arm", "rarm", "rt_arm"  -> BodyPart.RIGHT_ARM;
            case "l_leg", "lleg"            -> BodyPart.LEFT_LEG;
            case "r_leg", "rleg"            -> BodyPart.RIGHT_LEG;
            case "arm"                       -> BodyPart.LEFT_ARM;
            case "leg"                       -> BodyPart.LEFT_LEG;
            default -> null;
        };
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
