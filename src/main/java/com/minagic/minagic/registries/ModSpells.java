package com.minagic.minagic.registries;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.abstractionLayer.spells.Spell;
import com.minagic.minagic.druid.spells.CircleOfLife;
import com.minagic.minagic.druid.spells.OathOfLife;
import com.minagic.minagic.sorcerer.spells.EventHorizon;
import com.minagic.minagic.sorcerer.spells.FireballBarrage;
import com.minagic.minagic.sorcerer.spells.InstantFireballImbueSpell;
import com.minagic.minagic.sorcerer.spells.VoidBlast;
import com.minagic.minagic.spellCasting.SpellRegistry;
import com.minagic.minagic.spells.Fireball;
import com.minagic.minagic.spells.NoneSpell;
import com.minagic.minagic.testing.spells.*;
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

        // register dev spells here
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "dev_instnateneous"), new InstantaneousDevSpell());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "dev_channeled"), new ChanneledDevSpell());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "dev_charged"), new ChargedDevSpell());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "dev_charged_autonomous"), new AutonomousChargedDevSpell());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "dev_channeled_autonomous"), new ChanneledAutonomousDevSpell());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "dev_autonomous"), new AutonomousDevSpell());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "imbue_fireball"), new InstantFireballImbueSpell());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "circle_of_life"), new CircleOfLife());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "oath_of_life"), new OathOfLife());
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