package com.minagic.minagic.utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;

public class SpellUtils {
    public static <T extends Entity> List<T> getEntitiesInBox(
            Level level,
            BlockPos pos1,
            BlockPos pos2,
            Class<T> type // Use Entity.class if you want all
    ) {
        AABB box = new AABB(pos1).expandTowards(MathUtils.blockPosToVec3(pos2)); // Safe bounds no matter order
        return level.getEntitiesOfClass(type, box);
    }

    public static <T extends Entity> List<T> getEntitiesInXZColumnBox(Level level, BlockPos pos1, BlockPos pos2, Class<T> type) {
        return getEntitiesInXZColumnBox(level, pos1, pos2, type);
    }

    /**
     * Same as above, with a predicate filter
     */
    public static <T extends Entity> List<T> getEntitiesInXZColumnBox(Level level, BlockPos pos1, BlockPos pos2, Class<T> type, Predicate<T> filter) {
        int minX = Math.min(pos1.getX(), pos2.getX());
        int maxX = Math.max(pos1.getX(), pos2.getX()) + 1;
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ()) + 1;

        AABB verticalColumn = new AABB(
                minX, level.getMinY(), minZ,
                maxX, level.getMaxY(), maxZ
        );

        return level.getEntitiesOfClass(type, verticalColumn, filter);
    }

    public static boolean canSeeSky(Entity entity) {
        return entity.level().canSeeSky(entity.blockPosition());
    }

    public static double findSurfaceY(Level level, double x, double z) {
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.containing(x, 0, z)).getY();
    }

}
