package com.minagic.minagic.spellCasting;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.CooldownAttachment;
import com.minagic.minagic.registries.ModAttachments;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class SpellCooldownHandler {

    @SubscribeEvent
    public void onPlayerTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        CooldownAttachment.tick(entity);
    }
}
