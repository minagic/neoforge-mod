package com.minagic.minagic.wizard.starships.utilities;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertAttachment;
import com.minagic.minagic.capabilities.powersource.AbstractPowerSource;
import com.minagic.minagic.capabilities.powersource.ActivePowerSourceAttachment;
import com.minagic.minagic.capabilities.powersource.ShipPowerSourceAttachment;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipProjectile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.UUID;

public record OrdnanceState (
        ResourceLocation ordnanceID,
        int available,
        float buildProgress
) {
    public static Codec<OrdnanceState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("ordnance_id").forGetter(OrdnanceState::ordnanceID),
            Codec.INT.fieldOf("available").forGetter(OrdnanceState::available),
            Codec.FLOAT.fieldOf("build_progress").forGetter(OrdnanceState::buildProgress)
    ).apply(instance, OrdnanceState::new));

    public static StreamCodec<ByteBuf, OrdnanceState> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static EntityDataSerializer<OrdnanceState> ENTITY_DATA_SERIALIZER= new EntityDataSerializer<OrdnanceState>(){

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, OrdnanceState> codec() {
            return OrdnanceState.STREAM_CODEC;
        }

        @Override

        public @NotNull OrdnanceState copy(OrdnanceState value) {

            return value.copy();

        }
    };

    public static OrdnanceState DEFAULT() {
        return new OrdnanceState(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "ordnance_no_rack"), 3, 0f);
    }

    public OrdnanceState copy(){
        return new OrdnanceState(ordnanceID, available, buildProgress);
    }

    public OrdnanceState tickProgress(ArcaneShipEntity ship){
        Ordnance ordnance = OrdnanceRegistry.get(ordnanceID);
        if (available() >= ordnance.maxStock()){
            return new OrdnanceState(ordnanceID, ordnance.maxStock(), 0.0f);
        }
        AbstractPowerSource abstractPowerSource = ActivePowerSourceAttachment.getActivePowerSource(ship);
        if (!(abstractPowerSource instanceof ShipPowerSourceAttachment shipPower)) {
            Minagic.LOGGER.error("Invalid power source for Arcane Ship: {}", ship.debugIdentity());
            return this.copy();
        }
        if (!shipPower.canConsume(new SpellCastContext(ship),null, ordnance.recreationPerTick()))
        {
            return this.copy();
        }
        int available = this.available();
        float buildProgress = buildProgress()+ (float) ordnance.recreationPerTick() /ordnance.manaToRecreate();
        if (buildProgress >= 1f){
            buildProgress = 0f;
            available+=1;
        }
        shipPower.consume(new SpellCastContext(ship),null, ordnance.recreationPerTick());
        return new OrdnanceState(ordnanceID, available, buildProgress);

    }

    public OrdnanceState fire(
            ArcaneShipEntity ship,
            UUID pilotUUID
    ) {
        if (ship.getPilot() == null){
            Minagic.LOGGER.error("Ship {} has received a firing command but had no pilot", ship.debugIdentity());
            return this.copy();
        }
        if (available() == 0){
            HudAlertAttachment.addToEntity(ship.getPilot(), "No ordnance available", 0xFFFF000, 2, 60);
            return this.copy();
        }
        if (OrdnanceRegistry.get(ordnanceID) == null){
            Minagic.LOGGER.error("Could not find ordnance with id: {}", ordnanceID);
        }
        Ordnance ordnance = OrdnanceRegistry.get(ordnanceID);
        int mountIndex = ordnance.maxStock() - available();

        Quaternionf orientation =
                new Quaternionf(ship.getState().orientation());

        Vec3 pos = ordnance.localPosition().get(mountIndex);
        Vec3 dir = ordnance.localDirection().get(mountIndex);
        Vector3f positionOffsetF =
                orientation.transform(
                        pos.toVector3f()
                );

        Vector3f directionF =
                orientation.transform(
                        dir.toVector3f()
                );

        Vec3 worldPosition = ship.position()
                .add(new Vec3(positionOffsetF));

        Vec3 worldDirection =
                new Vec3(directionF).normalize();

        ordnance.projectileFactory().create(
                ship.level(),
                worldPosition,
                worldDirection,
                pilotUUID,
                ship.getUUID()
        );
        return new OrdnanceState(ordnanceID, available -1, buildProgress);
    }

}
