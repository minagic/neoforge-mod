package com.minagic.minagic.sorcerer.voidbourne.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.SpellValidator;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.SpellUtils;
import com.minagic.minagic.utilities.VisualUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;

import java.util.List;
import java.util.Set;

public class KineticNullificationField extends AutonomousSpell {
    public KineticNullificationField() {
        this.spellName = "Kinetic Nullification Field";
        this.cooldown = 100;
        this.simulacraMaxLifetime = 300;
    }

    @Override
    public SpellValidator.CastFailureReason canCast(SpellCastContext context) {
        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getMainClass() != PlayerClassEnum.SORCERER) {
            return SpellValidator.CastFailureReason.CASTER_CLASS_MISMATCH;
        }

        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE) == 0) {
            return SpellValidator.CastFailureReason.CASTER_SUBCLASS_MISMATCH;
        }

        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE) < 3) {
            return SpellValidator.CastFailureReason.CASTER_CLASS_LEVEL_TOO_LOW;
        }
        return SpellValidator.CastFailureReason.OK;
    }


    @Override
    public void tick(SpellCastContext context, SimulacrumSpellData simulacrumData){
        System.out.println("KineticNullificationField tick");
        List<Projectile> targets = SpellUtils.findEntitiesInRadius(
                context.level(),
                context.target.position(),
                5.8,
                Projectile.class,
                e->true,
                Set.of()
        );
        for (Projectile projectile : targets) {
            System.out.println("Kinetic Nullification detected target: " + projectile);
            Minagic.ENTITY_FREEZER.freeze(projectile, (ServerLevel) context.level());
        }

        VisualUtils.createParticlesInSphere(
                (ServerLevel) context.level(),
                context.target.position(),
                5,
                ParticleTypes.SMOKE,
                150
        );

        drainMana(context, 1);
    }
}
