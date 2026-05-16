package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.ChanneledAutonomousSpell;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.powersource.SorceryPowerSourceAttachment;
import com.minagic.minagic.common.events.custom.StatCollectEvent;
import com.minagic.minagic.scaling.DefaultActions;
import com.minagic.minagic.scaling.DefaultStats;
import com.minagic.minagic.scaling.SpellStatCollector;
import com.minagic.minagic.sorcerer.celestial.mechanics.StarShard;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.MathUtils;
import com.minagic.minagic.utilities.SpellUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

@AutoDetection.Spell
public class CelestialBombardment extends ChanneledAutonomousSpell implements SorceryPowerSourceAttachment.ISorcerySpell {

    public CelestialBombardment() {
        super(ChanneledAutonomousSpell.defaultProperties()
                .withCooldown(400)
                .withSpellName("Celestial Bombardment")
                .withIdName("celestial_bombardment")
                .withSimulacraMaxLifetime(200)
                .withSimulacraThreshold(5)
                .withManaCost(5));
    }

    static Vec3[] computeFiringSolution(Vec3 sourcePos, Vec3 mainTargetPos, Vec3 targetPos, double altitude, double angleDeg) {
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
        SpellStatCollector collector = new SpellStatCollector();
        collector.injectContext(this, ctx, simData);
        collector.beginCollection();

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
            StarShard shard = new StarShard(ctx.level(), pos_dir[0], pos_dir[1], collector.getStat(DefaultStats.Spell.AOE_RADIUS).intValue());
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

    @Override
    public <T extends StatCollectEvent> void contributeTo(T event){
        event.contribute(DefaultStats.Spell.AOE_RADIUS, new DefaultActions.OVERRIDE<Float>(), 4f, -1, "Default value from "+this.getString());
    }
}
