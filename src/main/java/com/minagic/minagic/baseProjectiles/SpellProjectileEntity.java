package com.minagic.minagic.baseProjectiles;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.utilities.EntityFreezer;
import com.minagic.minagic.wizard.starships.utilities.weapons.flight.IGuidingSystem;
import com.minagic.minagic.wizard.starships.utilities.weapons.flight.NoGuidanceSystem;
import com.minagic.minagic.wizard.starships.utilities.weapons.targeting.NoTargetingComputer;
import com.minagic.minagic.wizard.starships.utilities.weapons.targeting.TargetingComputer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;

public abstract class SpellProjectileEntity extends Projectile implements EntityFreezer.IFreezibleEntity {
    protected double speed = 0;
    protected Vec3 direction = Vec3.ZERO;
    protected double gravity = 0;
    protected boolean isBlockPiercing = false;
    protected boolean isEntityPiercing = false;
    protected int maxPierce = 10000000; // effectively infinite


    public record ProjectilePhysicsData(
            double speed,
            Vec3 direction,
            double gravity,
            boolean pierceBlocks,
            boolean pierceEntities,
            int maxEntityPierce,
            boolean isFrozen
    ) {

        public static final Codec<ProjectilePhysicsData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.DOUBLE.fieldOf("speed").forGetter(ProjectilePhysicsData::speed),
                        Vec3.CODEC.fieldOf("direction").forGetter(ProjectilePhysicsData::direction),
                        Codec.DOUBLE.fieldOf("gravity").forGetter(ProjectilePhysicsData::gravity),
                        Codec.BOOL.fieldOf("pierceBlocks").forGetter(ProjectilePhysicsData::pierceBlocks),
                        Codec.BOOL.fieldOf("pierceEntities").forGetter(ProjectilePhysicsData::pierceEntities),
                        Codec.INT.fieldOf("maxEntityPierce").forGetter(ProjectilePhysicsData::maxEntityPierce),
                        Codec.BOOL.fieldOf("isFrozen").forGetter(ProjectilePhysicsData::isFrozen)
                ).apply(instance, ProjectilePhysicsData::new)
        );

        public ProjectilePhysicsData decreasedHitLimit(){
            return new ProjectilePhysicsData(
                    speed,
                    direction,
                    gravity,
                    pierceBlocks,
                    pierceEntities,
                    maxEntityPierce - 1,
                    isFrozen
            );
        }

        public ProjectilePhysicsData freeze(){
            return new ProjectilePhysicsData(
                    speed,
                    direction,
                    gravity,
                    pierceBlocks,
                    pierceEntities,
                    maxEntityPierce,
                    true
            );
        }

        public ProjectilePhysicsData unfreeze(){
            return new ProjectilePhysicsData(
                    speed,
                    direction,
                    gravity,
                    pierceBlocks,
                    pierceEntities,
                    maxEntityPierce,
                    false
            );
        }

        public ProjectilePhysicsData changeDirection(Vec3 direction){
            return new ProjectilePhysicsData(
                    speed,
                    direction,
                    gravity,
                    pierceBlocks,
                    pierceEntities,
                    maxEntityPierce ,
                    isFrozen
            );
        }
    }


    public @Nullable SpellProjectileEntity.ProjectilePhysicsData physics;

    public SpellProjectileEntity(EntityType<? extends SpellProjectileEntity> type, Level level) {
        super(type, level);
    }

    @Deprecated
    public void createPhysicsIfNull(){

        if (this.physics == null){
            Minagic.LOGGER.debug("Creating Physics Data");
            this.physics = buildFromLegacy();
        }
    }
    @Deprecated
    protected ProjectilePhysicsData buildFromLegacy(){
        return new ProjectilePhysicsData(
                this.speed,
                this.direction,
                this.gravity,
                this.isBlockPiercing,
                this.isEntityPiercing,
                this.maxPierce,
                false
        );

    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        // --- Physics ---
        if (this.physics != null) {
            output.store("physics", ProjectilePhysicsData.CODEC, this.physics);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.physics = input.read("physics", ProjectilePhysicsData.CODEC)
                .orElse(new ProjectilePhysicsData(
                        0.0,
                        Vec3.ZERO,
                        0.0,
                        false,
                        false,
                        0,
                        false
                ));

        // --- Sanity guard (VERY important) ---
        if (this.physics.direction().lengthSqr() < 1e-6) {
            this.physics = new ProjectilePhysicsData(
                    this.physics.speed(),
                    new Vec3(0, 0, 1), // safe fallback direction
                    this.physics.gravity(),
                    this.physics.pierceBlocks(),
                    this.physics.pierceEntities(),
                    this.physics.maxEntityPierce(),
                    this.physics.isFrozen()
            );
        }
    }


    public interface HomingComputer {

        Vec3 changeDir(

                SpellProjectileEntity projectile,

                Vec3 currentDirection

        );

        boolean stillLockedOn(SpellProjectileEntity projectile);

        //Vec3 currentLockOnPosition();
        HomingComputer NONE = new NoHomingComputer();


    }



    public static final class NoHomingComputer implements HomingComputer {

        @Override

        public Vec3 changeDir(

                SpellProjectileEntity projectile,

                Vec3 currentDirection

        ) {

            return currentDirection;

        }
        public Vec3 currentLockOnPosition(){
            return Vec3.ZERO;
        }

        public boolean stillLockedOn(SpellProjectileEntity projectile){
            return true;
        }

    }


    public TargetingComputer computer = new NoTargetingComputer();
    public IGuidingSystem guidingSystem = new NoGuidanceSystem();
    @Override
    public void tick() {
        super.tick();
        if (isFrozen()){
            Minagic.LOGGER.debug("FROZEN, cancelling movement");
            return;
        }
        createPhysicsIfNull();


        this.physics = this.physics.changeDirection(guidingSystem.correctCourse(this, computer.lockOnPosition(this.level())));
        Vec3 dir = this.physics.direction.normalize();
        Vec3 delta = dir.scale(this.physics.speed).add(0.0, -this.physics.gravity, 0.0);

        faceVelocity();

        Vec3 currentPos = this.position();
        Vec3 nextPos = currentPos.add(delta);

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
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        assert this.physics != null;
        if (this.physics.maxEntityPierce < 0) {
            return;
        }

        super.onHitEntity(hitResult);
        hitEntity(hitResult);
        this.physics = this.physics.decreasedHitLimit();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult hitResult) {
        assert this.physics != null;
        if (this.physics.pierceBlocks){
            return;
        }
        super.onHitBlock(hitResult);
        hitBlock(hitResult);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        // no synced fields
    }

    protected void hitBlock(BlockHitResult hitResult){
        // no-op
    }

    protected void hitEntity(EntityHitResult hitResult){
        // no-op
    }


    public void faceVelocity() {
        assert this.physics != null;
        Vec3 flightDirection = this.physics.direction;
        if (flightDirection.lengthSqr() < 1e-6) return;

        float yaw = (float)(Mth.atan2(flightDirection.z, flightDirection.x) * (180F / Math.PI)) - 90F;
        float pitch = (float)(-(Mth.atan2(flightDirection.y,
                Math.sqrt(flightDirection.x * flightDirection.x + flightDirection.z * flightDirection.z)) * (180F / Math.PI)));

        this.setYRot(yaw);
        this.setXRot(pitch);
    }

    // IFreezibleEntity

    @Override
    public void freeze(){
        Minagic.LOGGER.debug("[SPELL PROJECTILE ENTITY] Freezing speed");
        assert this.physics != null;
        this.physics = this.physics.freeze();
    }

    @Override
    public void unfreeze(){
        assert this.physics != null;
        this.physics = this.physics.unfreeze();
    }

    @Override
    public boolean isFrozen(){
        if (this.physics == null){
            return false;
        }
        return this.physics.isFrozen;
    }
}