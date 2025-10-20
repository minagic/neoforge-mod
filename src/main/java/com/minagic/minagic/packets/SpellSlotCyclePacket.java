package com.minagic.minagic.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SpellSlotCyclePacket() implements CustomPacketPayload {
    // Unique packet ID
    public static final Type<SpellSlotCyclePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("minagic", "cycle_spell"));

    // Encoder/decoder (no data in this example)
    public static final StreamCodec<FriendlyByteBuf, SpellSlotCyclePacket> CODEC =
            StreamCodec.ofMember(SpellSlotCyclePacket::write, SpellSlotCyclePacket::new);

    public SpellSlotCyclePacket(FriendlyByteBuf buf) {
        this(); // nothing to read
    }

    public void write(FriendlyByteBuf buf) {
        // nothing to write
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}