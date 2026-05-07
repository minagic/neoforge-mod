package com.minagic.minagic.api.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


// An abstract class representing a spell with casting lifecycle methods and validation.
public abstract class Spell {
    protected final SpellProperties properties;

    protected Spell() {
        this(defaultProperties());
    }

    protected Spell(SpellProperties properties) {
        this.properties = properties;
    }

    protected static SpellProperties defaultProperties() {
        return SpellProperties.DEFAULT;
    }

    protected record SpellProperties(
            int cooldown,
            int manaCost,
            int sustainCost,
            int simulacraThreshold,
            int simulacraMaxLifetime,
            String spellName,
            String idName,
            boolean isTechnical,
            boolean requireSimulacrumOnCast
    ) {
        private static final SpellProperties DEFAULT = new SpellProperties(
                0,
                0,
                0,
                0,
                -1,
                "No Spell",
                "no_spell",
                false,
                false
        );

        public SpellProperties {
            Objects.requireNonNull(spellName, "spellName");
            Objects.requireNonNull(idName, "idName");
        }

        public SpellProperties withCooldown(int cooldown) {
            return new SpellProperties(cooldown, manaCost, sustainCost, simulacraThreshold, simulacraMaxLifetime, spellName, idName, isTechnical, requireSimulacrumOnCast);
        }

        public SpellProperties withManaCost(int manaCost) {
            return new SpellProperties(cooldown, manaCost, sustainCost, simulacraThreshold, simulacraMaxLifetime, spellName, idName, isTechnical, requireSimulacrumOnCast);
        }

        public SpellProperties withSustainCost(int sustainCost) {
            return new SpellProperties(cooldown, manaCost, sustainCost, simulacraThreshold, simulacraMaxLifetime, spellName, idName, isTechnical, requireSimulacrumOnCast);
        }

        public SpellProperties withSimulacraThreshold(int simulacraThreshold) {
            return new SpellProperties(cooldown, manaCost, sustainCost, simulacraThreshold, simulacraMaxLifetime, spellName, idName, isTechnical, requireSimulacrumOnCast);
        }

        public SpellProperties withSimulacraMaxLifetime(int simulacraMaxLifetime) {
            return new SpellProperties(cooldown, manaCost, sustainCost, simulacraThreshold, simulacraMaxLifetime, spellName, idName, isTechnical, requireSimulacrumOnCast);
        }

        public SpellProperties withSpellName(String spellName) {
            return new SpellProperties(cooldown, manaCost, sustainCost, simulacraThreshold, simulacraMaxLifetime, spellName, idName, isTechnical, requireSimulacrumOnCast);
        }

        public SpellProperties withIdName(String idName) {
            return new SpellProperties(cooldown, manaCost, sustainCost, simulacraThreshold, simulacraMaxLifetime, spellName, idName, isTechnical, requireSimulacrumOnCast);
        }

        public SpellProperties withTechnical(boolean technical) {
            return new SpellProperties(cooldown, manaCost, sustainCost, simulacraThreshold, simulacraMaxLifetime, spellName, idName, technical, requireSimulacrumOnCast);
        }

        public SpellProperties withRequireSimulacrumOnCast(boolean requireSimulacrumOnCast) {
            return new SpellProperties(cooldown, manaCost, sustainCost, simulacraThreshold, simulacraMaxLifetime, spellName, idName, isTechnical, requireSimulacrumOnCast);
        }
    }


    // CASTING LIFECYCLE METHODS
    public void perform(SpellEventPhase phase, SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        if (!context.validate()) {
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
        return properties.spellName();
    }

    public final boolean isTechnical() {
        return properties.isTechnical();
    }

    public ResourceLocation getID(){
        return ResourceLocation.fromNamespaceAndPath(Minagic.MODID, properties.idName());
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
