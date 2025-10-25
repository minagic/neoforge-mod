package com.minagic.minagic.registries;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.abstractionLayer.Spell;
import com.minagic.minagic.spellCasting.SpellRegistry;
import com.minagic.minagic.spells.Fireball;
import com.minagic.minagic.spells.NoneSpell;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;


public final class ModSpells {
    public static void register() {
        // REGISTER ALL SPELLS HERE
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "fireball"), new Fireball());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "empty_spell"), new NoneSpell());
    }
    public static @Nullable Spell get(ResourceLocation id) {
        return SpellRegistry.getSpell(id);
    }

    public static @Nullable ResourceLocation getId(Spell spell) {
        System.out.println("MOD SPELLS: Getting ID for spell: "+spell);
        System.out.println("MOD SPELLS: ID is: "+SpellRegistry.getId(spell));
        return SpellRegistry.getId(spell);
    }

    public static @Nullable Spell getFromString(String idString) {
        ResourceLocation id = ResourceLocation.parse(idString);
        return get(id);
    }
}