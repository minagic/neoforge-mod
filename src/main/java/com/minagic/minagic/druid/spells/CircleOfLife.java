package com.minagic.minagic.druid.spells;

import com.minagic.minagic.abstractionLayer.spells.AutonomousSpell;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

public class CircleOfLife extends AutonomousSpell {
    public CircleOfLife() {
        this.spellName = "Circle Of Life";
        this.cooldown = 200;
        this.simulacraMaxLifetime = 2000;
        this.manaCost = 5;
        this.simulacraThreshold = 10;
    }


    @Override
    public void cast(SpellCastContext context) {
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
                    context.stack,
                    animal
            );

            PlayerSimulacraAttachment.addSimulacrum(
                    subCtx,
                    oath,
                    oath.getSimulacrumThreshold(),
                    oath.getMaxLifetime()
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

    public CastFailureReason canCast(SpellCastContext context) {
        // check for DRUID, CIRCLE OF ANIMALS subclass level 5+
        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getMainClass() != PlayerClassEnum.DRUID) {
            return CastFailureReason.CASTER_CLASS_MISMATCH;
        }

        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.DRUID_ANIMALS) == 0) {
            return CastFailureReason.CASTER_SUBCLASS_MISMATCH;
        }

        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.DRUID_ANIMALS) < 5) {
            return CastFailureReason.CASTER_CLASS_LEVEL_TOO_LOW;
        }
        return CastFailureReason.OK;
    }


}
