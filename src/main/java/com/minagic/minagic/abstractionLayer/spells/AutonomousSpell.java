package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.capabilities.Mana;
import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.capabilities.PlayerSpellCooldowns;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import static com.ibm.icu.text.PluralRules.Operand.c;

/**
 * A simple self-managed spell that automatically attaches or detaches itself
 * as a background simulacrum when cast. Runs indefinitely (maxLifetime = -1)
 * until toggled again.
 */
public class AutonomousSpell extends Spell {

    @Override
    public LivingEntity preCast(SpellCastContext context) {return checkContext(context, true, true, getManaCost(), true);}

    @Override
    public LivingEntity preExitSimulacrum(SpellCastContext context) {return checkContext(context, true, false, 0, false);}

    @Override
    public LivingEntity preTick(SpellCastContext context) {return checkContext(context, true, false, 0, false);}

    @Override
    public LivingEntity preStart(SpellCastContext context) {return checkContext(context, true, true, 0, true);}

    @Override
    public LivingEntity preStop(SpellCastContext context) {return checkContext(context, true, false, 0, false); }


    @Override
    public final void postCast(SpellCastContext context) {
        applyMagicCosts(context, 0, getManaCost());
    }

    @Override
    public final void postExitSimulacrum(SpellCastContext context) {
        applyMagicCosts(context, getCooldownTicks(), 0);
    }

    @Override
    public final void postTick(SpellCastContext context) {}


    @Override
    public void onStart(SpellCastContext context) {
        LivingEntity player = preStart(context);
        if (player == null) {
            return; // Pre-cast checks failed
        }

        // Get player simulacra attachment
        PlayerSimulacraAttachment sim = player.getData(ModAttachments.PLAYER_SIMULACRA.get());

        // Toggle logic: if already active, remove; else add
        var existing = sim.getBackgroundSimulacra().get(ModSpells.getId(this));

        if (existing != null) {
            PlayerSimulacraAttachment.removeSimulacrum(context.caster, context.level, ModSpells.getId(this));
        } else {
            PlayerSimulacraAttachment.addSimulacrum( context.caster, context.level, this, getSimulacrumThreshold(), getMaxLifetime(), context.stack);
        }



    }

    @Override
    public final void tick(SpellCastContext context) {
        // No-op for autonomous spells
    }

    @Override
    public final void onStop(SpellCastContext context) {
        // No-op for autonomous spells
    }

}
