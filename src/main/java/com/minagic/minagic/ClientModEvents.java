package com.minagic.minagic;

import com.minagic.minagic.particles.VoidParticle;
import com.minagic.minagic.registries.ModParticles;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;


public class ClientModEvents {

    @SubscribeEvent
    public void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Minagic.FIREBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Minagic.VOID_BLAST_ENTITY.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Minagic.TRACER_BULLET_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Minagic.STAR_SHARD.get(), ThrownItemRenderer::new);
    }

    @SubscribeEvent
    public void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        ClientKeybinds.registerKeybinds(event);
    }

    @SubscribeEvent
    public void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.VOID_PARTICLE.get(), VoidParticle.Provider::new);
    }
}
