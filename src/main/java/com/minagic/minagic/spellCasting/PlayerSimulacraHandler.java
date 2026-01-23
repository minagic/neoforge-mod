package com.minagic.minagic.spellCasting;

import com.minagic.minagic.registries.ModAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class PlayerSimulacraHandler {
    @SubscribeEvent
    public void onEntityTick(EntityTickEvent.Post event) {
        Level level = event.getEntity().level();

        if (level.isClientSide()) return;
        LivingEntity entity = event.getEntity().asLivingEntity();
        if (entity == null) return;
        var sim = entity.getData(ModAttachments.PLAYER_SIMULACRA.get());
        sim.resolveAllContexts(level.getServer());
        sim.tick();
        entity.setData(ModAttachments.PLAYER_SIMULACRA.get(), sim);
    }
}
