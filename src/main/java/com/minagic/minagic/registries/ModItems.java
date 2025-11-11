package com.minagic.minagic.registries;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.druid.DruidHorn;
import com.minagic.minagic.item.EffectWandItem;
import com.minagic.minagic.sorcerer.sorcererStaff;
import com.minagic.minagic.wizard.WizardWand;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {


    public static final DeferredItem<Item> SORCERER_STAFF = Minagic.ITEMS.registerItem("sorcerer_staff", sorcererStaff::new);

    public static final DeferredItem<Item> WIZARD_WAND = Minagic.ITEMS.register("wizard_wand",
            () -> new WizardWand(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.parse(Minagic.MODID + "wizard_wand"))))
    );

    public static final DeferredItem<Item> DRUID_HORN = Minagic.ITEMS.register("druid_horn",
            () -> new DruidHorn(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.parse(Minagic.MODID + "druid_horn"))))
    );

    public static void register(IEventBus modBus) { Minagic.ITEMS.register(modBus); }
}