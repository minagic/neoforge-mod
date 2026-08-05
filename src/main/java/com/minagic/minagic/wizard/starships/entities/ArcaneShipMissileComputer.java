package com.minagic.minagic.wizard.starships.entities;

import com.minagic.minagic.baseProjectiles.SpellProjectileEntity;
import com.minagic.minagic.utilities.SpellUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

public class ArcaneShipMissileComputer implements SpellProjectileEntity.HomingComputer {

    private static final double TARGET_RANGE = 128.0;
    private static final double MIN_ALIGNMENT = 0.65;
    private static final double LEAD_TICKS = 4.0;

    /*
     * 0.0 = no steering.
     * 1.0 = instant snap.
     */
    private static final double HOMING_RESPONSE = 0.12;

    @Nullable
    protected UUID trackingUUID;

    @Nullable
    private transient LivingEntity cachedPilot;

    @Nullable
    private transient ArcaneShipEntity cachedShip;

    @Nullable
    private transient LivingEntity cachedTarget;


    @Override
    public Vec3 changeDir(
            SpellProjectileEntity og_projectile,
            Vec3 currentDirection
    ) {
        if (!(og_projectile instanceof ArcaneShipProjectile projectile) || projectile.level().isClientSide())
        {
            return currentDirection;
        }
        resolveLaunchContext(projectile);


        LivingEntity target = resolveOrAcquireTarget(projectile);

        if (target == null || !target.isAlive()) {
            return currentDirection;
        }

        Vec3 aimPoint = target.getBoundingBox()
                .getCenter()
                .add(target.getDeltaMovement().scale(LEAD_TICKS));

        Vec3 desiredDirection = aimPoint.subtract(projectile.position());

        if (desiredDirection.lengthSqr() < 1.0E-8) {
            return currentDirection;
        }

        desiredDirection = desiredDirection.normalize();

        Vec3 current = currentDirection.lengthSqr() < 1.0E-8
                ? desiredDirection
                : currentDirection.normalize();

        Vec3 steered = current
                .scale(1.0 - HOMING_RESPONSE)
                .add(desiredDirection.scale(HOMING_RESPONSE));

        if (steered.lengthSqr() < 1.0E-8) {
            return currentDirection;
        }

        return steered.normalize();
    }

    @Override
    public boolean stillLockedOn(SpellProjectileEntity projectile) {
        if (cachedTarget == null || !cachedTarget.isAlive()) return false;
        if (!(projectile instanceof ArcaneShipProjectile arcaneShipProjectile)) return false;
        Vec3 aimPoint = cachedTarget.getBoundingBox()
                .getCenter()
                .add(cachedTarget.getDeltaMovement().scale(LEAD_TICKS));

        Vec3 desiredDirection = aimPoint.subtract(projectile.position());
        return arcaneShipProjectile.physics.direction().dot(desiredDirection) > 0;
    }

    public Entity getShip(){
        return cachedShip;
    }

    private void resolveLaunchContext(ArcaneShipProjectile projectile) {
        if (cachedPilot == null
                || !cachedPilot.isAlive()
                || !cachedPilot.getUUID().equals(projectile.sourceUUID)) {

            cachedPilot = SpellUtils.resolveLivingEntityAcrossDimensions(projectile.sourceUUID, projectile.level().getServer());
        }

        if (cachedShip == null
                || !cachedShip.isAlive()
                || !cachedShip.getUUID().equals(projectile.shipUUID)) {

            LivingEntity resolvedShip =
                    SpellUtils.resolveLivingEntityAcrossDimensions(projectile.shipUUID, projectile.level().getServer());
            cachedShip = resolvedShip instanceof ArcaneShipEntity ship
                    ? ship
                    : null;
        }
    }

    @Nullable
    private LivingEntity resolveOrAcquireTarget(ArcaneShipProjectile projectile) {
        if (trackingUUID != null) {
            if (cachedTarget != null
                    && cachedTarget.isAlive()
                    && cachedTarget.getUUID().equals(trackingUUID)) {
                return cachedTarget;
            }

            LivingEntity resolved =
                    SpellUtils.resolveLivingEntityAcrossDimensions(
                            trackingUUID, projectile.level().getServer()
                    );

            if (isValidTarget(resolved, projectile)) {
                cachedTarget = resolved;
                return resolved;
            }

            trackingUUID = null;
            cachedTarget = null;
        }

        LivingEntity acquired = acquireTarget(projectile);

        if (acquired != null) {
            trackingUUID = acquired.getUUID();
            cachedTarget = acquired;
        }

        return acquired;
    }

    @Nullable
    private LivingEntity acquireTarget(ArcaneShipProjectile projectile) {
        if (cachedShip == null) {
            return null;
        }

        Vec3 origin = cachedShip.position();

        Vector3f forwardF = cachedShip.getState()
                .orientation()
                .transform(new Vector3f(0.0F, 0.0F, 1.0F));

        Vec3 shipForward = new Vec3(forwardF).normalize();

        AABB searchBox = cachedShip
                .getBoundingBox()
                .inflate(TARGET_RANGE);

        Optional<LivingEntity> nearest =
                projectile.level()
                        .getEntitiesOfClass(
                                LivingEntity.class,
                                searchBox
                        )
                        .stream()
                        .filter(entity -> {
                            Vec3 toEntity = entity
                                    .getBoundingBox()
                                    .getCenter()
                                    .subtract(origin);

                            if (toEntity.lengthSqr() < 1.0E-8) {
                                return false;
                            }

                            return toEntity
                                    .normalize()
                                    .dot(shipForward) >= MIN_ALIGNMENT;
                        })
                        .min(
                                Comparator.comparingDouble(entity ->
                                        targetScore(
                                                origin,
                                                shipForward,
                                                (LivingEntity) entity
                                        )
                                )
                        );

        return nearest.orElse(null);
    }

    private double targetScore(
            Vec3 origin,
            Vec3 forward,
            LivingEntity candidate
    ) {
        Vec3 offset = candidate
                .getBoundingBox()
                .getCenter()
                .subtract(origin);

        double distance = offset.length();

        if (distance < 1.0E-8) {
            return Double.POSITIVE_INFINITY;
        }

        double alignment = Mth.clamp(
                offset.normalize().dot(forward),
                -1.0,
                1.0
        );

        /*
         * Angular error is weighted heavily so the computer picks the
         * entity closest to the sight line, not merely the nearest entity.
         */
        double angularPenalty = 1.0 - alignment;

        return angularPenalty * TARGET_RANGE * 4.0 + distance;
    }

    private boolean isValidTarget(@Nullable LivingEntity entity, ArcaneShipProjectile projectile) {
        if (entity == null || !entity.isAlive()) {
            return false;
        }

        if (entity == cachedPilot || entity == cachedShip) {
            return false;
        }

        UUID uuid = entity.getUUID();

        if (projectile.sourceUUID != null && projectile.sourceUUID.equals(uuid)) {
            return false;
        }

        return projectile.shipUUID == null || !projectile.shipUUID.equals(uuid);
    }

    public @Nullable UUID getTrackingUUID() {
        return trackingUUID;
    }

    public void setTrackingUUID(@Nullable UUID trackingUUID) {
        this.trackingUUID = trackingUUID;
        this.cachedTarget = null;
    }
}