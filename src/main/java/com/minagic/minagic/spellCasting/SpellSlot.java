package com.minagic.minagic.spellCasting;

import com.minagic.minagic.abstractionLayer.Spell;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;

import com.minagic.minagic.spells.NoneSpell;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Optional;

public class SpellSlot {
    private @Nullable Spell spell;                  // runtime cache
    private @Nullable ResourceLocation spellId;      // persistent identity

    public SpellSlot() { this(new NoneSpell()); }

    public SpellSlot(@Nullable Spell spell) {
        this.spell = spell;
        this.spellId = null; // will be filled on first resolve if needed
    }

    // ---------- Runtime resolution ----------
    public void resolveSpell() {
        // If we have an ID but no object, resolve it.
        if (spell == null && spellId != null) {
            if (ModSpells.get(spellId) == null) {
                System.out.println("Achtung!!!! Codebase on Fire!!!! SpellSlot could not resolve spell ID: " + spellId);
            }
            this.spell = ModSpells.get(spellId);
        }
        // If we have an object but no ID, backfill the ID.
        if (spell != null && spellId == null) {
            if (ModSpells.getId(spell) == null) {
                System.out.println("Achtung!!!! Codebase on Fire!!!! SpellSlot could not resolve spell: " + spell);
            }

            this.spellId = ModSpells.getId(spell);
        }
    }

    public void setSpell(Spell spell) {
        System.out.println("[-SET SPELL-]Setting SpellSlot spell to: "+spell);

        this.spell = spell;
        this.spellId = ModSpells.getId(spell);
        System.out.println("[-SET SPELL-] SpellSlot spellId: "+spellId);

    }

    public Spell getSpell() {
        resolveSpell();
        return spell;
    }

    public @Nullable ResourceLocation getSpellId() { return spellId; }

    // CASTING

    public void onStart(SpellCastContext context) {
        resolveSpell();
        if (spell != null) {
            spell.onStart(context);
        }
    }

    public void onStop(SpellCastContext context) {
        resolveSpell();
        if (spell != null) {
            spell.onStop(context);
        }
    }

    public String getEnterPhrase() {
        resolveSpell();
        return spell != null ? "Spell: " + spell.getClass().getSimpleName() : "No spell assigned.";
    }

    // ---------- CODEC: persist only the id ----------
    public static final Codec<SpellSlot> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.optionalFieldOf("spell")
                    .forGetter(slot -> Optional.ofNullable(slot.spellId)))
            .apply(inst, (maybeId) -> {
        SpellSlot s = new SpellSlot(null);
        s.spellId = maybeId.orElse(null);
        return s;
    }));

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