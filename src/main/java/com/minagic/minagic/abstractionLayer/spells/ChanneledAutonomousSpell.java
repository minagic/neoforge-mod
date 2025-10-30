package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.capabilities.Mana;
import com.minagic.minagic.capabilities.PlayerSpellCooldowns;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class ChanneledAutonomousSpell extends Spell {
    @Override
    public final int getMaxLifetime() {
        return -1; // Channeled spells have no max lifetime
    }

    @Override
    public final int getSimulacrumThreshold() {
        return 0; // Channeled autonomous cannot be autocast by simulacra
    }

    @Override
    public String getString() {
        return "Channeled Autonomous Spell";
    }

    @Override
    public int getManaCost() {
        return 20; // default mana cost
    }

    @Override
    public int getCooldownTicks() {
        return 3; // default cooldown
    }

    // lifecycle like of channelled spell

    @Override
    public final void onStart(SpellCastContext context) {
        LivingEntity player = preCast(context, true);
        if (player == null) {
            return; // Pre-cast checks failed
        }

        var data = player.getData(ModAttachments.PLAYER_SIMULACRA.get());
        if (data.getActiveChanneling()!=null && ModSpells.getId(data.getActiveChanneling().getSpell()) != ModSpells.getId(this)) {
            data.setActiveChanneling(this, getSimulacrumThreshold(), -1, context.stack);
        }
        else if (data.getActiveChanneling()==null) {
            data.setActiveChanneling(this, getSimulacrumThreshold(), -1, context.stack);
        }
        player.setData(ModAttachments.PLAYER_SIMULACRA.get(), data);
    }

    @Override
    public final void tick(SpellCastContext context) {
        if (context.simulacrtumLifetime==-1) {
            throw new IllegalStateException("Channeled autonomous spell tick can only be called from simulacra");
        }
        cast(context);
    }

    @Override
    public final void onStop(SpellCastContext context) {
        LivingEntity player = preCast(context, false);
        if (player == null) {
            return; // Pre-cast checks failed
        }

        PlayerSpellCooldowns cooldowns = player.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS);
        var data = player.getData(ModAttachments.PLAYER_SIMULACRA);

        data.clearChanneling();
        cooldowns.setCooldown(ModSpells.getId(this), getCooldownTicks());

        player.setData(ModAttachments.PLAYER_SIMULACRA, data);
        player.setData(ModAttachments.PLAYER_SPELL_COOLDOWNS, cooldowns);
    }

    @Override
    public final String magicPrerequisitesHelper(SpellCastContext context) {
        Mana mana = context.caster.getData(ModAttachments.MANA.get());
        if (mana.getMana() < getManaCost()) {
            return "Not enough mana to sustain " + getString() + ".";
        } else {
            PlayerSpellCooldowns cd = context.caster.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get());
            if (cd != null) {
                int cooldown = cd.getCooldown(ModSpells.getId(this));
                if (cooldown > 0) {
                    return "Spell " + getString() + " is on cooldown for " + cooldown + " more ticks.";
                }
            }

            return "";
        }
    }

    public final void applyMagicCosts(SpellCastContext context) {
        // only drain mana
        var mana = context.caster.getData(ModAttachments.MANA.get());
        mana.drainMana(getManaCost());
    }



}
