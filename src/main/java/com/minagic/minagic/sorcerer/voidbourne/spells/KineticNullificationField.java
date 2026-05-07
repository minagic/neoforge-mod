package com.minagic.minagic.sorcerer.voidbourne.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.powersource.SorceryPowerSourceAttachment;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.SpellUtils;
import com.minagic.minagic.utilities.VisualUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;

import java.util.List;
import java.util.Set;

import static com.minagic.minagic.common.events.CommonEventRegistration.ENTITY_FREEZER;

@AutoDetection.Spell
public class KineticNullificationField extends AutonomousSpell implements SorceryPowerSourceAttachment.ISorcerySpell {
    public KineticNullificationField() {
        super(AutonomousSpell.defaultProperties()
                .withSpellName("Kinetic Nullification Field")
                .withIdName("kinetic_nullification_field")
                .withCooldown(100)
                .withSimulacraMaxLifetime(300)
                .withManaCost(1)
                .withSustainCost(1));
    }

    @Override
    public void tick(SpellCastContext ctx, SimulacrumData simData) {
        List<Projectile> targets = SpellUtils.findEntitiesInRadius(
                ctx.level(),
                ctx.target.position(),
                5.8,
                Projectile.class,
                e -> true,
                Set.of()
        );
        for (Projectile projectile : targets) {
            Minagic.LOGGER.debug("Kinetic Nullification detected target {}", projectile);
            ENTITY_FREEZER.freeze(projectile, (ServerLevel) ctx.level());
        }

        VisualUtils.createParticlesInSphere(
                (ServerLevel) ctx.level(),
                ctx.target.position(),
                5,
                ParticleTypes.SMOKE,
                150
        );

    }

    @Override
    public String getRequiredBloodline() {
        return SorceryPowerSourceAttachment.BLOODLINE_VOIDBOURNE;
    }

    @Override
    public int getRequiredAffinityLevel() {
        return 3;
    }
}
