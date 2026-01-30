package com.minagic.minagic.utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
                    options, px, py, pz, 1, 0, 0, 0, 0);
        }
    }

    public static void createParticleRay(ServerLevel level, Vec3 start, Vec3 end, ParticleOptions options, int count) {
        Vec3 direction = end.subtract(start);
        double length = direction.length();
        Vec3 step = direction.normalize().scale(length / count);

        for (int i = 0; i <= count; i++) {
            Vec3 pos = start.add(step.scale(i));
            level.sendParticles(options, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
        }
    }

    public static void createParticleRayUntilBlock(ServerLevel level, Vec3 start, Vec3 direction, ParticleOptions options, int count) {
        Vec3 normDir = direction.normalize();
        double stepLength = 0.25; // Smaller = smoother ray, but more checks
        Vec3 step = normDir.scale(stepLength);
        Vec3 currentPos = start;

        for (int i = 0; i < count; i++) {
            BlockPos blockPos = BlockPos.containing(currentPos);
            if (!level.isEmptyBlock(blockPos)) {
                break; // Stop if we hit a non-air block
            }

            level.sendParticles(options, currentPos.x, currentPos.y, currentPos.z, 1, 0, 0, 0, 0);
            currentPos = currentPos.add(step);
        }
    }

    public static void spawnExplosionVFX(ServerLevel level, Vec3 center, double maxRadius) {
        int rings = 20; // how many frames of expansion
        int particlesPerRing = 200;

        for (int r = 0; r < rings; r++) {
            double radius = (maxRadius * r) / rings;

            for (int i = 0; i < particlesPerRing; i++) {
                double theta = level.random.nextDouble() * Math.PI * 2;
                double phi = Math.acos(2 * level.random.nextDouble() - 1);

                double x = center.x + radius * Math.sin(phi) * Math.cos(theta);
                double y = center.y + radius * Math.sin(phi) * Math.sin(theta);
                double z = center.z + radius * Math.cos(phi);

                level.sendParticles(
                        ParticleTypes.EXPLOSION_EMITTER,
                        x, y, z,
                        1,
                        0, 0, 0,
                        0.0
                );
            }
        }
    }
}
