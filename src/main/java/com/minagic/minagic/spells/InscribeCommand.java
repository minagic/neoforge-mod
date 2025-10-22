package com.minagic.minagic.spells;

import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellRegistry;
import com.minagic.minagic.spellCasting.SpellcastingItem;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class InscribeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("inscribe")
                .then(Commands.literal("fireball")
                        .executes(ctx -> inscribeFireball(ctx.getSource(), ctx.getSource().getPlayerOrException()))));
    }

    private static int inscribeFireball(CommandSourceStack source, ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof SpellcastingItem sci)) {
            source.sendFailure(Component.literal("Hold a spellcasting item first."));
            return 0;
        }
        sci.writeSpell(stack, player.level(), 0, ModSpells.getFromString("minagic:fireball"));
        return 0;
    }
}
