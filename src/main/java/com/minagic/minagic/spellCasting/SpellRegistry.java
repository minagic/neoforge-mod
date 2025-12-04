package com.minagic.minagic.spellCasting;

import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.api.spells.SpellValidator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellRegistry {
    private static final Map<ResourceLocation, Spell> REGISTRY = new HashMap<>();
    private static final Map<Spell, ResourceLocation> REVERSE = new HashMap<>();


    public static void register(ResourceLocation id, Spell spell) {
        REGISTRY.put(id, spell);
        REVERSE.put(spell, id);
    }
    public static Spell getSpell(ResourceLocation id) {
        //System.out.println("Attempting lookup for spell ID: " + id + " in SpellRegistry: " + REGISTRY.keySet());

        if (id != null && REGISTRY.containsKey(id)) {
            return REGISTRY.get(id);
        }
        else{
            return null;
        }

        //return id == null || id.equals(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "empty_spell")) ? REGISTRY.get(id) : null;
    }

    public static ResourceLocation getId(Spell spell) {
        //System.out.println("SPELL REGISTRY: Looking up " + spell);
        //System.out.println("SPELL REGISTRY: " + REVERSE.keySet());
        //System.out.println("SPELL REGISTRY: Found ID: " + REVERSE.get(spell));
        return REVERSE.get(spell);
    }

    public static List<Spell> getSpells(Player player) {
        return REGISTRY.values().stream().filter(spell -> spell.canCast(new SpellCastContext(player)) == SpellValidator.CastFailureReason.OK && !spell.isTechnical()).toList();
    }
}
