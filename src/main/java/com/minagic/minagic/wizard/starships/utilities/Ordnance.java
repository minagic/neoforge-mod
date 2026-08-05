package com.minagic.minagic.wizard.starships.utilities;

import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.UUID;


public record Ordnance(
        ProjectileFactory projectileFactory,
        Vec3 localPosition,
        Vec3 localDirection,
        int manaToRecreate,
        int maxStock
) {
    @FunctionalInterface
    public interface ProjectileFactory {

        ArcaneShipProjectile create(
                Level level,
                Vec3 position,
                Vec3 direction,
                UUID sourceUUID,
                UUID shipUUID
        );
    }
    public ArcaneShipProjectile fire(
            ArcaneShipEntity ship,
            UUID pilotUUID
    ) {
        Quaternionf orientation =
                new Quaternionf(ship.getState().orientation());

        Vector3f positionOffsetF =
                orientation.transform(
                        localPosition.toVector3f()
                );

        Vector3f directionF =
                orientation.transform(
                        localDirection.toVector3f()
                );

        Vec3 worldPosition = ship.position()
                .add(new Vec3(positionOffsetF));

        Vec3 worldDirection =
                new Vec3(directionF).normalize();

        return projectileFactory.create(
                ship.level(),
                worldPosition,
                worldDirection,
                pilotUUID,
                ship.getUUID()
        );
    }
}