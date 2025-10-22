package com.minagic.minagic.utilities;

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

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<StaffData>> STAFF_DATA =
            COMPONENTS.register("staff_data", () ->
                    DataComponentType.<StaffData>builder()
                            .persistent(StaffData.CODEC)
                            .networkSynchronized(ByteBufCodecs.fromCodec(StaffData.CODEC))
                            .build()
            );

    public static void register(IEventBus modBus) {
        COMPONENTS.register(modBus);
    }
}