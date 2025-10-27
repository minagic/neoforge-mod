package com.minagic.minagic.abstractionLayer;

import com.minagic.minagic.capabilities.Mana;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SimulacrumSpellSlot;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;

public class ChanneledSpell extends Spell{

    @Override
    public void onStart(SpellCastContext context) {
        System.out.println("[ChanneledSpell] onStart called for spell: " + getString());
        ServerPlayer player = preCast(context, true);
        if (player == null) {
            return; // Pre-cast checks failed
        }
        System.out.println("[ChanneledSpell] onStart passed preCast check, prerequisites output " +( magicPrerequisitesHelper(context) == "" ? "no output" : magicPrerequisitesHelper(context))+  " for spell " + getString());
        var data = player.getData(ModAttachments.PLAYER_SIMULACRA.get());
        if (data.getActiveChanneling()!=null && ModSpells.getId(data.getActiveChanneling().getSpell()) != ModSpells.getId(this)) {
            System.out.println("[ChanneledSpell] onStart found existing different channelling spell, clearing it for spell: " + getString());
            data.setActiveChanneling(this, getSimulacrumThreshold(), -1, context.stack);
        }
        else if (data.getActiveChanneling()==null) {
            data.setActiveChanneling(this, getSimulacrumThreshold(), -1, context.stack);
        }
        player.setData(ModAttachments.PLAYER_SIMULACRA.get(), data);

    }

    public void onStop(SpellCastContext context) {
        System.out.println("[ChanneledSpell] onStop called for spell: " + getString());;
        ServerPlayer player = preCast(context);
        if (player == null) {
            return; // Pre-cast checks failed
        }
        var data = player.getData(ModAttachments.PLAYER_SIMULACRA.get());
        data.clearChanneling();
    }


}
