package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.ChargedSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.powersource.SorceryPowerSourceAttachment;
import com.minagic.minagic.registries.ModParticles;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.SpellUtils;
import com.minagic.minagic.utilities.VisualUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@AutoDetection.Spell
public class RadiantIllumination extends ChargedSpell implements SorceryPowerSourceAttachment.ISorcerySpell {

    public RadiantIllumination() {
        super(ChargedSpell.defaultProperties()
                .withManaCost(50)
                .withCooldown(200)
                .withSpellName("Radiant Illumination")
                .withIdName("radiant_illumination")
                .withSimulacraMaxLifetime(250)
                .withRequireSimulacrumOnCast(true));
    }

    @Override
    public void tick(SpellCastContext ctx, SimulacrumData simData) {
        super.tick(ctx, simData);
        float progress = Objects.requireNonNull(simData).progress();
        double radius = progress > 0.8 ? 1 : progress / 0.8;
        int density = 64;

        VisualUtils.spawnRadialParticleRing(ctx.level(), ctx.target.position(), radius * 32, density, ModParticles.CELEST_PARTICLES.get());


    }

    @Override
    public void cast(SpellCastContext ctx, @Nullable SimulacrumData simData) {
        // locate every entity within range
        float progress = Objects.requireNonNull(simData).progress();
        double radius = progress > 0.8 ? 1 : progress / 0.8;

        List<LivingEntity> targets = SpellUtils.findEntitiesInRadius(
                ctx.level(),
                ctx.target.position(),
                radius * 32,
                LivingEntity.class,
                e -> SpellUtils.hasTheoreticalLineOfSight(e, ctx.target),
                Set.of(ctx.target)
        );

        for (LivingEntity target : targets) {
            SpellCastContext currentContext = new SpellCastContext(
                    ctx.caster,
                    target
            );
            RadiantIlluminationBlinder blinder = new RadiantIlluminationBlinder();
            blinder.perform(SpellEventPhase.START, currentContext, null);
        }
    }


    @AutoDetection.Spell
    public static class RadiantIlluminationBlinder extends AutonomousSpell implements SorceryPowerSourceAttachment.ISorcerySpell {
        public RadiantIlluminationBlinder() {
            super(AutonomousSpell.defaultProperties()
                    .withSpellName("Radiant Blinding")
                    .withIdName("radiant_illumination_blinder")
                    .withCooldown(0)
                    .withManaCost(0)
                    .withSimulacraMaxLifetime(250)
                    .withSimulacraThreshold(1)
                    .withTechnical(true));
        }

        @Override
        // cast a VERY bright hyperdense particlespam around them
        public void cast(SpellCastContext ctx, SimulacrumData simData) {
            // locate every entity within range
            LivingEntity target = ctx.target;

            ServerLevel level = (ServerLevel) ctx.level();
            Vec3 center = target.position().add(0, target.getBbHeight() / 2.0, 0);

            double radius = 1.5;
            int particles = 100;

            for (int i = 0; i < particles; i++) {
                double angle = level.random.nextDouble() * 2 * Math.PI;
                double distance = level.random.nextDouble() * radius;
                double height = level.random.nextDouble() * target.getBbHeight();

                double xOffset = Math.cos(angle) * distance;
                double zOffset = Math.sin(angle) * distance;

                level.sendParticles(
                        ParticleTypes.END_ROD,
                        center.x + xOffset,
                        center.y + height,
                        center.z + zOffset,
                        0, 0, 0, 0, 0
                );
            }
        }
        @Override
        public String getRequiredBloodline() {
            return SorceryPowerSourceAttachment.BLOODLINE_CELESTIAL;
        }

        @Override
        public int getRequiredAffinityLevel() {
            return 5;
        }


    }

    @Override
    public String getRequiredBloodline() {
        return SorceryPowerSourceAttachment.BLOODLINE_CELESTIAL;
    }

    @Override
    public int getRequiredAffinityLevel() {
        return 5;
    }
}
