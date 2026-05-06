package com.minagic.minagic.common.registry;

import com.minagic.minagic.registries.*;
import com.minagic.testing.TestsRegistration;
import net.neoforged.bus.api.IEventBus;

public final class ModRegistries {
    public static void register(IEventBus modEventBus) {
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModParticles.register(modEventBus);
        MinagicNetwork.register(modEventBus);

        ModSpells.register();
        ModPowerSources.register();
        ProjectilePortalRenderers.register();


        TestsRegistration.TEST_FUNCTION.register(modEventBus);
    }

}