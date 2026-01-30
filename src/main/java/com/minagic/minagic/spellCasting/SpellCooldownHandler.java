package com.minagic.minagic.spellCasting;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.PlayerSpellCooldowns;
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
        PlayerSpellCooldowns cd = entity.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get());
        cd.tick();
        AttachmentType<PlayerSpellCooldowns> type = ModAttachments.PLAYER_SPELL_COOLDOWNS.get();
        if (type == null) {
            Minagic.LOGGER.warn("PLAYER_SPELL_COOLDOWNS attachment type is not registered yet");
            return;
        }
        entity.setData(type, cd);
    }
}
