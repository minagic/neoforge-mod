package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class ChanneledSpell extends Spell {

    @Override
    public final int getMaxLifetime() {
        return -1; // Channeled spells have no max lifetime
    }


    // Lifecycle methods
    @Override
    public final void onStart(SpellCastContext context) {
        System.out.println("[ChanneledSpell] onStart called for spell: " + getString());
        LivingEntity player = preCast(context, true);
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

    @Override
    public final void tick(SpellCastContext context) {
        // no-op for channeled spells
    }

    public final void onStop(SpellCastContext context) {
        LivingEntity player = preCast(context);
        if (player == null) {
            return; // Pre-cast checks failed
        }
        var data = player.getData(ModAttachments.PLAYER_SIMULACRA);
        data.clearChanneling();
        player.setData(ModAttachments.PLAYER_SIMULACRA, data);
    }




}
