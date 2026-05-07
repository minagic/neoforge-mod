package com.minagic.minagic.druid.spells;

import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@AutoDetection.Spell
public class CircleOfLife extends AutonomousSpell {

    public CircleOfLife() {
        super(AutonomousSpell.defaultProperties()
                .withSpellName("Circle Of Life")
                .withIdName("circle_of_life")
                .withCooldown(200)
                .withSimulacraMaxLifetime(2000)
                .withManaCost(5)
                .withSimulacraThreshold(10));
    }


    @Override
    public void cast(SpellCastContext ctx, @Nullable SimulacrumData simData) {
        OathOfLife oath = new OathOfLife();

        var entities = ctx.level().getEntitiesOfClass(
                Animal.class,
                ctx.target.getBoundingBox().inflate(5.0)
        );

        for (Animal animal : entities) {
            if (animal == ctx.caster) continue;
            if (!animal.isAlive()) continue;

            // Spawn tiny "life" particle burst
            spawnLifeParticle(animal);

            // Build a *fresh* context for each target
            SpellCastContext subCtx = new SpellCastContext(
                    ctx.caster,
                    animal
            );

            SimulacraAttachment.addSimulacrum(
                    subCtx.target,
                    subCtx,
                    oath,
                    oath.getSimulacrumThreshold(),
                    oath.getSimulacrumMaxLifetime()
            );
        }

    }

    private void spawnLifeParticle(LivingEntity entity) {
        Level level = entity.level();
        if (level instanceof ServerLevel server) {
            server.sendParticles(
                    ParticleTypes.HEART,       // particle type
                    entity.getX(),
                    entity.getY() + entity.getBbHeight() * 0.5,
                    entity.getZ(),
                    3,                         // count
                    0.2, 0.2, 0.2,             // spread
                    0.01                       // speed
            );
        }
    }
}
