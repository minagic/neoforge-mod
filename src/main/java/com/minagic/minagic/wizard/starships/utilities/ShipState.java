package com.minagic.minagic.wizard.starships.utilities;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.client.input.ClientShipInputHandler;
import com.minagic.minagic.utilities.CUSTOM_CODEC;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;


public record ShipState(
        Quaternionf orientation,
        Vector3f throttleControl,
        float rollVelocity,
        float pendingPitch,
        float pendingYaw,
        ClientShipInputHandler.ShipInput currentInput
) {


    @Override

    public String toString() {

        return String.format(

                "O: %s, TC: %s, RV: %s, P: %s, Y: %s, CI: %s",

                orientation,

                throttleControl,

                rollVelocity,

                pendingPitch,

                pendingYaw,

                currentInput

        );

    }
    private static final float ROLL_STOP_EPSILON = (float) Math.pow(10, -5);
    public static ShipState DEFAULT(){
        return new ShipState(new Quaternionf(), new Vector3f(0, 0, 0), 0f, 0f, 0f, ClientShipInputHandler.ShipInput.NONE());

    }
    public ShipState acceptShipInput(ClientShipInputHandler.ShipInput input){
        input = input.uninvertYawPitch();
        ShipState newState = new ShipState(orientation, throttleControl, rollVelocity, pendingPitch, pendingYaw, input);

        return newState;
    }

    public ShipState parseShipInput(ArcaneShipEntity ship) {
        Vector3f newThrottleControl = new Vector3f(throttleControl())
                .add(
                        currentInput.strafe() * 0.01F,
                        currentInput.vertical() * 0.01F,
                        currentInput.forward() * 0.01F
                );

        newThrottleControl.set(
                Mth.clamp(newThrottleControl.x, -1.0F, 1.0F),
                Mth.clamp(newThrottleControl.y, -1.0F, 1.0F),
                Mth.clamp(newThrottleControl.z, -1.0F, 1.0F)
        );

        float newPendingPitch =
                pendingPitch()
                        + currentInput.pitch()
                        * ship.getPhysicsData().aimSpeed();

        float newPendingYaw =
                pendingYaw()
                        + currentInput.yaw()
                        * ship.getPhysicsData().aimSpeed();

        float inputRoll = Mth.clamp(
                currentInput.roll(),
                -1.0F,
                1.0F
        );

        float newRollVelocity;

        if (Math.abs(inputRoll) > 0.01F) {
            newRollVelocity =
                    (float) ((float) inputRoll
                                                * ship.getPhysicsData()
                                                .maxAngularSpeed()
                                                .z);
        } else {
            newRollVelocity =
                    rollVelocity()
                            * ship.getPhysicsData().rollDecay();

            if (Math.abs(newRollVelocity) < ROLL_STOP_EPSILON) {
                newRollVelocity = 0.0F;
            }
        }

        return new ShipState(
                new Quaternionf(orientation()),
                newThrottleControl,
                newRollVelocity,
                newPendingPitch,
                newPendingYaw,
                currentInput
        );
    }

    public static Codec<ShipState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CUSTOM_CODEC.QUATERNION.fieldOf("orientation").forGetter(s -> s.orientation),
            CUSTOM_CODEC.VECTOR3.fieldOf("throttle_control").forGetter(s -> s.throttleControl),
            Codec.FLOAT.fieldOf("roll_velocity").forGetter(s -> s.rollVelocity),
            Codec.FLOAT.fieldOf("pending_pitch").forGetter(s -> s.pendingPitch),
            Codec.FLOAT.fieldOf("pending_yaw").forGetter(s -> s.pendingYaw),
            ClientShipInputHandler.ShipInput.CODEC.fieldOf("current_input").forGetter(s->s.currentInput)
    ).apply(instance, ShipState::new));

    public static StreamCodec<FriendlyByteBuf, ShipState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.QUATERNIONF,
            ShipState::orientation,
            ByteBufCodecs.VECTOR3F,
            ShipState::throttleControl,
            ByteBufCodecs.FLOAT,
            ShipState::rollVelocity,
            ByteBufCodecs.FLOAT,
            ShipState::pendingPitch,
            ByteBufCodecs.FLOAT,
            ShipState::pendingYaw,
            ClientShipInputHandler.ShipInput.STREAM_CODEC,
            ShipState::currentInput,
            ShipState::new
            
    );

    
    public ShipState copy(){
        return new ShipState(orientation, throttleControl, rollVelocity, pendingPitch, pendingYaw, currentInput);
    }

    public static EntityDataSerializer<ShipState> ENTITY_DATA_SERIALIZER= new EntityDataSerializer<ShipState>(){

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ShipState> codec() {
            return ShipState.STREAM_CODEC;
        }

        @Override

        public ShipState copy(ShipState value) {

            return value.copy();

        }
    };




}
