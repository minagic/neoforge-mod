package com.minagic.minagic.registries;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.wizard.starships.utilities.ShipState;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModEntityDataSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> SERIALIZERS =
            DeferredRegister.create(
                    NeoForgeRegistries.ENTITY_DATA_SERIALIZERS,
                    Minagic.MODID
            );

    public static final DeferredHolder<
                EntityDataSerializer<?>,
                EntityDataSerializer<ShipState>
                > SHIP_STATE =
            SERIALIZERS.register(
                    "ship_state",
                    () -> ShipState.ENTITY_DATA_SERIALIZER
            );

    private ModEntityDataSerializers() {
    }

    public static void register(IEventBus modBus) {
        SERIALIZERS.register(modBus);
    }
}