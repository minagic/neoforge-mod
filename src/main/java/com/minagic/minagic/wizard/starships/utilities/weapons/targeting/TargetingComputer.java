package com.minagic.minagic.wizard.starships.utilities.weapons.targeting;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import java.util.UUID;

public abstract class TargetingComputer {
    protected UUID targetUUID;
    protected Entity cachedTarget;





    protected void resolveTargetIfExists(Level level){
        if (level.isClientSide()) return;
        if (targetUUID == null) {
            Minagic.LOGGER.warn("WARNING: TARGET DID NOT EXIST WHEN ASKED FOR");
            return;
        }
        if (cachedTarget == null || cachedTarget.getUUID() != targetUUID) {
            cachedTarget = level.getEntityInAnyDimension(targetUUID);
        }

    }

    public abstract TargetingComputer copyToMissile(Level level);
    public abstract void acquireTarget(ArcaneShipEntity ship);
    public abstract void confirmTarget(ArcaneShipProjectile projectile);
    public Vector3f lockOnPosition(Level level){
        if (level.isClientSide()) return new Vector3f(0, 0, 0);
        if (targetUUID == null){
            Minagic.LOGGER.warn("Lock on position was requested, no lock on position exists");
            return new Vector3f(0, 0, 0);
        }
        resolveTargetIfExists(level);
        return cachedTarget.position().toVector3f();


    }
    public Entity target(Level level){
        if (level.isClientSide()) return null;
        if (targetUUID == null){
            Minagic.LOGGER.warn("Target was requested, no target exists");
            return null;
        }
        resolveTargetIfExists(level);
        return cachedTarget;
    }

    public String lockOnDescription(Level level) {
        if (level.isClientSide()) return "none";
        if (targetUUID == null){
            Minagic.LOGGER.warn("Target was requested, no target exists");
            return "none";
        }
        resolveTargetIfExists(level);
        return cachedTarget.getPlainTextName();
    }
}
