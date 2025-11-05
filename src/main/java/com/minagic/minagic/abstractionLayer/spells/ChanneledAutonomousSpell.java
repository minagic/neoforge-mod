package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.capabilities.Mana;
import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
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
    public int getSimulacrumThreshold() {
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
        return 30; // default cooldown
    }

    // lifecycle like of channelled spell

    @Override
    public final void onStart(SpellCastContext context) {
        LivingEntity player = preCast(context);
        if (player == null) {
            return; // Pre-cast checks failed
        }

        PlayerSimulacraAttachment.addSimulacrum(context.caster, context.level, this, getSimulacrumThreshold(), -1, context.stack);

    }

    @Override
    public final void tick(SpellCastContext context) {
        // no-op
    }

    @Override
    public final void onStop(SpellCastContext context) {
        LivingEntity player = preStop(context);
        if (player == null) {
            return; // Pre-cast checks failed
        }
        context.caster = player;
        PlayerSimulacraAttachment.removeSimulacrum(context.caster, context.level, ModSpells.getId(this));

    }

    @Override
    public LivingEntity preCast(SpellCastContext context) {return checkContext(context, true, true, getManaCost(), true);}

    @Override
    public LivingEntity preExitSimulacrum(SpellCastContext context) {return checkContext(context, true, false, 0, false);}

    @Override
    public LivingEntity preTick(SpellCastContext context) {return null;}

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

}
