package com.minagic.minagic.spellCasting;

import com.minagic.minagic.spells.ISpell;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Pre;;

public class SpellSlot {
    ISpell spell;
    public int cooldownRemaining = 0;

    // Update cooldown each tick
    public void tickCooldown () {
        if (cooldownRemaining > 0) {
            cooldownRemaining--;
        }
    }

    public void setSpell(ISpell spell) {
        this.spell = spell;
    }

    public ISpell getSpell() {
        return spell;
    }

    public void cast(SpellCastContext context){
        if (!(context.caster instanceof ServerPlayer player)) return;
        if (cooldownRemaining <= 0 && spell != null) {
            boolean success = spell.cast(context);
            if (success) cooldownRemaining = spell.getCooldownTicks();
        }
        else if (spell == null) {
            player.sendSystemMessage(Component.literal("No spell assigned to this slot."));
        }
        else if (cooldownRemaining > 0) {
            player.sendSystemMessage(Component.literal("Spell slot is on cooldown for " + (cooldownRemaining / 20 + 1) + " more seconds."));
        }
    }

    public String getEnterPhrase() {
        if (spell != null) {
            return "Spell: " + spell.getClass().getSimpleName();
        } else {
            return "No spell assigned.";
        }
    }

}
