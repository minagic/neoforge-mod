package com.minagic.minagic.spellCasting.spellslots;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spells.NoneSpell;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Optional;

public class SpellSlot {
    // ---------- CODEC: persist only the id ----------
    public static final Codec<SpellSlot> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    ResourceLocation.CODEC.optionalFieldOf("spell")
                            .forGetter(slot -> Optional.ofNullable(slot.spellId)))
            .apply(inst, (maybeId) -> {
                SpellSlot s = new SpellSlot(null);
                s.spellId = maybeId.orElse(null);
                return s;
            }));
    private @Nullable Spell spell;                  // runtime cache
    private @Nullable ResourceLocation spellId;      // persistent identity

    public SpellSlot() {
        this(new NoneSpell());
    }

    public SpellSlot(@Nullable Spell spell) {
        this.spell = spell;
        this.spellId = null; // will be filled on first resolve if needed
    }

    // ---------- Runtime resolution ----------
    public void resolveSpell() {
        // If we have an ID but no object, resolve it.
        if (spell == null && spellId != null) {
            if (ModSpells.get(spellId) == null) {
                Minagic.LOGGER.warn("SpellSlot could not resolve spell ID: {}", spellId);
            }
            this.spell = ModSpells.get(spellId);
        }
        // If we have an object but no ID, backfill the ID.
        if (spell != null && spellId == null) {
            if (ModSpells.getId(spell) == null) {
                Minagic.LOGGER.warn("SpellSlot could not resolve spell: {}", spell);
            }

            this.spellId = ModSpells.getId(spell);
        }
    }

    public Spell getSpell() {
        resolveSpell();
        return spell;
    }

    public void setSpell(Spell spell) {
        this.spell = spell;
        this.spellId = ModSpells.getId(spell);
    }

    // CASTING

    public @Nullable ResourceLocation getSpellId() {
        return spellId;
    }

    public void onStart(SpellCastContext context) {
        resolveSpell();
        if (spell != null) {
            spell.perform(SpellEventPhase.START, context, null);
        }
    }

    public void onStop(SpellCastContext context) {
        resolveSpell();
        if (spell != null) {
            spell.perform(SpellEventPhase.STOP, context, null);
        }
    }

    public String getEnterPhrase() {
        resolveSpell();
        return spell != null ? "Spell: " + spell.getClass().getSimpleName() : "No spell assigned.";
    }

    // overrides of equals and hashCode to ensure proper comparisons

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SpellSlot other)) return false;

        return this.getSpell() == other.getSpell();
    }

    @Override
    public int hashCode() {
        return (spell != null ? spell.hashCode() : 0);
    }
}
