package com.minagic.minagic.registries;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.AutoDetection;
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
import org.reflections.Reflections;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Set;


public final class ModSpells {


    public static void register() {
        Reflections reflections = new Reflections("com.minagic.minagic");

        Set<Class<?>> spellClasses =
                reflections.getTypesAnnotatedWith(AutoDetection.Spell.class);

        Minagic.LOGGER.debug("Located {} @AutoDetection.Spell classes: {}", spellClasses.size(), spellClasses);

        for (Class<?> spellClass : spellClasses) {
            try {
                if (!Spell.class.isAssignableFrom(spellClass)){
                    throw new IllegalClassFormatException("A non-spell class was registered with @AutoDetection.Spell: "+spellClass.getName());
                }
                Spell spell = (Spell) spellClass.getDeclaredConstructor().newInstance();
                SpellRegistry.register(spell);
                Minagic.LOGGER.info("Auto-registered spell: " + spell.getID());
            }
            catch (Exception e) {
                Minagic.LOGGER.error("Failed to auto-register spell: " + spellClass.getName(), e);
            }
        }

    }
}