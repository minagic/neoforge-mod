package com.minagic.minagic.registries;

import com.minagic.minagic.abstractionLayer.SpellcastingItemData;
import com.minagic.minagic.sorcerer.StaffData;
import com.minagic.minagic.wizard.WizardWandData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public final class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "minagic");

    // SORCERER
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<StaffData>> STAFF_DATA =
            COMPONENTS.register("staff_data", () ->
                    DataComponentType.<StaffData>builder()
                            .persistent(StaffData.CODEC)
                            .build()
            );
    // WIZARD
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WizardWandData>> WIZARD_WAND_DATA =
            COMPONENTS.register("wizard_wand_data", () ->
                    DataComponentType.<WizardWandData>builder()
                            .persistent(WizardWandData.CODEC)
                            .build()
            );

    public static void register(IEventBus modBus) {
        COMPONENTS.register(modBus);
    }

    public static HashMap<DataComponentType<?>, Type> getAllSpellcastingItemDataComponents() {
        return new HashMap<DataComponentType<?>, Type>() {{
            put( STAFF_DATA.get(), StaffData.class);
            put( WIZARD_WAND_DATA.get(), WizardWandData.class);
        }};
    }
}