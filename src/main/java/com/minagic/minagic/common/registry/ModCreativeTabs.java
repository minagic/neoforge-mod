package com.minagic.minagic.common.registry;

import com.minagic.minagic.registries.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.minagic.minagic.Minagic.MODID;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TABS.register(bus);
        bus.addListener(ModCreativeTabs::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.SORCERER_STAFF);
            event.accept(ModItems.WIZARD_WAND);
            event.accept(ModItems.DRUID_HORN);
        }
    }
}