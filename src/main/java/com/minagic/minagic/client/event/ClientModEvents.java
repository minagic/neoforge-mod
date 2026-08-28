package com.minagic.minagic.client.event;

import com.minagic.minagic.client.input.ClientKeybinds;
import com.minagic.minagic.Minagic;
import com.minagic.minagic.common.registry.ModEntityTypes;
import com.minagic.minagic.particles.CelestParticles;
import com.minagic.minagic.registries.ModParticles;
import com.minagic.minagic.sorcerer.celestial.spells.novaburst.NovaProxyRenderer;
import com.minagic.minagic.utilities.ProjectilePortal;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import com.minagic.minagic.wizard.starships.entities.models.wizard_fighter;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;


@EventBusSubscriber(modid = Minagic.MODID, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        Minagic.LOGGER.info("Registering entity renderers");
        event.registerEntityRenderer(ModEntityTypes.FIREBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.VOID_BLAST_ENTITY.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.TRACER_BULLET_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.STAR_SHARD.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.VOIDBOURNE_SORCERER_ENEMY.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.NOVA_PROXY.get(), NovaProxyRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.PROJECTILE_PORTAL.get(), ProjectilePortal.Renderer::new);
        event.registerEntityRenderer(ModEntityTypes.ARCANE_SHIP.get(), ArcaneShipEntity.Renderer::new);
        event.registerEntityRenderer(ModEntityTypes.MK1_BULLET.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.MK1_MISSILE.get(), ThrownItemRenderer::new);
        Minagic.LOGGER.info(

                "Registered renderer for {}",

                ModEntityTypes.ARCANE_SHIP.getId()

        );

        EntityType<ArcaneShipEntity> registeredType =
                ModEntityTypes.ARCANE_SHIP.get();

        Minagic.LOGGER.info(
                "Ship renderer key: id={}, identity={}",
                BuiltInRegistries.ENTITY_TYPE.getKey(registeredType),
                System.identityHashCode(registeredType)
        );

        event.registerEntityRenderer(
                registeredType,
                ArcaneShipEntity.Renderer::new
        );
        event.registerEntityRenderer(
                ModEntityTypes.ARCANE_FIGHTER.get(),
                ArcaneShipEntity.Renderer::new
        );
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        ClientKeybinds.registerKeybinds(event);
    }

    @SubscribeEvent
    public static void onRegisterRenderingLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(wizard_fighter.LAYER_LOCATION,  wizard_fighter::createBodyLayer);

    }
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.CELEST_PARTICLES.get(), CelestParticles.Provider::new);
    }

}
