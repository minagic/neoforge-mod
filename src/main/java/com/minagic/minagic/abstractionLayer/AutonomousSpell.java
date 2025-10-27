package com.minagic.minagic.abstractionLayer;

import com.minagic.minagic.abstractionLayer.Spell;
import com.minagic.minagic.capabilities.Mana;
import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

/**
 * A simple self-managed spell that automatically attaches or detaches itself
 * as a background simulacrum when cast. Runs indefinitely (maxLifetime = -1)
 * until toggled again.
 */
public class AutonomousSpell extends Spell {
    public int getMaxLifetime() {
        return -1; // Infinite lifetime
    }

    @Override
    public void onStart(SpellCastContext context) {
        ServerPlayer player = preCast(context, false);

        if (player == null) {
            return; // Pre-cast checks failed
        }

        // Get player simulacra attachment
        PlayerSimulacraAttachment sim = player.getData(ModAttachments.PLAYER_SIMULACRA.get());

        // Toggle logic: if already active, remove; else add
        var existing = sim.getBackgroundSimulacra().get(ModSpells.getId(this));

        if (existing != null) {
            sim.removeSimulacrum(ModSpells.getId(this));
            player.sendSystemMessage(Component.literal("§eDeactivated autonomous spell: §r" + getString()));
        } else {
            sim.addSimulacrum( this, getSimulacrumThreshold(), getMaxLifetime(), context.stack);
            player.sendSystemMessage(Component.literal("§bActivated autonomous spell: §r" + getString()));
        }

        // Save attachment back (important for NeoForge data sync)
        player.setData(ModAttachments.PLAYER_SIMULACRA.get(), sim);
    }

    @Override
    public String magicPrerequisitesHelper(SpellCastContext context) {
        // only check mana
        Mana mana = context.caster.getData(ModAttachments.MANA.get());
        if (mana.getMana() < getManaCost()) {
            return "Not enough mana to sustain " + getString() + ".";
        } else {
            return "";
        }
    }

    public void applyMagicCosts(SpellCastContext context) {
        // only drain mana
        var mana = context.caster.getData(ModAttachments.MANA.get());
        mana.drainMana(getManaCost());
    }
}
