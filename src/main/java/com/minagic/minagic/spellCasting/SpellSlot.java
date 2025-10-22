package com.minagic.minagic.spellCasting;

import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;

import com.minagic.minagic.spells.ISpell;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Optional;

public class SpellSlot {
    private @Nullable ISpell spell;                  // runtime cache
    private @Nullable ResourceLocation spellId;      // persistent identity

    public SpellSlot() { this(null); }

    public SpellSlot(@Nullable ISpell spell) {
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

    public void setSpell(@Nullable ISpell spell) {
        this.spell = spell;
        this.spellId = (spell == null) ? null : ModSpells.getId(spell);
    }

    public @Nullable ISpell getSpell() {
        resolveSpell();
        //System.out.println("This is SpellSlot reporting, current spell is: "+spell+"/"+ (spellId == null ? "null" : spellId.getPath().toString()));
        return spell;
    }

    public @Nullable ResourceLocation getSpellId() { return spellId; }

    public void cast(SpellCastContext context) {
        if (!(context.caster instanceof ServerPlayer player)) return;

        // Ensure resolved on use
        resolveSpell();
        if (spell == null) {
            player.sendSystemMessage(Component.literal("No spell assigned to this slot."));
            return;
        }

        System.out.println("Attempting to cast spell in slot: "+spell+"/"+ (spellId == null ? "null" : spellId.getPath().toString()));

        var cooldowns = context.caster.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get());
        var spell_ready = cooldowns.getCooldown(spellId) == 0;

        if (!spell_ready) {
            player.sendSystemMessage(Component.literal("Spell " + spell.getClass().getSimpleName() + " is on cooldown."));
            return;
        }
        var mana = context.caster.getData(ModAttachments.MANA.get());
        var mana_sufficient = mana.drainMana(spell.getManaCost());

        if (!mana_sufficient) {
            player.sendSystemMessage(Component.literal("Not enough mana to cast " + spell.getClass().getSimpleName() + "."));
            return;
        }


        boolean success = spell.cast(context);
        if (success) cooldowns.setCooldown(spellId, spell.getCooldownTicks());
        if (success) context.caster.setData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get(), cooldowns);
        if (success) context.caster.setData(ModAttachments.MANA.get(), mana);
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

//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof SpellSlot other)) return false;
//
//        return this.getSpell() == other.getSpell();
//    }
//
//    @Override
//    public int hashCode() {
//        return (spell != null ? spell.hashCode() : 0);
//    }
}