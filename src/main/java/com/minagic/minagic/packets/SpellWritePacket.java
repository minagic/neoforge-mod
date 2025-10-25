package com.minagic.minagic.packets;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.abstractionLayer.Spell;
import com.minagic.minagic.abstractionLayer.SpellcastingItem;
import com.minagic.minagic.registries.ModSpells;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.minagic.minagic.spellCasting.SpellRegistry;

public record SpellWritePacket(int slotIndex, ResourceLocation spellId) implements CustomPacketPayload {
    public static final Type<SpellWritePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "spell_write_packet"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SpellWritePacket pkt, IPayloadContext ctx) {
        if (!(ctx.player() instanceof ServerPlayer serverPlayer)) return;
        if (serverPlayer == null) return;

        ItemStack stack = serverPlayer.getMainHandItem();
        if (!(stack.getItem() instanceof SpellcastingItem<?> item)) return;

        Spell spell = ModSpells.get(pkt.spellId());
        if (spell == null) return;

        item.writeSpell(stack, serverPlayer.level(), serverPlayer, pkt.slotIndex(), spell); // <- this now runs on the server

        serverPlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
        serverPlayer.containerMenu.broadcastChanges();
    }
    public static final Codec<SpellWritePacket> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("slotIndex").forGetter(SpellWritePacket::slotIndex),
                    ResourceLocation.CODEC.fieldOf("spellId").forGetter(SpellWritePacket::spellId)
            ).apply(instance, SpellWritePacket::new)
    );


    public static final StreamCodec<RegistryFriendlyByteBuf, SpellWritePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    SpellWritePacket::slotIndex,
                    ResourceLocation.STREAM_CODEC,
                    SpellWritePacket::spellId,
                    SpellWritePacket::new
            );





}
