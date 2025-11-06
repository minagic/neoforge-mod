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

    // pre* methods

    @Override
    public final boolean preStart(SpellCastContext context) {return checkContext(context, true, true, 0, true, false); }

    @Override
    public final boolean preTick(SpellCastContext context) {return false;}

    @Override
    public final boolean preStop(SpellCastContext context) {return checkContext(context, true, false, 0, true, false); }

    @Override
    public final boolean preExitSimulacrum(SpellCastContext context) {return false;}

    @Override
    public final boolean preCast(SpellCastContext context) {return checkContext(context, true, true, getManaCost(), true, false);}

    // post* methods

    @Override
    public final void postStart(SpellCastContext context) {}

    @Override
    public final void postTick(SpellCastContext context) {}

    @Override
    public final void postStop(SpellCastContext context) {}

    @Override
    public final void postCast(SpellCastContext context) {
        applyMagicCosts(context, 0, getManaCost());
    }

    @Override
    public final void postExitSimulacrum(SpellCastContext context) {
        applyMagicCosts(context, getCooldownTicks(), 0);
    }

    @Override
    public final void start(SpellCastContext context) {
        PlayerSimulacraAttachment.setActiveChanneling(context.caster, context.level, this, getSimulacrumThreshold(), -1, context.stack);

    }

    @Override
    public final void tick(SpellCastContext context) {
        // no-op
    }

    @Override
    public final void stop(SpellCastContext context) {
        PlayerSimulacraAttachment.clearChanneling(context.caster, context.level);

    }

    @Override
    public final void exitSimulacrum(SpellCastContext context) {}

}
