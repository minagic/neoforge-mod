package com.minagic.minagic.spellCasting;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.SpellcastingItem;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.registries.ModSpells;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellRegistry {
    private static final Map<ResourceLocation, Spell> REGISTRY = new HashMap<>();
    private static final Map<Spell, ResourceLocation> REVERSE = new HashMap<>();


    public static void register(Spell spell) {
        ResourceLocation id = spell.getID();
        if (REGISTRY.containsKey(id)){
            throw new IllegalArgumentException("Spell with ID " + id + " is already registered");
        }

        REGISTRY.put(id, spell);
        REVERSE.put(spell, id);
        Minagic.LOGGER.debug("Successfully registered spell: {}/{}", id, spell);
    }

    public static Spell getSpell(ResourceLocation id) {
        Minagic.LOGGER.trace("[SpellRegistry] Lookup by id {} within {} entries", id, REGISTRY.size());

        if (id != null && REGISTRY.containsKey(id)) {
            return REGISTRY.get(id);
        } else {
            return null;
        }
    }

    public static ResourceLocation getId(Spell spell) {
        Minagic.LOGGER.trace("[SpellRegistry] Lookup by spell {}", spell == null ? "null" : spell.getClass().getSimpleName());
        return REVERSE.get(spell);
    }

    public static List<Spell> getSpells(SpellcastingItem item) {
        return REGISTRY.values().stream()
                .filter(item::canCastSpell)
                .filter(spell -> !spell.isTechnical())
                .toList();
    }

    public static Map<ResourceLocation, Spell> getFullMap(){
        return REGISTRY;
    }

    public static final Codec<Spell> SPELL_CODEC = ResourceLocation.CODEC.xmap(
            SpellRegistry::getSpell,
            SpellRegistry::getId
    );
}
