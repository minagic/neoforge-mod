package com.minagic.minagic;

import com.minagic.minagic.particles.CelestParticles;
import com.minagic.minagic.registries.ModParticles;
import com.minagic.minagic.sorcerer.celestial.spells.novaburst.NovaProxyRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import static com.minagic.minagic.Minagic.NOVA_PROXY;


public class ClientModEvents {

    @SubscribeEvent
    public void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Minagic.FIREBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Minagic.VOID_BLAST_ENTITY.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Minagic.TRACER_BULLET_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Minagic.STAR_SHARD.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Minagic.VOIDBOURNE_SORCERER_ENEMY.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(NOVA_PROXY.get(), NovaProxyRenderer::new);
        Minagic.LOGGER.debug("Registered NovaProxyRenderer for {}" , NOVA_PROXY.get());
    }

    @SubscribeEvent
    public void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        ClientKeybinds.registerKeybinds(event);
    }


    @SubscribeEvent
    public void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.CELEST_PARTICLES.get(), CelestParticles.Provider::new);
    }

}
