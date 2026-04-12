package com.minagic.minagic;

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

import static com.minagic.minagic.Minagic.NOVA_PROXY;
import static com.minagic.minagic.Minagic.PROJECTILE_PORTAL;


@EventBusSubscriber(modid = Minagic.MODID, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Minagic.FIREBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Minagic.VOID_BLAST_ENTITY.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Minagic.TRACER_BULLET_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Minagic.STAR_SHARD.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Minagic.VOIDBOURNE_SORCERER_ENEMY.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(NOVA_PROXY.get(), NovaProxyRenderer::new);
        event.registerEntityRenderer(PROJECTILE_PORTAL.get(), ProjectilePortal.Renderer::new);
        Minagic.LOGGER.debug("Registered NovaProxyRenderer for {}" , NOVA_PROXY.get());
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
