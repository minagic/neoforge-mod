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

/**
 * A simple self-managed spell that automatically attaches or detaches itself
 * as a background simulacrum when cast. Runs indefinitely (maxLifetime = -1)
 * until toggled again.
 */
public class AutonomousSpell extends Spell {

    @Override
    public void onStart(SpellCastContext context) {
        LivingEntity player = preCast(context, true, true, false);

        if (player == null) {
            return; // Pre-cast checks failed
        }

        // Get player simulacra attachment
        PlayerSimulacraAttachment sim = player.getData(ModAttachments.PLAYER_SIMULACRA.get());
        PlayerSpellCooldowns cooldowns = player.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS);

        // Toggle logic: if already active, remove; else add
        var existing = sim.getBackgroundSimulacra().get(ModSpells.getId(this));

        if (existing != null) {
            sim.removeSimulacrum(ModSpells.getId(this));
            cooldowns.setCooldown(ModSpells.getId(this), getCooldownTicks());
        } else {
            sim.addSimulacrum( this, getSimulacrumThreshold(), getMaxLifetime(), context.stack);
        }

        // Save attachment back (important for NeoForge data sync)
        player.setData(ModAttachments.PLAYER_SIMULACRA.get(), sim);
        player.setData(ModAttachments.PLAYER_SPELL_COOLDOWNS, cooldowns);
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
