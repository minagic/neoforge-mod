package com.minagic.minagic.client.event;

import com.minagic.interfaces.MinagicLivingRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

public class LivingRenderingEvent {
    @SubscribeEvent
    public static void onPlayerRenderEvent(RenderLivingEvent.Pre event){
        LivingEntityRenderState state = event.getRenderState();
        if (state instanceof MinagicLivingRenderState mstate && mstate.minagic$isInsideArcaneShip()){
            event.setCanceled(true);
        }

    }
}
