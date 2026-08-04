package com.minagic.minagic.wizard.starships.utilities;

public record EngineSystem(

        float maxThrust,

        float fuelPerTick

) {

    public EngineSystem {

        if (maxThrust < 0.0F) {

            throw new IllegalArgumentException(

                    "Engine thrust cannot be negative"

            );

        }

        if (fuelPerTick < 0.0F) {

            throw new IllegalArgumentException(

                    "Engine fuel cost cannot be negative"

            );

        }

    }

}