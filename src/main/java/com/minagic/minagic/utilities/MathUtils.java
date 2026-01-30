package com.minagic.minagic.utilities;


import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import static java.lang.Math.abs;

public class MathUtils {
    public static Vec3 blockPosToVec3(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    /**
     * Converts a Vec3 to a BlockPos by flooring its coordinates.
     * (Same behavior as Minecraft's built-in BlockPos constructor)
     */
    public static BlockPos vec3ToBlockPos(Vec3 vec) {
        return new BlockPos((int) Math.floor(vec.x), (int) Math.floor(vec.y), (int) Math.floor(vec.z));
    }

    /**
     * Converts a Vec3 to a BlockPos by rounding to the nearest integer instead of flooring.
     * Useful for particle targeting or raycasting endpoints.
     */
    public static BlockPos vec3ToRoundedBlockPos(Vec3 vec) {
        return new BlockPos((int) Math.round(vec.x), (int) Math.round(vec.y), (int) Math.round(vec.z));
    }

    /**
     * Gets a Vec3 from BlockPos corner (no offset).
     * Use this if you explicitly want the *corner* position of a block.
     */
    public static Vec3 blockCornerToVec3(BlockPos pos) {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ());
    }

    public static double areaBetween(Vec3 vec1, Vec3 vec2) {
        return abs((vec2.x + 1 - vec1.x) * (vec2.z + 1 - vec1.z));
    }

    public static double areaBetween(BlockPos pos1, BlockPos pos2) {
        return areaBetween(blockPosToVec3(pos1), blockPosToVec3(pos2));
    }

    public static Vec3[] twoVectorsWithAngle(Vec3 origin, double angleRad, double length, RandomSource rand) {

        // 1. Random base direction (unit vector)
        Vec3 dir = randomUnitVector(rand);

        // 2. Find a perpendicular vector
        Vec3 up = Math.abs(dir.y) < 0.99 ? new Vec3(0,1,0) : new Vec3(1,0,0);
        Vec3 right = dir.cross(up).normalize();

        // 3. Rotate dir by ± angle/2 around "right"
        double half = angleRad / 2.0;

        Vec3 v1 = rotateAroundAxis(dir, right, half).scale(length).add(origin);
        Vec3 v2 = rotateAroundAxis(dir, right, -half).scale(length).add(origin);

        return new Vec3[]{v1, v2};
    }

    // -----------------------
    // Helpers
    // -----------------------

    private static Vec3 randomUnitVector(RandomSource rand) {
        double x = rand.nextDouble() * 2 - 1;
        double y = rand.nextDouble() * 2 - 1;
        double z = rand.nextDouble() * 2 - 1;
        return new Vec3(x, y, z).normalize();
    }

    // Rodrigues' rotation formula
    private static Vec3 rotateAroundAxis(Vec3 v, Vec3 axis, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        return v.scale(cos)
                .add(axis.cross(v).scale(sin))
                .add(axis.scale(axis.dot(v) * (1 - cos)));
    }
}
