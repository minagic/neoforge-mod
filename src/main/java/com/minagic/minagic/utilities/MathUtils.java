package com.minagic.minagic.utilities;


import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

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
}
