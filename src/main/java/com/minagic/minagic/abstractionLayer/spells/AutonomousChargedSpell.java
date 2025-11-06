package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

//// An abstract class representing spells that are charged up over time before being released.
public class AutonomousChargedSpell extends Spell {

    @Override
    public final boolean preCast(SpellCastContext context) {
        return checkContext(context, true, true, getManaCost(), true, false);
    }

    @Override
    public final boolean preExitSimulacrum(SpellCastContext context) {
        return false;
    }

    @Override
    public final boolean preTick(SpellCastContext context) {
        return false;
    }

    @Override
    public final boolean preStart(SpellCastContext context) {
        return checkContext(context, true, true, 0, true, false);
    }

    @Override
    public final boolean preStop(SpellCastContext context) {
        return false;
    }



    @Override
    public final void postStart(SpellCastContext context) {}

    @Override
    public final void postTick(SpellCastContext context) {}

    @Override
    public final void postStop(SpellCastContext context) {}

    @Override
    public final void postCast(SpellCastContext context) {
        applyMagicCosts(context, getCooldownTicks(), getManaCost());
    }

    @Override
    public final void postExitSimulacrum(SpellCastContext context) {}



    // lifecycle methods
    @Override
    public final void start(SpellCastContext context) {

        // Get player simulacra attachment
        PlayerSimulacraAttachment sim = context.caster.getData(ModAttachments.PLAYER_SIMULACRA.get());

        // Toggle logic: if already active, remove; else add
        var existing = sim.getBackgroundSimulacra().get(ModSpells.getId(this));

        if (existing != null) {
            PlayerSimulacraAttachment.removeSimulacrum(context.caster, context.level, ModSpells.getId(this));
        } else {
            PlayerSimulacraAttachment.addSimulacrum(context.caster, context.level, this, getSimulacrumThreshold(), getMaxLifetime(), context.stack);
        }
    }

    @Override
    public  final void tick(SpellCastContext context) {
        // No-op for autonomous charged spells
    }

    @Override
    public final void stop(SpellCastContext context) {
        // No-op for autonomous charged spells
    }

    @Override
    public final void exitSimulacrum(SpellCastContext context) {
        // no-op
    }

    @Override
    public final int getMaxLifetime(){
        return getSimulacrumThreshold();
        // lifetime equals threshold for autonomous charged spells.
        // This will exit as soon as threshold is reached.
    }

    @Override
    public int getCooldownTicks(){
        return 0; // default cooldown
    }

    @Override
    public String getString() {
        return "Autonomous Charged Spell";
    }

    @Override
    public int getManaCost() {
        return 0; // default mana cost
    }



}
