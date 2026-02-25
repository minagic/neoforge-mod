package com.minagic.minagic.registries;

import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.druid.spells.CircleOfLife;
import com.minagic.minagic.druid.spells.OathOfLife;
import com.minagic.minagic.sorcerer.celestial.spells.*;
import com.minagic.minagic.sorcerer.celestial.spells.novaburst.NovaBurst;
import com.minagic.minagic.sorcerer.spells.EventHorizon;
import com.minagic.minagic.sorcerer.spells.FireballBarrage;
import com.minagic.minagic.sorcerer.spells.VoidBlast;
import com.minagic.minagic.sorcerer.voidbourne.spells.KineticNullificationField;
import com.minagic.minagic.spellCasting.SpellRegistry;
import com.minagic.minagic.spells.Fireball;
import com.minagic.minagic.spells.NoneSpell;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class ModSpells {


    public static void register() {
        // REGISTER ALL SPELLS HERE
        SpellRegistry.register(new Fireball());
        SpellRegistry.register(new NoneSpell());
        SpellRegistry.register(new VoidBlast());
        SpellRegistry.register(new FireballBarrage());
        SpellRegistry.register(new EventHorizon());
        SpellRegistry.register(new Banishment());
        SpellRegistry.register(new RadiantIllumination());
        SpellRegistry.register(new RadiantIllumination.RadiantIlluminationBlinder());
        SpellRegistry.register(new KineticNullificationField());
        SpellRegistry.register(new CelestialBombardment());

        SpellRegistry.register(new CircleOfLife());
        SpellRegistry.register(new OathOfLife());

        SpellRegistry.register(new TracerBullet());
        SpellRegistry.register(new TracerBullet.Exposure());

        SpellRegistry.register(new TorchPlacement());
        SpellRegistry.register(new SolarShield());
        SpellRegistry.register(new SolarSurge());
        SpellRegistry.register(new RadiantBlink());
        SpellRegistry.register(new GravitationalSuspension());
        SpellRegistry.register(new NovaBurst());
        SpellRegistry.register(new NovaBurst.NovaPulse());
        SpellRegistry.register(new NovaBurst.NovaPulsePrecursor());
        SpellRegistry.register(new AetherGlide());

    }

    public static @Nullable Spell get(ResourceLocation id) {
        return SpellRegistry.getSpell(id);
    }

    @Deprecated(forRemoval = true)
    public static @Nullable ResourceLocation getId(Spell spell) {
        return SpellRegistry.getId(spell);
    }

}