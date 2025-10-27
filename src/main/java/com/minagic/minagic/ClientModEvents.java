package com.minagic.minagic;

import com.minagic.minagic.spells.FireballEntity;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;


public class ClientModEvents {

    @SubscribeEvent
    public void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Minagic.FIREBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(Minagic.VOID_BLAST_ENTITY.get(), ThrownItemRenderer::new);
    }

    @SubscribeEvent
    public void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        ClientKeybinds.registerKeybinds(event);
    }
}
