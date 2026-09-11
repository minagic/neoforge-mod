package com.minagic.minagic.wizard.starships.utilities;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.hudAlerts.FlightControls;
import com.minagic.minagic.capabilities.powersource.AbstractPowerSource;
import com.minagic.minagic.capabilities.powersource.ActivePowerSourceAttachment;
import com.minagic.minagic.capabilities.powersource.ShipPowerSourceAttachment;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.MathUtils;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ShipPhysics {


    public static void tickTranslational(ArcaneShipEntity ship){
        long t0 = System.nanoTime();
        ShipState state = ship.state;
        PhysicsData phys = ship.getPhysicsData();
        long t1 = System.nanoTime();
        AbstractPowerSource attachment = ActivePowerSourceAttachment.getActivePowerSource(ship);
        if (!(attachment instanceof ShipPowerSourceAttachment shipPower) ){
            Minagic.LOGGER.error("Ship {} does not have the correct power source! Aborting!", ship.debugIdentity());
            return;
        }
        long t2 = System.nanoTime();
        Vector3f thrustControl = new Vector3f(state.throttleControl());
        SpellCastContext ctx = new SpellCastContext(ship);
        int fuel = (int) phys.getRequestedFuel(thrustControl);
        Vec3 thrust;
        if (shipPower.canConsume(ctx, null, fuel)){
            thrust = phys.getLocalThrust(thrustControl);
            shipPower.consume(ctx, null, fuel);
        }
        else{
            shipPower.onFail(ctx.caster, fuel);
            int actual = shipPower.removeFuel(fuel);
            thrustControl = thrustControl.mul((float) actual /fuel);
            thrust = phys.getLocalThrust(thrustControl);

        }
        long t3 = System.nanoTime();
        ship.setData(ModAttachments.SHIP_POWER_SOURCE, shipPower);
        long t4 = System.nanoTime();
        Quaternionf normalizedOrientation =
                new Quaternionf(ship.state.orientation()).normalize();
        long t5 = System.nanoTime();
        Vector3f worldThrust = thrust.toVector3f();
        normalizedOrientation.transform(worldThrust);

        long t6 = System.nanoTime();
        Vec3 worldForce = new Vec3(worldThrust);
        Vec3 drag = computeDragVector(ship);
        Vec3 gravity = new Vec3(0, -0.1, 0).scale(ship.getPhysicsData().mass());

        Vec3 net = worldForce.add(drag).add(gravity);
        long t7 = System.nanoTime();

        if (ship.getPilot() != null){
            FlightControls.setForceData(ship.getPilot(), net, worldForce, drag, gravity);
        }
        else{
            ship.telemetry.net = net;
            ship.telemetry.gravity = gravity;
            ship.telemetry.drag = drag;
        }
        long t8 = System.nanoTime();


        Vec3 oldVelocity = ship.getDeltaMovement();
        Vec3 acceleration = net.scale(1.0 / ship.getPhysicsData().mass());
        Vec3 nextVelocity = oldVelocity.add(acceleration);
        long t9 = System.nanoTime();
        ship.setDeltaMovement(nextVelocity);
        long t10 = System.nanoTime();
        if (t10-t0 > 10000000){
            Minagic.LOGGER.info("Translational ticking of ship {} took {}", ship.debugIdentity(), t10-t0);
            Minagic.LOGGER.info("Breakdown: state extraction: {}, attachment validity: {}, fuel computation/thrust {}, write fuel data {}, quaternion normalize {}, quaternion transform {}, net force compute {}, pilot / telemetry {}, velocity compute {}. velocity write {}",
                    t1-t0, t2-t1, t3-t2, t4-t3, t5-t4, t6-t5, t7-t6, t8-t7, t9-t8, t10-t9);
        }

    }

    public static Vec3 computeDragVector(ArcaneShipEntity ship) {
        Vec3 velocity = ship.getDeltaMovement();
        double speed = velocity.length();

        if (speed < 1.0E-8) {
            return Vec3.ZERO;
        }

        double requestedDrag = 0.0;

        for (DragSurface surface : ship.getPhysicsData().dragProfile()) {
            requestedDrag += surface.getDragContribution(
                    velocity,
                    ship
            );
        }
        return velocity.scale(
                -requestedDrag / speed
        );
    }
    public static ShipState tickRotational(ArcaneShipEntity ship){

        ShipState state = ship.state;
        PhysicsData phys = ship.getPhysicsData();
        float pitch = (float) Mth.clamp(state.pendingPitch(), -phys.maxAngularSpeed().x, phys.maxAngularSpeed().x);

        float yaw = (float) Mth.clamp(state.pendingYaw(), -phys.maxAngularSpeed().y, phys.maxAngularSpeed().y);

        Quaternionf orientation = new Quaternionf(state.orientation());

        orientation
                .rotateY(yaw)
                .rotateX(pitch)
                .rotateZ(state.rollVelocity())
                .normalize();
        float remainingPitch = state.pendingPitch() - pitch;
        float remainingYaw = state.pendingYaw() - yaw;

        ShipState newState = new ShipState(orientation, state.throttleControl(), state.rollVelocity(), remainingPitch, remainingYaw, state.currentInput());
        Minagic.LOGGER.info(

                "rot: pendingP={} maxP={} appliedP={} remainingP={} pendingY={} maxY={} appliedY={} remainingY={}",

                state.pendingPitch(),

                phys.maxAngularSpeed().x,

                pitch,

                remainingPitch,

                state.pendingYaw(),

                phys.maxAngularSpeed().y,

                yaw,

                remainingYaw

        );
        return newState;
    }
}
