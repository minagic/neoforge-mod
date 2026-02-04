package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerClassEnum;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import net.minecraft.world.entity.LivingEntity;

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
                            Minagic.LOGGER.trace("Gravitational Suspension tick for {}", ctx.target.getName().getString());
                            ctx.target.setNoGravity(true);
                            ctx.target.hurtMarked = true;
                        }
                )
                .execute(context, simulacrumData);
    }

    @Override
    public void start(SpellCastContext context, SimulacrumData simulacrumData) {
        super.start(context, simulacrumData);
        if(SimulacraAttachment.hasSpell(context.target, ModSpells.getId(this))) {
            context.target.setNoGravity(true);
            context.target.hurtMarked = true;
        }
    }

    @Override
    public void exitSimulacrum(SpellCastContext context, SimulacrumData simulacrumData) {
        Minagic.LOGGER.debug("Gravitational Suspension ended on {} (side: {})",
                context.target.getName().getString(),
                context.level().isClientSide() ? "CLIENT" : "SERVER");
        Minagic.LOGGER.trace("Before cleanup: noGravity={}", context.target.isNoGravity());
        LivingEntity e = context.target;
        e.setNoGravity(false);
        e.hurtMarked = true;

        e.setDeltaMovement(e.getDeltaMovement().multiply(1, 1, 1));
        e.fallDistance = 0;
        e.hurtMarked = true;
        Minagic.LOGGER.trace("After cleanup: noGravity={}", context.target.isNoGravity());
    }

    public List<DefaultGates.ClassGate.MagicClassEntry> getAllowedClasses(){
        return List.of(new DefaultGates.ClassGate.MagicClassEntry(PlayerClassEnum.SORCERER, PlayerSubClassEnum.SORCERER_CELESTIAL, 8));
    }
}
