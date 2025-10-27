package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.abstractionLayer.ChanneledSpell;
import com.minagic.minagic.abstractionLayer.Spell;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class EventHorizon extends ChanneledSpell {
    @Override
    public String getString() {
        return "Event Horizon";
    }

    @Override
    public int getManaCost() {
        return 100;
    }

    @Override
    public int getSimulacrumThreshold(){
        return 100;
    }

    @Override
    public int getCooldownTicks() {
        return 10;
    }

    @Override
    public String canCast(SpellCastContext context) {
        // voidbourne sorcerers of level 20 only
        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE) < 20) {
            return "Only Voidbourne Sorcerers of level 20 can cast Event Horizon.";
        }
        return "";
    }

    @Override
    public void cast(SpellCastContext context) {
        ServerPlayer player = preCast(context);
        if (player == null) {
            return; // Pre-cast checks failed
        }
        player.sendSystemMessage(Component.literal("You think I literally implemented a black hole?"));
        applyMagicCosts(context);
    }

}
