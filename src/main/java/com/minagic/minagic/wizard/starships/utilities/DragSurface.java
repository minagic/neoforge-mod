package com.minagic.minagic.wizard.starships.utilities;

import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import net.minecraft.world.phys.Vec3;

public record DragSurface(Vec3 localNormal, float area, float coefficient){
    public float getDragContribution(
            Vec3 velocity,
            ArcaneShipEntity ship
    ) {
        Vec3 worldNormal = new Vec3(
                ship.getState().orientation()
                        .transform(localNormal.toVector3f())
        ).normalize();

        double incomingSpeed = Math.max(
                0.0,
                -velocity.dot(worldNormal)
        );

        return (float) (
                area
                        * coefficient
                        * incomingSpeed
                       // * incomingSpeed
        );
    }
}