package com.minagic.minagic.common.commands.handlers;

import com.minagic.minagic.api.spells.GatedSpell;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.spellCasting.SpellRegistry;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CheckGateChain {
    public static void register(com.mojang.brigadier.CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("checkgatechain")
                        .then(
                                Commands.argument("spell", StringArgumentType.greedyString())
                                        .executes(CheckGateChain::execute)

                        )

        );

    }

    private static int execute(CommandContext<CommandSourceStack> context) {

        String rawSpellId = StringArgumentType.getString(context, "spell");

        ResourceLocation spellId;

        try {

            spellId = ResourceLocation.parse(rawSpellId);

        } catch (Exception e) {

            context.getSource().sendFailure(

                    Component.literal(

                            "Invalid spell id: " + rawSpellId

                    )

            );

            return 0;

        }

        Spell spell = SpellRegistry.getSpell(spellId);
        if (spell == null) {
            context.getSource().sendFailure(

                    Component.literal(

                            "Spell not found: " + spellId

                    )

            );

            return 0;

        }

        if (!(spell instanceof GatedSpell gatedSpell)) {
            context.getSource().sendFailure(

                    Component.literal(

                            "Spell " + spell.getString() + "does not support gate chain dumping"

                    )

            );

            return 0;
        }
        else {
            context.getSource().sendSuccess(

                    () -> Component.literal(

                            gatedSpell.describeSpellChain()

                    ),

                    false

            );

            return 1;
        }



    }

}

