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
        ShipState state = ship.getState();
        PhysicsData phys = ship.getPhysicsData();

        AbstractPowerSource attachment = ActivePowerSourceAttachment.getActivePowerSource(ship);
        if (!(attachment instanceof ShipPowerSourceAttachment shipPower) ){
            Minagic.LOGGER.error("Ship {} does not have the correct power source! Aborting!", ship.debugIdentity());
            return;
        }

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

        ship.setData(ModAttachments.SHIP_POWER_SOURCE, shipPower);

        Quaternionf normalizedOrientation =
                new Quaternionf(ship.getState().orientation()).normalize();

        Vector3f worldThrust = thrust.toVector3f();
        normalizedOrientation.transform(worldThrust);

        Vec3 worldForce = new Vec3(worldThrust);
        Vec3 drag = computeDragVector(ship);
        Vec3 gravity = new Vec3(0, -0.1, 0).scale(ship.getPhysicsData().mass());

        Vec3 net = worldForce.add(drag).add(gravity);


        if (ship.getPilot() != null){
            FlightControls.setForceData(ship.getPilot(), net, worldForce, drag, gravity);
        }

        Vec3 oldVelocity = ship.getDeltaMovement();
        Vec3 acceleration = net.scale(1.0 / ship.getPhysicsData().mass());
        Vec3 nextVelocity = oldVelocity.add(acceleration);

        ship.setDeltaMovement(nextVelocity);

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
        ShipState state = ship.getState();
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
        return newState;
    }
}
