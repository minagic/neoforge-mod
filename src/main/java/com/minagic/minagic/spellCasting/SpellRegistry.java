package com.minagic.minagic.spellCasting;

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
        return REGISTRY.get(id);
    }

    public static ResourceLocation getId(ISpell spell) {
        return REVERSE.get(spell);
    }
}
