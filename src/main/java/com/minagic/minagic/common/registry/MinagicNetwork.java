package com.minagic.minagic.common.registry;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.common.network.packets.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class MinagicNetwork {
    public static void register(IEventBus modBus) {
        PayloadRegistrar registrar = new PayloadRegistrar(Minagic.MODID);

        registrar.playToServer(
                SpellSlotCyclePacket.TYPE,
                SpellSlotCyclePacket.CODEC,
                SpellSlotCyclePacket::handle
        );

        registrar.playToServer(
                SpellSlotCycleDownPacket.TYPE,
                SpellSlotCycleDownPacket.CODEC,
                SpellSlotCycleDownPacket::handle
        );

        registrar.playToServer(
                SpellWritePacket.TYPE,
                SpellWritePacket.STREAM_CODEC,
                SpellWritePacket::handle
        );

        registrar.playToServer(
                ClientShipInputPacket.TYPE,
                ClientShipInputPacket.STREAM_CODEC,
                ClientShipInputPacket::handle
        );

        registrar.playToClient(
                SyncSpellcastingDataPacket.TYPE,
                SyncSpellcastingDataPacket.STREAM_CODEC,
                SyncSpellcastingDataPacket::handle
        );
    }


}