package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.DamageTypes;
import com.minagic.minagic.Minagic;
import com.minagic.minagic.MinagicDamage;
import com.minagic.minagic.api.spells.ISimulacrumSpell;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerClassEnum;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.*;
import com.minagic.minagic.registries.ModParticles;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGateChain;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import com.minagic.minagic.utilities.MathUtils;
import com.minagic.minagic.utilities.SpellUtils;
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
        manaCost = 1;
    }

    // lifecycle

    @Override
    public final void start(SpellCastContext ctx, @Nullable SimulacrumData simData) {
        Minagic.LOGGER.debug("Banishment spell start invoked");
        new SpellGateChain().addGate(new DefaultGates.ClassGate(this.getAllowedClasses())).setEffect(
                (context, simulacrumData) -> {
                    if (!SpellMetadata.has(context.target, this, "bb_start")) {
                        Minagic.LOGGER.debug("Banishment precheck: no metadata, initializing area");
                        SpellMetadata.setBlockPos(context.target, this, "bb_start", context.target.blockPosition());
                        SimulacraAttachment.addSimulacrum(context.target, context, this, -1, 200);
                    }

                    else if (!SpellMetadata.has(context.target, this, "bb_end")) {
                        Minagic.LOGGER.debug("Banishment precheck: partial metadata detected");
                        BlockPos pos = context.target.blockPosition();
                        int manaCost = (int) MathUtils.areaBetween(SpellMetadata.getBlockPos(context.target, this, "bb_start"), pos);

                        new SpellGateChain()
                                .addGate(new DefaultGates.ManaGate(manaCost, this))
                                .setEffect(
                                        (internal_ctx, data) -> {
                                            SpellMetadata.setBlockPos(internal_ctx.target, this, "bb_end", pos);
                                            SimulacraAttachment.addSimulacrum(internal_ctx.target, internal_ctx, this, 1, -1);
                                        }
                                )
                                .execute(context, simulacrumData);

                    }
                    else {
                        Minagic.LOGGER.debug("Banishment precheck: full metadata detected, cancelling spell");
                        SimulacraAttachment.removeSimulacrum(context.target, ModSpells.getId(this));
                        SpellMetadata.removeBlockPos(context.target, this, "bb_start");
                        SpellMetadata.removeBlockPos(context.target, this, "bb_end");
                    }

                }
        ).execute(ctx, simData);


    }

    public final void tick(SpellCastContext context, SimulacrumData simulacrumData) {
    }

    public final void stop(SpellCastContext context, SimulacrumData simulacrumData) {
    }

    public final void exitSimulacrum(SpellCastContext context, SimulacrumData simulacrumData) {
        SpellMetadata.removeBlockPos(context.target, this, "bb_start");
        SpellMetadata.removeBlockPos(context.target, this, "bb_end");
    }

    @Override
    public final void cast(SpellCastContext ctx, @Nullable SimulacrumData simData) {
        SpellGatePolicyGenerator.build(SpellEventPhase.CAST, this.getAllowedClasses(), null, manaCost, null, false, this)
                .addGate(new DefaultGates.MetadataGate(this, List.of("bb_start", "bb_end"), true))
                .setEffect((context, simulacrumData) -> {
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
                        double ty = SpellUtils.findSurfaceY(level, tx, tz); // Get the highest point at (tx, tz)

                        // Beam visual descending from above
                        for (int step = 0; step < 16; step++) {
                            double y = ty + 16 - step;
                            level.sendParticles(ModParticles.CELEST_PARTICLES.get(), tx, y, tz, 1, 0, 0, 0, 0.0);
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
                })
                .execute(ctx, simData);

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

    @Override
    public List<DefaultGates.ClassGate.MagicClassEntry> getAllowedClasses() {
        return List.of(new DefaultGates.ClassGate.MagicClassEntry[]{new DefaultGates.ClassGate.MagicClassEntry(PlayerClassEnum.SORCERER, PlayerSubClassEnum.SORCERER_CELESTIAL, 10)});
    }
}
