package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.abstractionLayer.spells.ChanneledSpell;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.world.entity.LivingEntity;

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
    public CastFailureReason canCast(SpellCastContext context) {
        // voidbourne sorcerers of level 20 only
        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getMainClass() != PlayerClassEnum.SORCERER) {
            return CastFailureReason.CASTER_CLASS_MISMATCH;
        }

        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE) == 0) {
            return CastFailureReason.CASTER_SUBCLASS_MISMATCH;
        }

        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE) < 20) {
            return CastFailureReason.CASTER_CLASS_LEVEL_TOO_LOW;
        }
        return CastFailureReason.OK;
    }

    @Override
    public void cast(SpellCastContext context) {
        System.out.println("[EventHorizon] cast called for spell: " + getString());
    }

}
