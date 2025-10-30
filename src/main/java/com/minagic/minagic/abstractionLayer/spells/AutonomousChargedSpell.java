package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.world.entity.LivingEntity;

//// An abstract class representing spells that are charged up over time before being released.
public class AutonomousChargedSpell extends Spell {
    @Override
    public final int getMaxLifetime(){
        return getSimulacrumThreshold();
        // lifetime equals threshold for autonomous charged spells.
        // This will exit as soon as threshold is reached.
    }

    @Override
    public int getCooldownTicks(){
        return 10; // default cooldown
    }

    @Override
    public String getString() {
        return "Autonomous Charged Spell";
    }

    @Override
    public int getManaCost() {
        return 15; // default mana cost
    }

    // lifecycle methods
    @Override
    public final void onStart(SpellCastContext context) {
        LivingEntity player = preCast(context, false);

        if (player == null) {
            return; // Pre-cast checks failed
        }

        // Get player simulacra attachment
        PlayerSimulacraAttachment sim = player.getData(ModAttachments.PLAYER_SIMULACRA.get());

        // Toggle logic: if already active, remove; else add
        var existing = sim.getBackgroundSimulacra().get(ModSpells.getId(this));

        if (existing != null) {
            sim.removeSimulacrum(ModSpells.getId(this));
        } else {
            sim.addSimulacrum( this, getSimulacrumThreshold(), getMaxLifetime(), context.stack);
        }

        // Save attachment back (important for NeoForge data sync)
        player.setData(ModAttachments.PLAYER_SIMULACRA.get(), sim);
    }

    @Override
    public  final void tick(SpellCastContext context) {
        // No-op for autonomous charged spells
    }

    @Override
    public final void onStop(SpellCastContext context) {
        // No-op for autonomous charged spells
    }
}
