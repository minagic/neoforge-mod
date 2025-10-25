package com.minagic.minagic.packets;

import com.minagic.minagic.Minagic;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.bus.api.IEventBus;

public class MinagicNetwork {
    public void register(IEventBus modBus) {
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

        registrar.playToClient(
                SyncSpellcastingDataPacket.TYPE,
                SyncSpellcastingDataPacket.STREAM_CODEC,
                SyncSpellcastingDataPacket::handle
        );
    }


}