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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.Locale;

public class SorceryPowerCommand {

    public enum BloodlineOption {
        CELESTIAL,
        VOIDBOURNE,
        SPIRITUAL,
        INFERNAL,
        DRACONIC;

        public static BloodlineOption fromString(String value) {
            return BloodlineOption.valueOf(value.toUpperCase(Locale.ROOT));
        }
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("testsorcery")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("entity", EntityArgument.entity())
                        .then(Commands.argument("bloodline", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    builder.suggest("celestial");
                                    builder.suggest("voidbourne");
                                    builder.suggest("spiritual");
                                    builder.suggest("infernal");
                                    builder.suggest("draconic");
                                    return builder.buildFuture();
                                })
                                .executes(SorceryPowerCommand::activate)
                        )
                )
        );
    }

    private static int activate(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(ctx, "entity");
        String bloodlineRaw = StringArgumentType.getString(ctx, "bloodline");

        BloodlineOption bloodline;
        try {
            bloodline = BloodlineOption.fromString(bloodlineRaw);
        } catch (IllegalArgumentException ex) {
            ctx.getSource().sendFailure(Component.literal(
                    "Invalid bloodline: " + bloodlineRaw + ". Valid options: celestial, voidbourne, spiritual, infernal, draconic."
            ));
            return 0;
        }

        ActivePowerSourceAttachment.activate(entity, new SorceryPowerSourceAttachment().getId());
        SorceryPowerSourceAttachment.setRank(entity, getBloodlineConstant(bloodline), 20);

        ctx.getSource().sendSuccess(() -> Component.literal(
                "Activated sorcery powers for " + entity.getName().getString()
                        + " with " + formatName(bloodline) + " affinity 20."
        ), true);

        return 1;
    }

    private static String getBloodlineConstant(BloodlineOption bloodline) {
        return switch (bloodline) {
            case CELESTIAL -> SorceryPowerSourceAttachment.BLOODLINE_CELESTIAL;
            case VOIDBOURNE -> SorceryPowerSourceAttachment.BLOODLINE_VOIDBOURNE;
            case SPIRITUAL -> SorceryPowerSourceAttachment.BLOODLINE_SPIRITUAL;
            case INFERNAL -> SorceryPowerSourceAttachment.BLOODLINE_INFERNAL;
            case DRACONIC -> SorceryPowerSourceAttachment.BLOODLINE_DRACONIC;
        };
    }

    private static String formatName(BloodlineOption bloodline) {
        String lower = bloodline.name().toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}