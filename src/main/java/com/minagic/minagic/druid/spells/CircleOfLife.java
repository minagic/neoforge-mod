package com.minagic.minagic.druid.spells;

import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CircleOfLife extends AutonomousSpell {
    public CircleOfLife() {
        this.spellName = "Circle Of Life";
        this.cooldown = 200;
        this.simulacraMaxLifetime = 2000;
        this.manaCost = 5;
        this.simulacraThreshold = 10;
    }


    @Override
    public void cast(SpellCastContext ctx, @Nullable SimulacrumData simData) {
        SpellGatePolicyGenerator.build(SpellEventPhase.CAST, this.getAllowedClasses(), null, manaCost, null, false, this)
                .setEffect((context, simulacrumData) -> {
                    OathOfLife oath = new OathOfLife();

                    var entities = context.level().getEntitiesOfClass(
                            Animal.class,
                            context.target.getBoundingBox().inflate(5.0)
                    );

                    for (Animal animal : entities) {
                        if (animal == context.caster) continue;
                        if (!animal.isAlive()) continue;

                        // Spawn tiny “life” particle burst
                        spawnLifeParticle(animal);

                        // Build a *fresh* context for each target
                        SpellCastContext subCtx = new SpellCastContext(
                                context.caster,
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
                })
                .execute(ctx, simData);

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

    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses() {
        return List.of(new DefaultGates.ClassGate.AllowedClass(
                PlayerClassEnum.DRUID,
                PlayerSubClassEnum.DRUID_ANIMALS,
                5
        ));
    }


}
