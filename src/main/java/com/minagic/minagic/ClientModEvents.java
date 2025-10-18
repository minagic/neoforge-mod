package com.minagic.minagic;

import com.minagic.minagic.spells.FireballEntity;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;



public class ClientModEvents {

    @SubscribeEvent
    public void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer((EntityType<FireballEntity>) Minagic.FIREBALL.get(), ThrownItemRenderer::new);
    }
}
