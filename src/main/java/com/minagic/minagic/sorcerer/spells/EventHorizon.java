package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.api.spells.ChanneledSpell;
import com.minagic.minagic.api.spells.SpellValidator;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;

public class EventHorizon extends ChanneledSpell {
    public EventHorizon() {
        super();

        this.spellName = "Event Horizon";
        this.manaCost = 100;
        this.simulacraThreshold = 100;
        this.cooldown = 10;
        // simulacraMaxLifetime left to superclass default
    }

    @Override
    public SpellValidator.CastFailureReason canCast(SpellCastContext context) {
        // voidbourne sorcerers of level 20 only
        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getMainClass() != PlayerClassEnum.SORCERER) {
            return SpellValidator.CastFailureReason.CASTER_CLASS_MISMATCH;
        }

        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE) == 0) {
            return SpellValidator.CastFailureReason.CASTER_SUBCLASS_MISMATCH;
        }

        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE) < 20) {
            return SpellValidator.CastFailureReason.CASTER_CLASS_LEVEL_TOO_LOW;
        }
        return SpellValidator.CastFailureReason.OK;
    }

    @Override
    public void cast(SpellCastContext context, SimulacrumSpellData simulacrumData) {
        System.out.println("[EventHorizon] cast called for spell: " + getString());
    }

}
