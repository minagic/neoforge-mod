package com.minagic.minagic.utilities;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.MagicClassEnums.DeityEnum;
import com.minagic.minagic.capabilities.MagicClass;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerClassEnum;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.powersource.ActivePowerSourceAttachment;
import com.minagic.minagic.registries.ModAttachments;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.server.command.EnumArgument;

import java.util.Locale;

public class SetClassCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setclass")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.entities())
                        .then(Commands.argument("main", EnumArgument.enumArgument(PlayerClassEnum.class))
                                .executes(ctx -> apply(ctx, false, false))
                                .then(Commands.argument("deity", EnumArgument.enumArgument(DeityEnum.class))
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
        ActivePowerSourceAttachment.activate(ctx.getSource().getEntity(), ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "power_source_wizardry"));
        return 0;
    }
}