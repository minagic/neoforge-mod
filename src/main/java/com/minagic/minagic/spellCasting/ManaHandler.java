package com.minagic.minagic.spellCasting;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.Mana;
import com.minagic.minagic.registries.ModAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class ManaHandler {
    @SubscribeEvent
    public void onPlayerTick(EntityTickEvent.Pre event) {
        LivingEntity entity = event.getEntity().asLivingEntity();
        if (entity == null || entity.level().isClientSide()) return;

        Mana mana = entity.getData(ModAttachments.MANA.get());
        mana.tick(entity);
        Minagic.LOGGER.trace("[Minagic] Mana tick for {} -> {}/{}",
                entity.getName().getString(),
                mana.getMana(),
                mana.getMaxMana());
        entity.setData(ModAttachments.MANA.get(), mana);
    }
}
