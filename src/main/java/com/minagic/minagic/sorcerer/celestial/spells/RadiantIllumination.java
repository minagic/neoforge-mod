package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.ChargedSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.SpellUtils;
import com.minagic.minagic.utilities.VisualUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
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
    public void tick(SpellCastContext context) {
        super.tick(context);
        float progress = context.simulacrtumLifetime.progress();
        double radius =  progress > 0.8 ? 1 : progress/0.8;
        int density = 64;

        VisualUtils.spawnRadialParticleRing(context.level(), context.target.position(), radius*32, density, ParticleTypes.END_ROD);
    }

    @Override
    public CastFailureReason canCast(SpellCastContext context) {
        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getMainClass() != PlayerClassEnum.SORCERER) {
            return CastFailureReason.CASTER_CLASS_MISMATCH;
        }

        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_CELESTIAL) == 0) {
            return CastFailureReason.CASTER_SUBCLASS_MISMATCH;
        }

        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_CELESTIAL) < 5) {
            return CastFailureReason.CASTER_CLASS_LEVEL_TOO_LOW;
        }
        return CastFailureReason.OK;
    }


    @Override
    public void cast(SpellCastContext context) {
        // locate every entity within range
        float progress = context.simulacrtumLifetime.progress();
        double radius =  progress > 0.8 ? 1 : progress/0.8;

        List<LivingEntity> targets = SpellUtils.findEntitiesInRadius(
                context.level(),
                context.target.position(),
                radius*32,
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
            blinder.perform(SpellEventPhase.START, currentContext);
        }
    }



    public static class RadiantIlluminationBlinder extends AutonomousSpell {
        public RadiantIlluminationBlinder() {
            super();
            this.spellName = "Radiant Blinding";
            this.cooldown = 0;
            this.manaCost = 0;
            this.simulacraMaxLifetime = 250;
            this.simulacraThreshold = 1;
        }

        @Override
        // cast a VERY bright hyperdense particlespam around them
        public void cast(SpellCastContext context) {
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
                double yOffset = height;

                level.sendParticles(
                        ParticleTypes.END_ROD,
                        center.x + xOffset,
                        center.y + yOffset,
                        center.z + zOffset,
                        0, 0, 0, 0, 0
                );
            }
        }

        @Override
        protected boolean before(SpellEventPhase phase, SpellCastContext context){
            return switch (phase) {
                case CAST -> validateCaster(context) && validateItem(context);
                default -> true;
            };
        }

    }
}
