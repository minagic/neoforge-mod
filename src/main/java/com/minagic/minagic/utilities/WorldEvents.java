package com.minagic.minagic.utilities;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellcastingItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

public final class WorldEvents {
    public WorldEvents() {
    }

    @SubscribeEvent
    public void onWorldLoad(PlayerEvent.PlayerLoggedInEvent event) {
        //
    }
}