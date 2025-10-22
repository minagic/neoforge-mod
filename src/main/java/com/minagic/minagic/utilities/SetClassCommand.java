package com.minagic.minagic.utilities;

import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.registries.ModAttachments;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class SetClassCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setclass")
                .requires(source -> source.hasPermission(2)) // permission level 2 = operators
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("class", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (PlayerClassEnum cls : PlayerClassEnum.values()) {
                                        if (cls != PlayerClassEnum.UNDECLARED) {
                                            builder.suggest(cls.name().toLowerCase(Locale.ROOT));
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                                    String classInput = StringArgumentType.getString(ctx, "class").toUpperCase(Locale.ROOT);

                                    PlayerClassEnum chosenClass;
                                    try {
                                        chosenClass = PlayerClassEnum.valueOf(classInput);
                                    } catch (IllegalArgumentException e) {
                                        ctx.getSource().sendFailure(Component.literal("Invalid class: " + classInput));
                                        return 0;
                                    }

                                    if (chosenClass == PlayerClassEnum.UNDECLARED) {
                                        ctx.getSource().sendFailure(Component.literal("You cannot set class to UNDECLARED."));
                                        return 0;
                                    }

                                    var data = player.getData(ModAttachments.PLAYER_CLASS);
                                    data.setPlayerClass(chosenClass);
                                    player.setData(ModAttachments.PLAYER_CLASS, data);

                                    var manaData = player.getData(ModAttachments.MANA);
                                    manaData.changeClass(chosenClass);
                                    player.setData(ModAttachments.MANA, manaData);
                                    ctx.getSource().sendSuccess(() ->
                                            Component.literal("Set class of " + player.getName().getString() + " to " + chosenClass.name()), true);
                                    return 1;
                                }))));
    }
}