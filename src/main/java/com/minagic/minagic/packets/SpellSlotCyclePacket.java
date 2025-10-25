package com.minagic.minagic.packets;

import com.minagic.minagic.abstractionLayer.SpellcastingItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

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

    public static void handle(SpellSlotCyclePacket packet, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        if (player == null) {
            return;
        }

        // get the item in the player's main hand
        var stack = player.getMainHandItem();
        if (stack.getItem() instanceof SpellcastingItem spellcastingItem) {
            spellcastingItem.cycleSlotUp(Optional.of(player), stack);
        }
    }
}