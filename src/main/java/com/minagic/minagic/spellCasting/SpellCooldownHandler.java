package com.minagic.minagic.spellCasting;

import com.minagic.minagic.capabilities.PlayerSpellCooldowns;
import com.minagic.minagic.registries.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class SpellCooldownHandler {

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        PlayerSpellCooldowns cd = serverPlayer.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get());
        cd.tick();
        AttachmentType<PlayerSpellCooldowns> type = ModAttachments.PLAYER_SPELL_COOLDOWNS.get();
        if (type == null) {
            System.out.println("PLAYER_SPELL_COOLDOWNS type is NULL — attachment not registered yet!");
            return;
        }
        serverPlayer.setData(type, cd);
    }
}
