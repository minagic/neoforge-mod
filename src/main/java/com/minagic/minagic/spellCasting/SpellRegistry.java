package com.minagic.minagic.spellCasting;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.spells.ISpell;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class SpellRegistry {
    private static final Map<ResourceLocation, ISpell> REGISTRY = new HashMap<>();
    private static final Map<ISpell, ResourceLocation> REVERSE = new HashMap<>();



    public static void register(ResourceLocation id, ISpell spell) {
        REGISTRY.put(id, spell);
        REVERSE.put(spell, id);
    }
    public static ISpell getSpell(ResourceLocation id) {
        System.out.println("Attempting lookup for spell ID: " + id + " in SpellRegistry: " + REGISTRY.keySet());

        if (id != null && REGISTRY.containsKey(id)) {
            return REGISTRY.get(id);
        }
        else{
            return null;
        }

        //return id == null || id.equals(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "empty_spell")) ? REGISTRY.get(id) : null;
    }

    public static ResourceLocation getId(ISpell spell) {
        return REVERSE.get(spell);
    }
}
