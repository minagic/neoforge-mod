package com.minagic.minagic.registries;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.spellCasting.SpellRegistry;
import com.minagic.minagic.spells.Fireball;
import com.minagic.minagic.spells.ISpell;
import com.minagic.minagic.spells.NoneSpell;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.RegistryAccess;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.minagic.minagic.spellCasting.SpellRegistry;
import net.minecraft.core.Holder.Reference;
import java.util.function.Supplier;


public final class ModSpells {
    public static void register() {
        // REGISTER ALL SPELLS HERE
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "fireball"), new Fireball());
        SpellRegistry.register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "empty_spell"), new NoneSpell());
    }
    public static @Nullable ISpell get(ResourceLocation id) {
        return SpellRegistry.getSpell(id);
    }

    public static @Nullable ResourceLocation getId(ISpell spell) {
        System.out.println("MOD SPELLS: Getting ID for spell: "+spell);
        System.out.println("MOD SPELLS: ID is: "+SpellRegistry.getId(spell));
        return SpellRegistry.getId(spell);
    }

    public static @Nullable ISpell getFromString(String idString) {
        ResourceLocation id = ResourceLocation.parse(idString);
        return get(id);
    }
}