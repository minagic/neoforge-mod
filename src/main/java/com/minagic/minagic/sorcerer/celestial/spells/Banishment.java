package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.DamageTypes;
import com.minagic.minagic.MinagicDamage;
import com.minagic.minagic.api.spells.ISimulacrumSpell;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.api.spells.SpellValidator;
import com.minagic.minagic.capabilities.*;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.MathUtils;
import com.minagic.minagic.utilities.SpellUtils;
import com.minagic.minagic.utilities.SpellValidationResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

// this will extend raw spell as this is an easier approach
public class Banishment extends Spell implements ISimulacrumSpell {
    public Banishment() {
        spellName = "Banishment";
        cooldown = 20;
    }

    // lifecycle
    @Override
    protected SpellValidationResult before(SpellEventPhase phase, SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        SpellValidationResult result = SpellValidationResult.OK;

        switch (phase) {
            case START -> {
                result = result
                        .and(SpellValidator.validateCaster(this, context))
                        .and(SpellValidator.validateCooldown(this, context))
                        .and(SpellValidator.validateItem(this, context));
            }
            case CAST -> {
                result = result
                        .and(SpellValidator.validateCaster(this, context))
                        .and(SpellValidator.validateCooldown(this, context))
                        .and(SpellValidator.validateItem(this, context))
                        .and(SpellValidator.validateMetadata(this, context, List.of("bb_start", "bb_end")));
            }
            case EXIT_SIMULACRUM -> {
                result = result
                        .and(SpellValidator.validateCaster(this, context))
                        .and(SpellValidator.validateItem(this, context));
            }
            case TICK, STOP -> {
                result = result.and(SpellValidationResult.INVALID_PHASE);
            }
        }

        return result;
    }

    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses() {
        return List.of(new DefaultGates.ClassGate.AllowedClass(
                PlayerClassEnum.SORCERER,
                PlayerSubClassEnum.SORCERER_CELESTIAL,
                10
        ));
    }


    @Override
    public final void start(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        if (!SpellMetadata.has(context.target, this, "bb_start")){
            SpellMetadata.setBlockPos(context.target, this, "bb_start", context.target.blockPosition());
            SimulacraAttachment.addSimulacrum(context.target, context, this, -1, 200);
            return;
        }

        if (!SpellMetadata.has(context.target, this, "bb_end")){

            BlockPos pos = context.target.blockPosition();
            int manaCost = (int) MathUtils.areaBetween(SpellMetadata.getBlockPos(context.target, this, "bb_start"), pos);
            SpellValidationResult result = SpellValidator.validateMana(this, context, manaCost);
            SpellValidator.showFailureIfNeeded(context, result);

            if (!result.success()) return;

            SpellMetadata.setBlockPos(context.target, this, "bb_end", pos);
            SimulacraAttachment.addSimulacrum(context.target, context, this, 1, -1);
            drainMana(context, manaCost);
            return;
        }

        SimulacraAttachment.removeSimulacrum(context.target, ModSpells.getId(this));

    }

    public final void tick(SpellCastContext context, SimulacrumData simulacrumData) {}
    public final void stop(SpellCastContext context, SimulacrumData simulacrumData) {}
    public final void exitSimulacrum(SpellCastContext context, SimulacrumData simulacrumData) {}

    @Override
    public final void cast(SpellCastContext context, SimulacrumData simulacrumData) {
        ServerLevel level = (ServerLevel) context.level();
        BlockPos start = SpellMetadata.getBlockPos(context.target, this, "bb_start");
        BlockPos end = SpellMetadata.getBlockPos(context.target, this, "bb_end");

        // Calculate the AABB corners
        int minX = Math.min(start.getX(), end.getX());
        int maxX = Math.max(start.getX(), end.getX());
        int minY = Math.min(start.getY(), end.getY());
        int maxY = Math.max(start.getY(), end.getY());
        int minZ = Math.min(start.getZ(), end.getZ());
        int maxZ = Math.max(start.getZ(), end.getZ());

        // Visual: draw vertical light columns at corners of the bounding box
        for (int x : new int[]{minX, maxX}) {
            for (int z : new int[]{minZ, maxZ}) {
                for (int y = minY; y <= maxY; y += 4) {
                    level.sendParticles(ParticleTypes.END_ROD, x + 0.5, y + 0.5, z + 0.5, 2, 0, 0, 0, 0.01);
                }
            }
        }

        // Simulate orbital beams hitting inside area at random
        int beamCount = 300;
        for (int i = 0; i < beamCount; i++) {
            double tx = minX + level.getRandom().nextDouble() * (maxX - minX);
            double tz = minZ + level.getRandom().nextDouble() * (maxZ - minZ);
            double ty = SpellUtils.findSurfaceY(level, tx, tz); // Get highest point at (tx, tz)

            // Beam visual descending from above
            for (int step = 0; step < 16; step++) {
                double y = ty + 16 - step;
                level.sendParticles(ParticleTypes.GLOW, tx, y, tz, 1, 0, 0, 0, 0.0);
            }

            // Impact visuals
            level.sendParticles(ParticleTypes.EXPLOSION, tx, ty + 1, tz, 3, 0.1, 0.1, 0.1, 0.05);
            level.sendParticles(ParticleTypes.FLAME, tx, ty + 1, tz, 12, 0.3, 0.3, 0.3, 0.01);
        }

        // TARGETING INFORMATION
        List<LivingEntity> targets = SpellUtils.getEntitiesInXZColumnBox(context.level(), start, end, LivingEntity.class, SpellUtils::canSeeSky);

        for (LivingEntity target : targets) {

            MinagicDamage damage = new MinagicDamage(
                    context.target,
                    target,
                    context.target,
                    3,
                    Set.of(
                            DamageTypes.MAGIC,
                            DamageTypes.RADIANT
                    )
            );
            damage.hurt(level);
        }


    }

    @Override
    protected void after(SpellEventPhase phase, SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        if (phase == SpellEventPhase.EXIT_SIMULACRUM) {
            SpellMetadata.removeBlockPos(context.target, this, "bb_end");
            SpellMetadata.removeBlockPos(context.target, this, "bb_start");
            applyCooldown(context, cooldown);
        }
    }


    @Override
    public int getSimulacrumThreshold() {
        return 0;
    }

    @Override
    public int getSimulacrumMaxLifetime() {
        return 0;
    }

    @Override
    public final float progress(SimulacrumData data) {
        if (data.maxLifetime() <= 0) {
            return 1f;
        }
        return data.remainingLifetime() / data.maxLifetime();
    }

    @Override
    public final int color(float progress) {
        return 0xFFAAFFAA;
    }
}
