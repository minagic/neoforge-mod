package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGateChain;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class GravitationalSuspension extends AutonomousSpell {
    public GravitationalSuspension(){
        this.manaCost = 3;
        this.spellName = "Gravitational Suspension";
        this.simulacraMaxLifetime = -1;
        this.simulacraThreshold = 0;
    }

    @Override
    public void tick(SpellCastContext context, SimulacrumData simulacrumData) {
        SpellGatePolicyGenerator.build(SpellEventPhase.TICK, this.getAllowedClasses(), 0, 0, manaCost, false, this)
                .setEffect(
                        (ctx, simData) -> {
                            System.out.println("FGravitation ticked");
                            ctx.target.setNoGravity(true);
                            ctx.target.hurtMarked = true;
                        }
                )
                .execute(context, simulacrumData);
    }

    @Override
    public void start(SpellCastContext context, SimulacrumData simulacrumData) {
        super.start(context, simulacrumData);
        SimulacraAttachment sim = context.caster.getData(ModAttachments.PLAYER_SIMULACRA);
        if(sim.hasSpell(ModSpells.getId(this))) {
            context.target.setNoGravity(true);
            context.target.hurtMarked = true;
        }
    }

    @Override
    public void exitSimulacrum(SpellCastContext context, SimulacrumData simulacrumData) {
        System.out.println("Gravitational Suspension ended... side=" + (context.level().isClientSide() ? "CLIENT" : "SERVER"));
        System.out.println("Before: noGravity=" + context.target.isNoGravity());
        LivingEntity e = context.target;
        e.setNoGravity(false);
        e.hurtMarked = true;

        e.setDeltaMovement(e.getDeltaMovement().multiply(1, 1, 1));
        e.fallDistance = 0;
        e.hurtMarked = true;
        System.out.println("After: noGravity=" + context.target.isNoGravity());
    }

    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses(){
        return List.of(new DefaultGates.ClassGate.AllowedClass(PlayerClassEnum.SORCERER, PlayerSubClassEnum.SORCERER_CELESTIAL, 8));
    }
}
