package com.minagic.minagic.wizard.starships.utilities;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

public record PhysicsData(
        List<DragSurface> dragProfile,
        Vec3 maxAngularSpeed,
        float rollDecay,
        float mass,
        float aimSpeed,
        Map<EngineDirection, EngineSystem> engines

) {

    private EngineSystem engine(EngineDirection direction) {

        EngineSystem system = engines.get(direction);

        if (system == null) {

            throw new IllegalStateException(

                    "Missing engine definition: " + direction

            );

        }

        return system;

    }

    public float getRequestedFuel(Vector3f thrustControl) {

        float xFuel;

        if (thrustControl.x >= 0.0F) {

            xFuel = thrustControl.x

                    * engine(EngineDirection.RIGHT).fuelPerTick();

        } else {

            xFuel = -thrustControl.x

                    * engine(EngineDirection.LEFT).fuelPerTick();

        }

        float yFuel;

        if (thrustControl.y >= 0.0F) {

            yFuel = thrustControl.y

                    * engine(EngineDirection.UP).fuelPerTick();

        } else {

            yFuel = -thrustControl.y

                    * engine(EngineDirection.DOWN).fuelPerTick();

        }

        float zFuel;

        if (thrustControl.z >= 0.0F) {

            zFuel = thrustControl.z

                    * engine(EngineDirection.FORWARD).fuelPerTick();

        } else {

            zFuel = -thrustControl.z

                    * engine(EngineDirection.BACKWARD).fuelPerTick();

        }

        return xFuel + yFuel + zFuel;

    }

    public Vec3 getLocalThrust(Vector3f thrustControl) {
        double thrustX;

        if (thrustControl.x >= 0.0F) {
            thrustX = thrustControl.x
                    * engine(EngineDirection.RIGHT).maxThrust();
        } else {
            thrustX = thrustControl.x
                    * engine(EngineDirection.LEFT).maxThrust();
        }

        double thrustY;

        if (thrustControl.y >= 0.0F) {
            thrustY = thrustControl.y
                    * engine(EngineDirection.UP).maxThrust();
        } else {
            thrustY = thrustControl.y
                    * engine(EngineDirection.DOWN).maxThrust();
        }

        double thrustZ;

        if (thrustControl.z >= 0.0F) {
            thrustZ = thrustControl.z
                    * engine(EngineDirection.FORWARD).maxThrust();
        } else {
            thrustZ = thrustControl.z
                    * engine(EngineDirection.BACKWARD).maxThrust();
        }

        return new Vec3(
                thrustX,
                thrustY,
                thrustZ
        );
    }
}
