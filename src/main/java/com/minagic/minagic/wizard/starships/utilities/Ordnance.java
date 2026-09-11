package com.minagic.minagic.wizard.starships.utilities;

import com.minagic.minagic.wizard.starships.entities.ArcaneShipProjectile;
import com.minagic.minagic.wizard.starships.rendering.OrdnanceRenderer;
import com.minagic.minagic.wizard.starships.utilities.weapons.targeting.TargetingComputer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;


public record Ordnance(
        ProjectileFactory projectileFactory,
        List<Vec3> localPosition,
        List<Vec3> localDirection,
        int manaToRecreate,
        int maxStock,
        int recreationPerTick,
        OrdnanceRenderer renderer,
        TargetingComputerProvider computerProvider

) {
    @FunctionalInterface
    public interface ProjectileFactory {

        ArcaneShipProjectile create(
                Level level,
                Vec3 position,
                Vec3 direction,
                UUID sourceUUID,
                UUID shipUUID,
                TargetingComputer computer
        );
    }

    @FunctionalInterface
    public interface TargetingComputerProvider{
        TargetingComputer getComputer();
    }

}