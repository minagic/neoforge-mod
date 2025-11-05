package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class ChargedSpell extends Spell {
    private int chargeTime = 0;

    protected final int getChargeTime() {
        return chargeTime;
    }

    @Override
    public int getMaxLifetime() {
        return 100; // Default max lifetime for charged spells
    }

    @Override
    public final int getSimulacrumThreshold() {
        return 0; // Charged spells cannot be automatically cast
    }

    @Override
    public String getString() {
        return "Charged Spell";
    }

    @Override
    public int getManaCost() {
        return 10; // Default mana cost for charged spells
    }

    // Lifecycle methods
    @Override
    public void onStart(SpellCastContext context) {
        LivingEntity player = preCast(context, true, true, false);
        if (player == null) {
            return; // Pre-cast checks failed
        }

        PlayerSimulacraAttachment data =  player.getData(ModAttachments.PLAYER_SIMULACRA);
        data.setActiveChanneling(this, getSimulacrumThreshold(), getMaxLifetime(), context.stack);
        player.setData(ModAttachments.PLAYER_SIMULACRA.get(), data);
    }

    @Override
    public void tick(SpellCastContext context) {
        chargeTime = context.simulacrtumLifetime == -1 ? chargeTime + 1 : context.simulacrtumLifetime;
    }

    @Override
    public final void onStop(SpellCastContext context) {
        LivingEntity player = preCast(context, true, false, false);
        if (player == null) {
            return; // Pre-cast checks failed
        }

        PlayerSimulacraAttachment data =  player.getData(ModAttachments.PLAYER_SIMULACRA);
        data.clearChanneling();
        player.setData(ModAttachments.PLAYER_SIMULACRA.get(), data);

        cast(context);
    }


}
