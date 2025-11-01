package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.abstractionLayer.spells.ChanneledSpell;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.world.entity.LivingEntity;

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
    public boolean canCast(SpellCastContext context) {
        // voidbourne sorcerers of level 20 only
        return context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE) >= 20;
    }

    @Override
    public void cast(SpellCastContext context) {
        LivingEntity player = preCast(context);
        if (player == null) {
            return; // Pre-cast checks failed
        }

        System.out.println("[EventHorizon] cast called for spell: " + getString());
        applyMagicCosts(context);
    }

}
