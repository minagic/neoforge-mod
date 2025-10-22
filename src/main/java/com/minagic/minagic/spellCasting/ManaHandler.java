package com.minagic.minagic.spellCasting;

import com.minagic.minagic.capabilities.Mana;
import com.minagic.minagic.capabilities.PlayerSpellCooldowns;
import com.minagic.minagic.registries.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class ManaHandler {
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        Mana mana = serverPlayer.getData(ModAttachments.MANA.get());
        mana.tick(serverPlayer);
        serverPlayer.setData(ModAttachments.MANA.get(), mana);
    }
}
