package com.minagic.minagic.common.network.packets;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.SpellcastingItem;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.client.input.ClientShipInputHandler;
import com.minagic.minagic.spellCasting.SpellRegistry;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

public record ClientShipInputPacket(float vertical, float forward, float strafe, float pitch, float yaw, float roll, boolean reset) implements CustomPacketPayload {
    public static final Type<ClientShipInputPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "client_ship_input_packet"));
    public static final Codec<ClientShipInputPacket> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("vertical").forGetter(ClientShipInputPacket::vertical),
                    Codec.FLOAT.fieldOf("forward").forGetter(ClientShipInputPacket::forward),
                    Codec.FLOAT.fieldOf("strafe").forGetter(ClientShipInputPacket::strafe),
                    Codec.FLOAT.fieldOf("pitch").forGetter(ClientShipInputPacket::pitch),
                    Codec.FLOAT.fieldOf("yaw").forGetter(ClientShipInputPacket::yaw),
                    Codec.FLOAT.fieldOf("roll").forGetter(ClientShipInputPacket::roll),
                    Codec.BOOL.fieldOf("reset").forGetter(ClientShipInputPacket::reset)
            ).apply(instance, ClientShipInputPacket::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientShipInputPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT,
                    ClientShipInputPacket::vertical,
                    ByteBufCodecs.FLOAT,
                    ClientShipInputPacket::forward,
                    ByteBufCodecs.FLOAT,
                    ClientShipInputPacket::strafe,
                    ByteBufCodecs.FLOAT,
                    ClientShipInputPacket::pitch,
                    ByteBufCodecs.FLOAT,
                    ClientShipInputPacket::yaw,
                    ByteBufCodecs.FLOAT,
                    ClientShipInputPacket::roll,
                    ByteBufCodecs.BOOL,
                    ClientShipInputPacket::reset,
                    ClientShipInputPacket::new
            );

    public static void handle(ClientShipInputPacket pkt, IPayloadContext ctx) {
        if (!(ctx.player() instanceof ServerPlayer serverPlayer)) return;
        if (!(ctx.player().getVehicle() instanceof ArcaneShipEntity ship)) return;
        //if (ship.getControllingPassenger() != serverPlayer) return;
        ship.overrideControls();
        ship.acceptInputs(ClientShipInputHandler.ShipInput.fromPacket(pkt));



    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


}
