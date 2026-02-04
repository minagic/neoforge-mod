package com.minagic.minagic.utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
        return getEntitiesInXZColumnBox(level, pos1, pos2, type, entity -> true);
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

    public static <T extends Entity> List<T> findEntitiesInRadius(
            Level level,
            Vec3 center,
            double radius,
            Class<T> entityType,
            @Nullable Predicate<T> filter,
            @Nullable Set<Entity> exclusions
    ) {
        AABB box = new AABB(center, center).inflate(radius);

        return level.getEntitiesOfClass(entityType, box, entity -> {
            if (exclusions != null && exclusions.contains(entity))
                return false;
            if (filter != null && !filter.test(entity)) return false;
            return entity.distanceToSqr(center) <= radius * radius;
        });
    }

    public static boolean hasTheoreticalLineOfSight(Entity observer, Entity target) {
        Level level = observer.level();

        // Entity eye positions
        Vec3 start = observer.getEyePosition();
        Vec3 end = target.getEyePosition();

        // Ray trace from observer to target
        BlockHitResult result = level.clip(new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                observer
        ));

        // If the result hit nothing or hit the target's bounding box, return true
        return result.getType() == HitResult.Type.MISS || target.getBoundingBox().contains(result.getLocation());
    }

    public static boolean canSeeSky(Entity entity) {
        return entity.level().canSeeSky(entity.blockPosition());
    }

    public static double findSurfaceY(Level level, double x, double z) {
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.containing(x, 0, z)).getY();
    }


    public static BlockPos getTargetBlockPos(Entity entity, double range) {
        Level world = entity.level();
        Vec3 start = entity.getEyePosition(1.0F); // Position of eyes
        Vec3 look = entity.getLookAngle();        // Look direction vector
        Vec3 end = start.add(look.scale(range));  // Where the look vector ends

        // Use ray trace to detect block
        ClipContext context = new ClipContext(
                start,
                end,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                entity
        );

        BlockHitResult result = world.clip(context);

        if (result.getType() == HitResult.Type.BLOCK) {
            return result.getBlockPos();
        }

        return null; // No block hit
    }

    public static LivingEntity resolveLivingEntityAcrossDimensions(UUID uuid, MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            Entity entity = level.getEntity(uuid);
            if (entity instanceof LivingEntity living && living.isAlive()) {
                return living;
            }
        }
        throw new InvalidParameterException("Entity with UUID: " + uuid + " not found");
    }

}
