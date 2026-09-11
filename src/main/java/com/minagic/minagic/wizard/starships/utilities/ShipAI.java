package com.minagic.minagic.wizard.starships.utilities;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.client.input.ClientShipInputHandler;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipMissileComputer;
import com.minagic.minagic.wizard.starships.spells.FireOrdnance;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class ShipAI {
    private ShipAIState state = ShipAIState.PURSUIT;
    private int ticksManeuver;
    private int primaryTicks = 0;

    public ShipAIState getDesiredState(ArcaneShipEntity ship) {
        if (ship.onGround()) return ShipAIState.GROUNDED;
        if (ship.checkGPWS()) return ShipAIState.RECOVERY;
        if (ship.level().getNearestPlayer(ship, 100) != null && ship.position().subtract(ship.level().getNearestPlayer(ship, 100).position()).dot(new Vec3(ship.state.orientation().transform(new Vector3f(0, 0, 1)))) < -0.3)
            return ShipAIState.EVASION;
        if (ship.targetingComputer.target(ship.level()) instanceof Player) return ShipAIState.ATTACK;
        return ShipAIState.PURSUIT;


    }

    public void doShipState(ArcaneShipEntity ship) {
        long t0 = System.nanoTime();

        state = getDesiredState(ship);

        long t1 = System.nanoTime();

        if (ship.getPilot() != null && ship.getPilot() instanceof ServerPlayer player) {
            player.sendSystemMessage(Component.literal("AI state: " + state.name()));
        }

        long t2 = System.nanoTime();

        if (state == ShipAIState.GROUNDED) {
            ship.acceptInputs(new ClientShipInputHandler.ShipInput(
                    1, 0.3f, 0, 0, 0, 0
            ));
        }

        long t3 = System.nanoTime();

        if (state == ShipAIState.ATTACK) {
            if (ship.getOrdnance().available() == 0) {
                ShipWeapons.firePrimary(ship, primaryTicks);
                primaryTicks++;
            } else {
                ShipWeapons.fireOrdnance(ship);
            }
        } else {
            primaryTicks = 0;
        }

        long t4 = System.nanoTime();

        if (state == ShipAIState.RECOVERY) {
            float pitch = ship.telemetry.GPWS ? 1 : 0;

            float dotproduct = ship.state.orientation()
                    .transform(new Vector3f(0, 1, 0))
                    .dot(new Vector3f(0, 1, 0));

            pitch = pitch * dotproduct / Math.abs(dotproduct);

            float v = ship.state.orientation()
                    .transform(new Vector3f(0, 1, 0))
                    .dot(new Vector3f(0, 1, 0));

            float f = ship.state.orientation()
                    .transform(new Vector3f(0, 0, 1))
                    .dot(new Vector3f(0, 0, 1));

            float s = ship.state.orientation()
                    .transform(new Vector3f(1, 0, 0))
                    .dot(new Vector3f(1, 0, 0));

            ship.acceptInputs(new ClientShipInputHandler.ShipInput(
                    v, f, s, pitch, 0, 0
            ));
        } else {
            if (ship.telemetry.net.y > 0) {
                ship.acceptInputs(new ClientShipInputHandler.ShipInput(
                        -1,
                        1,
                        0,
                        -ship.state.pendingPitch(),
                        0,
                        0
                ));
            }
        }

        long t5 = System.nanoTime();

        if (state == ShipAIState.EVASION) {
            if (ticksManeuver == 0) ticksManeuver = 20;
            ticksManeuver--;

            float roll = ship.state.orientation()
                    .transform(new Vector3f(0, 1, 0))
                    .dot(new Vector3f(0, 1, 0)) > 0.5
                    ? 1
                    : 0;

            ship.acceptInputs(new ClientShipInputHandler.ShipInput(
                    -ship.state.currentInput().vertical(), 1f, 0, 0, 1, roll
            ));
        }

        long t6 = System.nanoTime();

        if (state == ShipAIState.PURSUIT) {
            if (
                    ship.level().getNearestPlayer(ship, 100) == null
                            ||
                            ship.position()
                                    .subtract(ship.level().getNearestPlayer(ship, 100).position())
                                    .dot(new Vec3(
                                            ship.state.orientation()
                                                    .transform(new Vector3f(0, 0, 1))
                                    )) > 0.5
            ) {
                ship.acceptInputs(new ClientShipInputHandler.ShipInput(
                        -ship.state.currentInput().vertical(),
                        ship.state.currentInput().forward() == 0.3f ? -ship.state.currentInput().forward() : 0,
                        -ship.state.currentInput().strafe(),
                        0,
                        1,
                        0
                ));
            } else {
                ship.acceptInputs(new ClientShipInputHandler.ShipInput(
                        -ship.state.currentInput().vertical(),
                        1f,
                        -ship.state.currentInput().strafe(),
                        0,
                        0,
                        0
                ));
            }
        }

        long t7 = System.nanoTime();

        long total = t7 - t0;

        if (total > 10_000_000L) {
            Minagic.LOGGER.info(
                    "AI slow tick: total={} ns, desiredState={} ns, pilotMessage={} ns, grounded={} ns, attack={} ns, recovery={} ns, evasion={} ns, pursuit={} ns, state={}",
                    total,
                    t1 - t0,
                    t2 - t1,
                    t3 - t2,
                    t4 - t3,
                    t5 - t4,
                    t6 - t5,
                    t7 - t6,
                    state
            );
        }
    }
}
