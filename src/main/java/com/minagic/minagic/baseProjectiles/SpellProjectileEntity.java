package com.minagic.minagic.baseProjectiles;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public abstract class SpellProjectileEntity extends Projectile {
    protected double speed = 0;
    protected final Vec3 direction = Vec3.ZERO;
    protected double gravity = 0;
    protected boolean isBlockPiercing = false;
    protected boolean isEntityPiercing = false;
    protected int maxPierce = 10000000; // effectively infinite

    public SpellProjectileEntity(EntityType<? extends SpellProjectileEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(gravity == 0);
        this.setDeltaMovement(direction.normalize().scale(speed));
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 currentPos = this.position();
        Vec3 motion = this.getDeltaMovement().add(0, -gravity, 0);
        Vec3 nextPos = currentPos.add(motion);

        // --- BLOCK COLLISION ---
        if (!isBlockPiercing) {
            BlockHitResult blockHit = traceBlock(currentPos, nextPos);
            if (blockHit.getType() != HitResult.Type.MISS) {
                onHitBlock(blockHit);
                this.discard();
                return;
            }
        }

        // --- ENTITY COLLISION ---
        if (!isEntityPiercing) {
            for (EntityHitResult entityHit : findAllEntityHits(currentPos, nextPos)) {
                onHitEntity(entityHit);
            }
        }

        // --- MOVE PROJECTILE ---
        this.setPos(nextPos.x, nextPos.y, nextPos.z);
        this.setDeltaMovement(motion);
        this.setBoundingBox(this.makeBoundingBox());
    }

    // ---- collision helpers ---------------------------------------------------

    private BlockHitResult traceBlock(Vec3 start, Vec3 end) {
        return level().clip(new ClipContext(
                start, end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this
        ));
    }

    protected List<EntityHitResult> findAllEntityHits(Vec3 start, Vec3 end) {
        // Expand search area to cover entire motion path
        AABB searchBox = this.getBoundingBox()
                .expandTowards(end.subtract(start))
                .inflate(0.5D); // small buffer so tiny entities aren’t skipped

        // Gather all entities in range
        List<Entity> candidates = this.level().getEntities(this, searchBox, this::canHitEntity);
        List<EntityHitResult> results = new ArrayList<>();

        for (Entity target : candidates) {
            AABB targetBox = target.getBoundingBox().inflate(0.3D); // a little leniency for fast projectiles
            Optional<Vec3> intercept = targetBox.clip(start, end);
            intercept.ifPresent(vec3 -> results.add(new EntityHitResult(target, vec3)));
        }

        // Sort by distance from the start so you can process in order
        results.sort(Comparator.comparingDouble(hit -> hit.getLocation().distanceToSqr(start)));

        return results;
    }

    protected boolean canHitEntity(Entity entity) {
        return entity.isAlive() && entity.isPickable() && entity != this.getOwner();
    }

    // ---- hooks ---------------------------------------------------------------

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // no synced fields
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        if (maxPierce <= 0) {
            return;
        }

        super.onHitEntity(hitResult);
        maxPierce--;
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
    }
}