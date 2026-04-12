package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.DamageTypes;
import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.ChanneledAutonomousSpell;
import com.minagic.minagic.baseProjectiles.SpellProjectileEntity;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.powersource.SorceryPowerSourceAttachment;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spells.AOEHit;
import com.minagic.minagic.utilities.MathUtils;
import com.minagic.minagic.utilities.SpellUtils;
import com.minagic.minagic.utilities.VisualUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

@AutoDetection.Spell
public class CelestialBombardment extends ChanneledAutonomousSpell implements SorceryPowerSourceAttachment.ISorcerySpell {

    public CelestialBombardment() {
        super();
        this.cooldown = 400;
        this.spellName = "Celestial Bombardment";
        this.idName = "celestial_bombardment";
        this.simulacraMaxLifetime = 200;
        this.simulacraThreshold = 5;
        this.manaCost = 5;
    }

    private static Vec3[] computeFiringSolution(Vec3 sourcePos, Vec3 mainTargetPos, Vec3 targetPos, double altitude, double angleDeg) {
        // Normalize Y positions
        Vec3 flatSource = new Vec3(sourcePos.x, 0, sourcePos.z);
        Vec3 flatMainTarget = new Vec3(mainTargetPos.x, 0, mainTargetPos.z);

        // Get horizontal direction (XZ plane)
        Vec3 flatDirection = flatMainTarget.subtract(flatSource).normalize();

        // Convert angle to radians
        double angleRad = Math.toRadians(angleDeg);

        // Compute vertical and horizontal scaling from angle
        double horizontalComponent = Math.cos(angleRad); // base length projection
        double verticalComponent = -Math.sin(angleRad); // downward (negative Y)

        // Final direction vector
        Vec3 direction = new Vec3(
                flatDirection.x * horizontalComponent,
                verticalComponent,
                flatDirection.z * horizontalComponent
        ).normalize();

        // Spawn position: start at 'altitude' along that direction but far enough back to "aim at" target
        double verticalDistance = altitude - targetPos.y;
        double totalDistance = verticalDistance / -verticalComponent;

        Vec3 spawnPos = targetPos.subtract(direction.scale(totalDistance));

        return new Vec3[]{spawnPos, direction};
    }

    @Override
    public void cast(SpellCastContext ctx, @Nullable SimulacrumData simData) {
        int XZRange = 5;
        int targetCount = 5;
        int YRange = 3;


        LivingEntity target = ctx.target;

        BlockPos targetedBlock = SpellUtils.getTargetBlockPos(target, 48);
        if (targetedBlock == null) {
            return;
        }
        // generate targets
        RandomSource random = ctx.level().random;

        ArrayList<BlockPos> targets = new ArrayList<>();
        for (int i = 0; i < targetCount; i++) {
            int XOffset = random.nextInt(-XZRange, XZRange);
            int ZOffset = random.nextInt(-XZRange, XZRange);

            BlockPos currentBlockPos = new BlockPos(targetedBlock.getX() + XOffset, 0, targetedBlock.getZ() + ZOffset);
            currentBlockPos = new BlockPos(currentBlockPos.getX(),
                    (int) SpellUtils.findSurfaceY(ctx.level(), currentBlockPos.getX(), currentBlockPos.getZ()),
                    currentBlockPos.getZ());
            targets.add(currentBlockPos);
            Minagic.LOGGER.debug("Celestial Bombardment locked target {}", currentBlockPos);

        }


        double baseAltitude = SpellUtils.findSurfaceY(ctx.level(), ctx.target.position().x, ctx.target.position().z);


        ArrayList<Integer> altitudes = new ArrayList<>();
        for (int i = 0; i < targetCount; i++) {
            altitudes.add((int) baseAltitude + random.nextInt(-YRange, YRange) + 50);
        }

        for (int i = 0; i < targetCount; i++) {
            Vec3[] pos_dir = computeFiringSolution(ctx.caster.position(), MathUtils.blockPosToVec3(targetedBlock), MathUtils.blockPosToVec3(targets.get(i)), altitudes.get(i), 35);
            StarShard shard = new StarShard(ctx.level(), pos_dir[0], pos_dir[1]);
            Minagic.LOGGER.debug("Celestial Bombardment spawning StarShard at {}", Arrays.toString(pos_dir));
            shard.setOwner(ctx.caster);
            ctx.level().addFreshEntity(shard);
        }


    }

    @Override
    public String getRequiredBloodline() {
        return SorceryPowerSourceAttachment.BLOODLINE_CELESTIAL;
    }

    @Override
    public int getRequiredAffinityLevel() {
        return 17;
    }

    public static class StarShard extends SpellProjectileEntity implements ItemSupplier {
        public StarShard(EntityType<? extends CelestialBombardment.StarShard> type, Level level) {
            super(type, level);
            this.speed = 0f;
        }

        public StarShard(Level level, Vec3 position, Vec3 direction) {
            super(Minagic.STAR_SHARD.get(), level);

            this.speed = 1;
            this.direction = direction;
            this.isEntityPiercing = false;
            this.setPos(position.x, position.y, position.z);

        }

        public StarShard(Level level, PhysicsData physics){
            super(Minagic.STAR_SHARD.get(), level);
            this.physics = physics;
        }

        @Override
        public void onHitBlock(@NotNull BlockHitResult result) {
            if (this.level().isClientSide()) return;
            VisualUtils.createParticlesInSphere((ServerLevel) this.level(), this.position(), 4, ParticleTypes.END_ROD, 40);
            AOEHit.applyAOE(
                    this.getOwner(),
                    this,
                    Set.of(DamageTypes.RADIANT),
                    12,
                    4,
                    result.getBlockPos()
            );
            this.discard();
        }


        @Override
        public @NotNull ItemStack getItem() {
            return new ItemStack(Items.PRISMARINE_CRYSTALS);
        }
    }
}
