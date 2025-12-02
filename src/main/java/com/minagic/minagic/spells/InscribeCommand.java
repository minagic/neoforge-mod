package com.minagic.minagic.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.SpellcastingItem;
import com.minagic.minagic.registries.ModSpells;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
        sci.writeSpell(stack, player.level(), player, 0, ModSpells.get(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "fireball")));
        return 0;
    }
}
