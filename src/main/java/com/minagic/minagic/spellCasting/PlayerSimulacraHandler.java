package com.minagic.minagic.spellCasting;

import com.minagic.minagic.registries.ModAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class PlayerSimulacraHandler {
    @SubscribeEvent
    public void onEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;
        LivingEntity entity = event.getEntity().asLivingEntity();
        if (entity == null) return;
        var sim = entity.getData(ModAttachments.PLAYER_SIMULACRA.get());
        sim.resolveAllContexts(entity.level());
        sim.tick();
        entity.setData(ModAttachments.PLAYER_SIMULACRA.get(), sim);
    }
}
