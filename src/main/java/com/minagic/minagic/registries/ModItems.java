package com.minagic.minagic.registries;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.item.EffectWandItem;
import com.minagic.minagic.sorcerer.sorcererStaff;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {


    public static final DeferredItem<Item> SORCERER_STAFF = Minagic.ITEMS.register("sorcerer_staff",
            () -> new sorcererStaff(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "sorcerer_staff"))))
    );

    public static final DeferredItem<Item> EFFECT_WAND = Minagic.ITEMS.register("effect_wand",
            () -> new EffectWandItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.parse(Minagic.MODID + "effect_wand"))))
    );

    public static void register(IEventBus modBus) { Minagic.ITEMS.register(modBus); }
}