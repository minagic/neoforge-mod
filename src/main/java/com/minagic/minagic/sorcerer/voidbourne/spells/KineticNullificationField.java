package com.minagic.minagic.sorcerer.voidbourne.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.sorcerer.spells.VoidBlastEntity;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import com.minagic.minagic.utilities.SpellUtils;
import com.minagic.minagic.utilities.VisualUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;

public class KineticNullificationField extends AutonomousSpell {
    public KineticNullificationField() {
        this.spellName = "Kinetic Nullification Field";
        this.cooldown = 100;
        this.simulacraMaxLifetime = 300;
        this.manaCost = 1;
    }

    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses() {
        return List.of(new DefaultGates.ClassGate.AllowedClass(
                PlayerClassEnum.SORCERER,
                PlayerSubClassEnum.SORCERER_VOIDBOURNE,
                3
        ));
    }


    @Override
    public void tick(SpellCastContext ctx, SimulacrumData simData){
        SpellGatePolicyGenerator.build(SpellEventPhase.TICK, this.getAllowedClasses(), null, null, manaCost, false, this)
                .setEffect((context, simulacrumData) -> {
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
                })
                .execute(ctx, simData);

    }
}
