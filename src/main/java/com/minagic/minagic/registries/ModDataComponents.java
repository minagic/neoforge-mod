package com.minagic.minagic.registries;

import com.minagic.minagic.abstractionLayer.SpellcastingItemData;
import com.minagic.minagic.sorcerer.StaffData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "minagic");

    // ABSTRACTION ONLY DATA COMPONENT

    public static final DeferredHolder<DataComponentType<?>, DataComponentType< SpellcastingItemData>> SPELLCASTING_ITEM_DATA =
            COMPONENTS.register("spellcasting_item_data", () ->
                    DataComponentType.<SpellcastingItemData>builder()
                            .persistent(SpellcastingItemData.codec())
                            .networkSynchronized(ByteBufCodecs.fromCodec(SpellcastingItemData.codec()))
                            .build()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<StaffData>> STAFF_DATA =
            COMPONENTS.register("staff_data", () ->
                    DataComponentType.<StaffData>builder()
                            .persistent(com.minagic.minagic.sorcerer.StaffData.codec())
                            .networkSynchronized(ByteBufCodecs.fromCodec(StaffData.codec()))
                            .build()
            );

    public static void register(IEventBus modBus) {
        COMPONENTS.register(modBus);
    }
}