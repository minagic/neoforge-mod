package com.minagic.minagic;

import com.minagic.minagic.common.registry.ModEntityTypes;
import com.minagic.minagic.entity.sorcerer.voidbourne.VoidborneSorcererEntity;
import com.minagic.minagic.sorcerer.celestial.spells.novaburst.NovaImpactProxyEntity;
import com.minagic.minagic.utilities.ProjectilePortal;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Minagic.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Minagic.MODID, value = Dist.CLIENT)
public class MinagicClient {
    public MinagicClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        Minagic.LOGGER.info("HELLO FROM CLIENT SETUP");
        Minagic.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.VOIDBOURNE_SORCERER_ENEMY.get(),
                VoidborneSorcererEntity.createAttributes().build());
        event.put(
                ModEntityTypes.NOVA_PROXY.get(),
                NovaImpactProxyEntity.createAttributes().build());

        event.put(
                ModEntityTypes.PROJECTILE_PORTAL.get(),
                ProjectilePortal.createAttributes().build()
        );

        event.put(
                ModEntityTypes.ARCANE_SHIP.get(),
                ArcaneShipEntity.createAttributes().build()

        );
    }
}
