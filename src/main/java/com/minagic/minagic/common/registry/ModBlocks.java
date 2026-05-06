package com.minagic.minagic.common.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.minagic.minagic.Minagic.MODID;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    public static final void register(IEventBus bus){
        BLOCKS.register(bus);
    }
}
