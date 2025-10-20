package com.minagic.minagic.baseProjectiles;

import com.minagic.minagic.Minagic;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import java.util.List;
import java.util.Optional;

public abstract class SpellProjectileEntity extends Projectile {
    protected double speed;
    protected Vec3 direction;
    protected double gravity;

    public SpellProjectileEntity(EntityType<? extends SpellProjectileEntity> type, Level level,
                                 double speed, Vec3 direction, double gravity) {
        super(type, level);
        this.speed = speed;
        this.direction = direction;
        this.setNoGravity(gravity == 0); // Disable gravity by default
        this.setDeltaMovement(direction.normalize().scale(speed));
        this.gravity = gravity;

    }

    // PHYSICS
    @Override
    public void tick() {

        // Collision check: block
        Vec3 currentPos = this.position();
        Vec3 motion = this.getDeltaMovement();
        motion = motion.add(0, -gravity, 0); // Apply gravity
        Vec3 nextPos = currentPos.add(motion);

        // collision with block
        BlockHitResult blockHit = this.level().clip(new ClipContext(
                currentPos,
                nextPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this
        ));

        // Handle block hit
        if (blockHit.getType() != HitResult.Type.MISS) {
            this.onHitBlock(blockHit);
            return;
        }

        // Collision check: entity
        EntityHitResult entityHit = findEntityHit(currentPos, nextPos);
        if (entityHit != null) {
            this.onHitEntity(entityHit);
            return;
        }

        // Move the entity
        this.setPos(nextPos.x, nextPos.y, nextPos.z);
        this.setBoundingBox(this.makeBoundingBox());

        super.tick(); // optional: keeps tick count, fires death events, etc.
    }

    // Custom entity raycast
    private EntityHitResult findEntityHit(Vec3 from, Vec3 to) {
        AABB searchBox = this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(0.25);
        List<Entity> candidates = this.level().getEntities(this, searchBox, this::canHitEntity);

        Entity closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Entity target : candidates) {
            AABB targetBox = target.getBoundingBox();
            Optional<Vec3> hitPos = targetBox.clip(from, to);

            if (hitPos.isPresent()) {
                double dist = from.distanceToSqr(hitPos.get());
                if (dist < closestDist) {
                    closest = target;
                    closestDist = dist;
                }
            }
        }

        return closest != null ? new EntityHitResult(closest) : null;
    }

    protected boolean canHitEntity(Entity entity) {
        return entity.isAlive() && entity.isPickable();
    }


    // HANDLING HITS
    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        this.discard();

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // No additional data to sync
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        this.discard();

    }
}