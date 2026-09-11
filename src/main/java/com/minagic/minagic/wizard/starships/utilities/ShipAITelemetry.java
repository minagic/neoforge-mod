package com.minagic.minagic.wizard.starships.utilities;

import net.minecraft.world.phys.Vec3;

public class ShipAITelemetry {
    public Vec3 net; public Vec3 gravity; public Vec3 drag; public boolean GPWS;
    public ShipAITelemetry(){
        this.drag = Vec3.ZERO;
        this.gravity = Vec3.ZERO;
        this.net = Vec3.ZERO;
        this.GPWS = false;
    }
}
