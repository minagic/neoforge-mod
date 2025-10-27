package com.minagic.minagic.spellCasting;

import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.registries.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class PlayerSimulacraHandler {
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        var sim = player.getData(ModAttachments.PLAYER_SIMULACRA.get());
        sim.tick(player, player.level());
        player.setData(ModAttachments.PLAYER_SIMULACRA.get(), sim);
        // Re-apply *only* when actually changed (or every N ticks)
        //player.setData(ModAttachments.PLAYER_SIMULACRA.get(), PlayerSimulacraAttachment.copy(sim));
    }

//    @SubscribeEvent
//    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
//        if (!(event.getEntity() instanceof ServerPlayer player)) return;
//
//        PlayerSimulacraAttachment simulacra = player.getData(ModAttachments.PLAYER_SIMULACRA.get());
//        if (simulacra == null) return;
//
//        // Clear all active simulacra to avoid memory leaks or ghost ticking
//        simulacra.clearBackgroundSimulacrum();
//        simulacra.clearActiveChannelling();
//
//        // Push the updated (empty) attachment back
//        player.setData(ModAttachments.PLAYER_SIMULACRA.get(), simulacra);
//
//        System.out.println("[Minagic] Cleared player simulacra for logout: " + player.getName().getString());
//    }

//    @SubscribeEvent
//    public static void onPlayerRespawn(PlayerEvent.Clone event) {
//        if (!(event.getEntity() instanceof ServerPlayer player)) return;
//        // Optional: clear simulacra on death (when cloning)
//        player.getData(ModAttachments.PLAYER_SIMULACRA.get()).getBackgroundSimulacra().clear();
//        player.getData(ModAttachments.PLAYER_SIMULACRA.get()).clearActiveChannelling();
//    }
}
