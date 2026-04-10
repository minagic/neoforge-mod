package com.minagic.minagic.api.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;


// An abstract class representing a spell with casting lifecycle methods and validation.
public abstract class Spell {
    // properties
    protected int cooldown = 0;
    protected int manaCost = 0;
    protected int sustainCost = 0;
    protected int simulacraThreshold = 0;
    protected int simulacraMaxLifetime = -1;
    protected String spellName = "No Spell";
    protected String idName = "no_spell";
    protected boolean isTechnical = false;


    // CASTING LIFECYCLE METHODS
    public void perform(SpellEventPhase phase, SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        if (context.validate()) {
            return;
        }
        if (simulacrumData != null) {
            if (!simulacrumData.validate()) {
                return;
            }
        }

        dispatchPhase(phase, context, simulacrumData);

    }

    protected void dispatchPhase(SpellEventPhase phase,
                                 SpellCastContext context,
                                 @Nullable SimulacrumData simulacrumData) {

        switch (phase) {
            case START -> start(context, simulacrumData);
            case STOP -> stop(context, simulacrumData);
            case EXIT_SIMULACRUM -> exitSimulacrum(context, simulacrumData);
            case CAST -> cast(context, simulacrumData);
            case TICK -> tick(context, simulacrumData);
        }
    }

    // OVERRIDES TO DEFINE SPELL BEHAVIOR
    // the main spell logic goes here
    // the context is guaranteed to be valid here
    protected void start(SpellCastContext context, SimulacrumData simulacrumData) {
    }

    protected void tick(SpellCastContext context, SimulacrumData simulacrumData) {
    }

    protected void stop(SpellCastContext context, SimulacrumData simulacrumData) {
    }

    protected void cast(SpellCastContext context, SimulacrumData simulacrumData) {
    }

    protected void exitSimulacrum(SpellCastContext context, SimulacrumData simulacrumData) {
    }

    public final String getString() {
        return spellName;
    }

    public final boolean isTechnical() {
        return isTechnical;
    }

    public ResourceLocation getID(){
        return ResourceLocation.fromNamespaceAndPath(Minagic.MODID, idName);
    }

    // CASTER VALIDATION METHODS
    // check if caster can use this spell
    // default: OK for all casters
    //  HUD
    public int color(float progress) {
        return 0x00000000;
    }

    // EQUALITY OVERRIDES

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

}
