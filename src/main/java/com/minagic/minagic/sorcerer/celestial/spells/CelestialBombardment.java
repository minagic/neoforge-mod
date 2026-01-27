package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.DamageTypes;
import com.minagic.minagic.Minagic;
import com.minagic.minagic.MinagicDamage;
import com.minagic.minagic.api.spells.ChanneledAutonomousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.api.spells.SpellValidator;
import com.minagic.minagic.baseProjectiles.SpellProjectileEntity;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.SpellMetadata;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import com.minagic.minagic.spells.AOEHit;
import com.minagic.minagic.utilities.MathUtils;
import com.minagic.minagic.utilities.SpellUtils;
import com.minagic.minagic.utilities.SpellValidationResult;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CelestialBombardment extends ChanneledAutonomousSpell {

    public CelestialBombardment(){
        super();
        this.cooldown = 400     ;
        this.spellName = "Celestial Bombardment";
        this.simulacraMaxLifetime = 200;
        this.simulacraThreshold = 5;
        this.manaCost = 0;
    }

    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses() {
        return List.of(new DefaultGates.ClassGate.AllowedClass(
                PlayerClassEnum.SORCERER,
                PlayerSubClassEnum.SORCERER_CELESTIAL,
                17
        ));
    }

    @Override
    public void cast(SpellCastContext ctx, @Nullable SimulacrumData simData){
        SpellGatePolicyGenerator.build(SpellEventPhase.CAST, this.getAllowedClasses(), null, 5, null, false, this)
                .setEffect((context, simulacrumData) -> {
                    int XZRange = 5;
                    int targetCount = 5;
                    int YRange = 3;


                    LivingEntity target = context.target;

                    BlockPos targetedBlock = SpellUtils.getTargetBlockPos(target, 48);
                    if (targetedBlock == null) {
                        return;
                    }
                    System.out.println("[Celestial Bombardment] Primary Target Locked: " + targetedBlock);
                    // generate targets
                    RandomSource random = context.level().random;

                    ArrayList<BlockPos> targets = new ArrayList<>();
                    for (int i = 0; i<targetCount; i++){
                        int XOffset = random.nextInt(-XZRange, XZRange);
                        int ZOffset = random.nextInt(-XZRange, XZRange);

                        BlockPos currentBlockPos = new BlockPos(targetedBlock.getX() + XOffset, 0, targetedBlock.getZ() + ZOffset);
                        currentBlockPos = new BlockPos(currentBlockPos.getX(),
                                (int)SpellUtils.findSurfaceY(context.level(), currentBlockPos.getX(), currentBlockPos.getZ()),
                                currentBlockPos.getZ());
                        targets.add(currentBlockPos);
                        System.out.println("[Celestial Bombardment] Locked Additional Target: "+ currentBlockPos);

                    }



                    double baseAltitude = SpellUtils.findSurfaceY(context.level(), context.target.position().x, context.target.position().z);


                    ArrayList<Integer> altitudes = new ArrayList<>();
                    for (int i = 0; i<targetCount; i++){
                        altitudes.add((int)baseAltitude + random.nextInt(-YRange, YRange) + 50);
                    }

                    for (int i = 0; i < targetCount; i++){
                        Vec3[] pos_dir = computeFiringSolution(context.caster.position(), MathUtils.blockPosToVec3(targetedBlock), MathUtils.blockPosToVec3(targets.get(i)), altitudes.get(i), 35);
                        StarShard shard = new StarShard(context.level(), pos_dir[0], pos_dir[1]);
                        System.out.println("[Celestial Bombardment] Spawning StarShard at " + Arrays.toString(pos_dir));
                        shard.setOwner(context.caster);
                        context.level().addFreshEntity(shard);
                    }
                })
                .execute(ctx, simData);


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

        return new Vec3[] { spawnPos, direction };
    }

    public static class StarShard extends SpellProjectileEntity implements ItemSupplier {
        public StarShard(EntityType<? extends CelestialBombardment.StarShard> type, Level level) {
            super(type, level);
            this.speed = 0f;
        }

        public StarShard(Level level, Vec3 position, Vec3 direction) {
            super(Minagic.STAR_SHARD.get(), level);

            this.speed = 1;
            this.isEntityPiercing = false;
            this.setPos(position.x, position.y, position.z);
            this.setDeltaMovement(direction.normalize().scale(this.speed));

        }

        @Override
        public void onHitBlock(BlockHitResult result){
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
        public @NotNull ItemStack getItem(){
            return new ItemStack(Items.PRISMARINE_CRYSTALS);
        }
    }
}
