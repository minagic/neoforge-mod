package com.minagic.minagic.utilities;

import com.minagic.minagic.capabilities.Deity;
import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.registries.ModAttachments;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.server.command.EnumArgument;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class SetClassCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setclass")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.entities())
                        .then(Commands.argument("main", EnumArgument.enumArgument(PlayerClassEnum.class))
                                .executes(ctx -> apply(ctx, false, false))
                                .then(Commands.argument("deity", EnumArgument.enumArgument(Deity.class))
                                        .executes(ctx -> apply(ctx, true, false))
                                        .then(Commands.argument("subclasses", StringArgumentType.greedyString())
                                                .executes(ctx -> apply(ctx, true, true))
                                        )
                                )
                        )
                )
        );
    }

    private static int apply(CommandContext<CommandSourceStack> ctx, boolean withDeity, boolean withSubclasses) throws CommandSyntaxException {
        PlayerClassEnum main = ctx.getArgument("main", PlayerClassEnum.class);

        PlayerClass pc = new PlayerClass();
        pc.setMainClass(main);

        if (withDeity) {
            Deity deity = ctx.getArgument("deity", Deity.class);
            if (!pc.setDeity(deity)) {
                ctx.getSource().sendFailure(Component.literal("Invalid deity " + deity.name() + " for " + main.name()));
                return 0;
            }
        }

        if (withSubclasses) {
            String subclassesRaw = StringArgumentType.getString(ctx, "subclasses");

            for (String entry : subclassesRaw.split(",")) {
                String[] pair = entry.trim().split(":");
                if (pair.length != 2) {
                    ctx.getSource().sendFailure(Component.literal("Invalid subclass format: " + entry));
                    return 0;
                }

                try {
                    PlayerSubClassEnum subclass = PlayerSubClassEnum.valueOf(pair[0].toUpperCase(Locale.ROOT));
                    int level = Integer.parseInt(pair[1]);

                    if (!pc.setSubclassLevel(subclass, level)) {
                        ctx.getSource().sendFailure(Component.literal("Subclass " + subclass.name() + " is not valid for " + main.name()));
                        return 0;
                    }

                } catch (Exception e) {
                    ctx.getSource().sendFailure(Component.literal("Error parsing subclass: " + entry));
                    return 0;
                }
            }
        }

        for(Entity entity : EntityArgument.getEntities(ctx, "player")) {
            entity.setData(ModAttachments.PLAYER_CLASS, pc);
            ctx.getSource().sendSuccess(() ->
                    Component.literal("Updated class of " + entity.getName().getString() + " to " + main.name()), true);
        }



        return 1;
    }
}