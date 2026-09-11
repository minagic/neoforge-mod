package com.minagic.minagic.wizard.starships.utilities.weapons.targeting;

import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipProjectile;
import net.minecraft.world.level.Level;

public class NoTargetingComputer extends TargetingComputer {
    @Override
    public TargetingComputer copyToMissile(Level level) {
        return new NoTargetingComputer();
    }

    @Override
    public void acquireTarget(ArcaneShipEntity ship) {
        // no-op
    }

    @Override
    public void confirmTarget(ArcaneShipProjectile projectile) {
        // no-op
    }

}
