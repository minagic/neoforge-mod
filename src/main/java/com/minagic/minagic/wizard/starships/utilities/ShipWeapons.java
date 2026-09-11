package com.minagic.minagic.wizard.starships.utilities;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.powersource.ActivePowerSourceAttachment;
import com.minagic.minagic.capabilities.powersource.ShipPowerSourceAttachment;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import com.minagic.minagic.wizard.starships.utilities.weapons.targeting.NoTargetingComputer;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class ShipWeapons {
    public static void firePrimary(ArcaneShipEntity ship, int tick){
        PrimaryData data = ship.getPrimaryData();

        Minagic.LOGGER.info("Attempting to fire primary for {}", ship.debugIdentity());
        if (!(ActivePowerSourceAttachment.getActivePowerSource(ship) instanceof ShipPowerSourceAttachment shipPower)) {Minagic.LOGGER.info("Incorrect power source for {}", ship.debugIdentity());return;}
        if (tick%data.cooldown()!=0) {Minagic.LOGGER.info("Primary for {} is on cooldown", ship.debugIdentity()); return;}
        if (!shipPower.canConsume(new SpellCastContext(ship), null, data.fuelcost())) {Minagic.LOGGER.info("Insufficient power for primary at {}", ship.debugIdentity()); return;}

        Quaternionf orientation = new Quaternionf(ship.state.orientation());
        for(int i = 0; i<data.positions().size(); i++){
            // transform all:
            Vec3 pos = ship.getPosition(0).add(new Vec3(orientation.transform(data.positions().get(i).toVector3f())));
            Vec3 dir = new Vec3(orientation.transform(data.directions().get(i).toVector3f()));
            Minagic.LOGGER.info("Flight vector: {}", dir);
            if (ship.getPilot() == null) {
                data.projectile().create(ship.level(), pos, dir, ship.getUUID(), ship.getUUID(), new NoTargetingComputer());
            }
            else {
                data.projectile().create(ship.level(), pos, dir, ship.getPilot().getUUID(), ship.getUUID(), new NoTargetingComputer());
            }
        }

        shipPower.consume(new SpellCastContext(ship), null, data.fuelcost());

    }

    public static void fireOrdnance(ArcaneShipEntity ship){
        if (ship.getPilot() == null){
            ship.ordnance = ship.ordnance.fire(ship, ship.getUUID());
            return;
        }
        ship.ordnance = ship.ordnance.fire(ship, ship.getPilot().getUUID());
    }

}
