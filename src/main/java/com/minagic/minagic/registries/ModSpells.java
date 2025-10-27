package com.minagic.minagic.registries;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.abstractionLayer.Spell;
import com.minagic.minagic.sorcerer.spells.EventHorizon;
import com.minagic.minagic.sorcerer.spells.FireballBarrage;
import com.minagic.minagic.sorcerer.spells.VoidBlast;
import com.minagic.minagic.spellCasting.SpellRegistry;
import com.minagic.minagic.spells.Fireball;
import com.minagic.minagic.spells.NoneSpell;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;


public final class ModSpells {
    public static void register() {
        // REGISTER ALL SPELLS HERE
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "fireball"), new Fireball());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "empty_spell"), new NoneSpell());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "void_blast"), new VoidBlast());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "fireball_barrage"), new FireballBarrage());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "event_horizon"), new EventHorizon());
    }
    public static @Nullable Spell get(ResourceLocation id) {
        return SpellRegistry.getSpell(id);
    }

    public static @Nullable ResourceLocation getId(Spell spell) {
        return SpellRegistry.getId(spell);
    }

    public static final Codec<Spell> SPELL_CODEC = ResourceLocation.CODEC.xmap(
            ModSpells::get,
            ModSpells::getId
    );
}