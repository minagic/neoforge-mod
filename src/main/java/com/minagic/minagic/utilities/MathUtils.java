package com.minagic.minagic.utilities;


import net.minecraft.core.BlockPos;
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
}
