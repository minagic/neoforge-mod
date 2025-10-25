package com.minagic.minagic.packets;

import com.minagic.minagic.abstractionLayer.SpellcastingItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record SpellSlotCycleDownPacket() implements CustomPacketPayload {
    // Unique packet ID
    public static final Type<SpellSlotCycleDownPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("minagic", "cycle_spell_down"));

    // Encoder/decoder (no data in this example)
    public static final StreamCodec<FriendlyByteBuf, SpellSlotCycleDownPacket> CODEC =
            StreamCodec.ofMember(SpellSlotCycleDownPacket::write, SpellSlotCycleDownPacket::new);

    public SpellSlotCycleDownPacket(FriendlyByteBuf buf) {
        this(); // nothing to read
    }

    public void write(FriendlyByteBuf buf) {
        // nothing to write
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SpellSlotCycleDownPacket payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        // get the item in the player's main hand
        ItemStack stack = serverPlayer.getMainHandItem();
        if (stack.getItem() instanceof SpellcastingItem spellcastingItem) {
            spellcastingItem.cycleSlotDown(Optional.of(serverPlayer), stack);
        }

    }
}