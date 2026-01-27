package com.minagic.minagic.registries;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.druid.spells.CircleOfLife;
import com.minagic.minagic.druid.spells.OathOfLife;
import com.minagic.minagic.sorcerer.celestial.spells.*;
import com.minagic.minagic.sorcerer.spells.EventHorizon;
import com.minagic.minagic.sorcerer.spells.FireballBarrage;
import com.minagic.minagic.sorcerer.spells.InstantFireballImbueSpell;
import com.minagic.minagic.sorcerer.spells.VoidBlast;
import com.minagic.minagic.sorcerer.voidbourne.spells.KineticNullificationField;
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
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "banishment"), new Banishment());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "radiant_illumination"), new RadiantIllumination());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "radiant_illumination_linder"), new RadiantIllumination.RadiantIlluminationBlinder());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "kinetic_nullification_field"), new KineticNullificationField());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "celestial_bombardment"), new CelestialBombardment());

        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "imbue_fireball"), new InstantFireballImbueSpell());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "circle_of_life"), new CircleOfLife());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "oath_of_life"), new OathOfLife());

        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "tracer_bullet"), new TracerBullet());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "exposure"), new TracerBullet.Exposure());

        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "torch_placement"), new TorchPlacement());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "solar_shield"), new SolarShield());
        // register dev spells here
//        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "dev_instnateneous"), new InstantaneousDevSpell());
//        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "dev_channeled"), new ChanneledDevSpell());
//        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "dev_charged"), new ChargedDevSpell());
//        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "dev_charged_autonomous"), new AutonomousChargedDevSpell());
//        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "dev_channeled_autonomous"), new ChanneledAutonomousDevSpell());
//        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "dev_autonomous"), new AutonomousDevSpell());

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