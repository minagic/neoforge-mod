package com.minagic.minagic.utilities;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VisualUtils {
    public static void spawnRadialParticleRing(Level level, Vec3 center, double radius, int density, ParticleOptions particle) {
        int points = Math.max(8, density); // minimum number of points
        ServerLevel world = (ServerLevel) level;
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            double y = center.y;

            // Emit particle with no movement
            world.sendParticles(particle, x, y, z, 1, 0, 0, 0, 0);
        }
    }
}
