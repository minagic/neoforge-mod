package com.minagic.minagic.spellCasting;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.ManaAttachment;
import com.minagic.minagic.registries.ModAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class ManaHandler {
    @SubscribeEvent
    public void onPlayerTick(EntityTickEvent.Pre event) {
        LivingEntity entity = event.getEntity().asLivingEntity();
        if (entity == null || entity.level().isClientSide()) return;

        ManaAttachment manaAttachement = entity.getData(ModAttachments.MANA.get());
        manaAttachement.tick(entity);
        Minagic.LOGGER.trace("[Minagic] ManaAttachment tick for {} -> {}/{}",
                entity.getName().getString(),
                manaAttachement.getMana(),
                manaAttachement.getMaxMana());
        entity.setData(ModAttachments.MANA.get(), manaAttachement);
    }
}
