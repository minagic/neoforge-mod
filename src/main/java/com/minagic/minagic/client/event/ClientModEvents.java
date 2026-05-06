package com.minagic.minagic.client.event;

import com.minagic.minagic.client.input.ClientKeybinds;
import com.minagic.minagic.Minagic;
import com.minagic.minagic.common.registry.ModEntityTypes;
import com.minagic.minagic.particles.CelestParticles;
import com.minagic.minagic.registries.ModParticles;
import com.minagic.minagic.sorcerer.celestial.spells.novaburst.NovaProxyRenderer;
import com.minagic.minagic.utilities.ProjectilePortal;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
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
        event.registerEntityRenderer(ModEntityTypes.FIREBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.VOID_BLAST_ENTITY.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.TRACER_BULLET_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.STAR_SHARD.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.VOIDBOURNE_SORCERER_ENEMY.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.NOVA_PROXY.get(), NovaProxyRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.PROJECTILE_PORTAL.get(), ProjectilePortal.Renderer::new);
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        ClientKeybinds.registerKeybinds(event);
    }


    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.CELEST_PARTICLES.get(), CelestParticles.Provider::new);
    }

}
