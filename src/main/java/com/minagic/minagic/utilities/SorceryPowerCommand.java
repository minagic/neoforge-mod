package com.minagic.minagic.utilities;

import com.minagic.minagic.capabilities.powersource.ActivePowerSourceAttachment;
import com.minagic.minagic.capabilities.powersource.SorceryPowerSourceAttachment;
import com.minagic.minagic.registries.ModAttachments;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class SorceryPowerCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("testsorcery")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("entity", EntityArgument.entity())
                        .executes(SorceryPowerCommand::activate)
                )
        );
    }

    private static int activate(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(ctx, "entity");

        ActivePowerSourceAttachment.activate(entity, new SorceryPowerSourceAttachment().getId());
        SorceryPowerSourceAttachment.setRank(entity, SorceryPowerSourceAttachment.BLOODLINE_CELESTIAL, 20);

        ctx.getSource().sendSuccess(() -> Component.literal(
                "Activated sorcery powers for " + entity.getName().getString() + " with Celestial affinity 20."
        ), true);
        return 1;
    }
}
