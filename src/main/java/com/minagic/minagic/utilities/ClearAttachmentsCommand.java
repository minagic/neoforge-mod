package com.minagic.minagic.utilities;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.SpellMetadata;
import com.minagic.minagic.registries.ModAttachments;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class ClearAttachmentsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("clearallattachments")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("entity", EntityArgument.entity())
                        .executes(ClearAttachmentsCommand::clear)
                )
        );
    }

    @SuppressWarnings("SameReturnValue")
    private static int clear(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(ctx, "entity");
        ModAttachments.resetAllAttachments(entity);

        ctx.getSource().sendSuccess(() -> Component.literal(
                "Cleared attachments for " + entity.getName().getString()), true);
        Minagic.LOGGER.debug("Resulting metadata attachment: {}", SpellMetadata.getAllBlockPos(entity));
        return 1;
    }
}
