package com.minagic.minagic.wizard.starships.utilities.weapons.targeting;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipProjectile;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.*;

public class MissileTargetingComputer extends TargetingComputer {
    private static final double TARGET_RANGE = 128.0;
    private static final double MIN_ALIGNMENT = 0.65;
    private static final double LEAD_TICKS = 4.0;

    public MissileTargetingComputer(){
        this.cachedTarget = null;
        this.targetUUID = null;
    }

    public MissileTargetingComputer(Entity target, UUID uuid){
        this.cachedTarget = target;
        this.targetUUID = uuid;
    }
    @Override
    public TargetingComputer copyToMissile(Level level) {
        return new MissileTargetingComputer(cachedTarget, targetUUID);
    }

    @Override
    public void acquireTarget(ArcaneShipEntity ship) {
        long t0 = System.nanoTime();
        Level level = ship.level();
        if (level.isClientSide()) return;
//        if (cachedTarget != null && cachedTarget.getUUID() == targetUUID) return;
//        if (targetUUID != null) { resolveTargetIfExists(level); return;}

        List<Entity> blacklist = new ArrayList<>(List.of());
        blacklist.add(ship);
        if (ship.getPilot() != null){
            blacklist.add(ship.getPilot());
        }

        Vec3 forward =new Vec3(ship.state.orientation().transform(new Vector3f(0, 0, 1))).normalize();
        Vec3 origin = ship.position();
        Vec3 end = origin.add(forward.scale(TARGET_RANGE));

        AABB searchBox = new AABB(origin, end)
                .inflate(5);
        LivingEntity best = null;

        double bestScore = Double.POSITIVE_INFINITY;


        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, searchBox)) {
            if (blacklist.contains(entity) || entity.onGround()) {
                continue;
            }

            Vec3 offset = entity.getBoundingBox()
                    .getCenter()
                    .subtract(origin);

            double distSqr = offset.lengthSqr();

            if (distSqr < 1.0E-8) {
                continue;
            }

            double distance = Math.sqrt(distSqr);

            double alignment =
                    offset.dot(forward) / distance;

            if (alignment < MIN_ALIGNMENT) {
                continue;
            }

            double score =
                    (1.0 - alignment) * TARGET_RANGE * 4.0
                            + distance;

            if (score < bestScore) {
                bestScore = score;
                best = entity;
            }
        }

        if(best != null){

            this.cachedTarget = best;
            this.targetUUID = this.cachedTarget.getUUID();
            if (ship.getPilot() != null) Minagic.LOGGER.info("Ship {} is locked onto {} (UUID: {}) @ {}, time elasped: {}", ship.debugIdentity(), this.cachedTarget.getName(), this.cachedTarget.getUUID(), this.cachedTarget.position(), System.nanoTime() - t0);
        }
        else{
            this.cachedTarget = null;
            this.targetUUID = null;
            if (ship.getPilot() != null) Minagic.LOGGER.info("Ship {} Targeting failed, no target found", ship.debugIdentity());
        }

    }
    private static double targetScore(
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
    @Override
    public void confirmTarget(ArcaneShipProjectile projectile) {
        // no-op
    }
}
