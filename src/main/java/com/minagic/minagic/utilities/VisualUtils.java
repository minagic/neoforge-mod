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

    public static void createParticlesInSphere(ServerLevel level, Vec3 center, double radius, ParticleOptions options, int density) {
        // Use spherical coordinates to generate evenly-ish spaced points on a sphere surface
        double offset = 2.0 / density;
        double increment = Math.PI * (3.0 - Math.sqrt(5.0)); // golden angle

        for (int i = 0; i < density; i++) {
            double y = i * offset - 1 + (offset / 2);
            double r = Math.sqrt(1 - y * y);

            double phi = i * increment;

            double x = Math.cos(phi) * r;
            double z = Math.sin(phi) * r;

            // Scale to radius and translate to center
            double px = center.x + x * radius;
            double py = center.y + y * radius;
            double pz = center.z + z * radius;

            level.sendParticles(
                    options, px, py, pz, 1, 0 , 0, 0, 0);
        }
    }
}
