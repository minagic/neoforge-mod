package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.ChargedSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.registries.ModParticles;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
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

public class RadiantIllumination extends ChargedSpell {

    public RadiantIllumination() {
        super();
        this.manaCost = 50;
        this.cooldown = 200;
        this.spellName = "Radiant Illumination";
        this.simulacraMaxLifetime = 250;
    }

    @Override
    public void tick(SpellCastContext ctx, SimulacrumData simData) {
        SpellGatePolicyGenerator.build(SpellEventPhase.TICK, this.getAllowedClasses(), cooldown, 0, 0, false, this).setEffect(
                (context, simulacrumData) -> {
                    super.tick(context, simulacrumData);
                    float progress = Objects.requireNonNull(simulacrumData).progress();
                    double radius = progress > 0.8 ? 1 : progress / 0.8;
                    int density = 64;

                    VisualUtils.spawnRadialParticleRing(context.level(), context.target.position(), radius * 32, density, ModParticles.CELEST_PARTICLES.get());
                }
        ).execute(ctx, simData);


    }

    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses() {
        return List.of(new DefaultGates.ClassGate.AllowedClass(
                PlayerClassEnum.SORCERER,
                PlayerSubClassEnum.SORCERER_CELESTIAL,
                5
        ));
    }


    @Override
    public void cast(SpellCastContext ctx, @Nullable SimulacrumData simData) {
        SpellGatePolicyGenerator.build(SpellEventPhase.CAST, this.getAllowedClasses(), null, manaCost, null, true, this)
                .setEffect((context, simulacrumData) -> {
                    // locate every entity within range
                    float progress = Objects.requireNonNull(simulacrumData).progress();
                    double radius = progress > 0.8 ? 1 : progress / 0.8;

                    List<LivingEntity> targets = SpellUtils.findEntitiesInRadius(
                            context.level(),
                            context.target.position(),
                            radius * 32,
                            LivingEntity.class,
                            e -> SpellUtils.hasTheoreticalLineOfSight(e, context.target),
                            Set.of(context.target)
                    );

                    for (LivingEntity target : targets) {
                        SpellCastContext currentContext = new SpellCastContext(
                                context.caster,
                                target
                        );
                        RadiantIlluminationBlinder blinder = new RadiantIlluminationBlinder();
                        blinder.perform(SpellEventPhase.START, currentContext, null);
                    }
                })
                .execute(ctx, simData);
    }


    public static class RadiantIlluminationBlinder extends AutonomousSpell {
        public RadiantIlluminationBlinder() {
            super();
            this.spellName = "Radiant Blinding";
            this.cooldown = 0;
            this.manaCost = 0;
            this.simulacraMaxLifetime = 250;
            this.simulacraThreshold = 1;
            this.isTechnical = true;
        }

        @Override
        // cast a VERY bright hyperdense particlespam around them
        public void cast(SpellCastContext ctx, SimulacrumData simData) {
            SpellGatePolicyGenerator.build(SpellEventPhase.CAST, this.getAllowedClasses(), null, manaCost, null, false, this)
                    .setEffect((context, simulacrumData) -> {
                        // locate every entity within range
                        LivingEntity target = context.target;

                        ServerLevel level = (ServerLevel) context.level();
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
                    })
                    .execute(ctx, simData);
        }


    }
}
