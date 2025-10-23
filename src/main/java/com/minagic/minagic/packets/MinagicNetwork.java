package com.minagic.minagic.packets;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.spellCasting.ISpellcastingItem;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.bus.api.IEventBus;

import java.util.Optional;

public class MinagicNetwork {
    public void register(IEventBus modBus) {
        PayloadRegistrar registrar = new PayloadRegistrar(Minagic.MODID);

        registrar.playToServer(
                SpellSlotCyclePacket.TYPE,
                SpellSlotCyclePacket.CODEC,
                MinagicNetwork::handleCycleSpell
        );

        registrar.playToServer(
                SpellSlotCycleDownPacket.TYPE,
                SpellSlotCycleDownPacket.CODEC,
                ServerPayloadHandler::handleCycleSpellDown
        );
    }

    private static void handleCycleSpell(SpellSlotCyclePacket packet, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        if (player == null) {
            return;
        }

        // get the item in the player's main hand
        var stack = player.getMainHandItem();
        if (stack.getItem() instanceof ISpellcastingItem spellcastingItem) {
            spellcastingItem.cycleActiveSpellSlot(Optional.of(player), stack);
        }
    }
}