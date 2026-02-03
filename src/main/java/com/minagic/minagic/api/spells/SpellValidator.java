package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.SpellMetadata;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertAttachment;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.SpellValidationResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpellValidator {

    public static SpellValidationResult validateCooldown(Spell spell, SpellCastContext context) {
        var cooldowns = context.caster.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get());
        if (cooldowns.getCooldown(ModSpells.getId(spell)) > 0) {
            return SpellValidationResult.playerFail("Spell is on cooldown!");
        }
        return SpellValidationResult.OK;
    }

    public static SpellValidationResult validateMana(Spell spell, SpellCastContext context, int manaCost) {
        var mana = context.caster.getData(ModAttachments.MANA.get());
        if (mana.getMana() < manaCost) {
            return SpellValidationResult.playerFail("Not enough mana to cast " + spell.getString() + ".");
        }
        return SpellValidationResult.OK;
    }

    public static SpellValidationResult validateSimulacrum(@Nullable SimulacrumData simulacrumData) {
        if (simulacrumData == null) {
            return SpellValidationResult.internalFail("Simulacrum data is null");
        }
        if (simulacrumData.remainingLifetime() == 0) {
            return SpellValidationResult.internalFail("Simulacrum expired.");
        }
        return SpellValidationResult.OK;
    }

    public static SpellValidationResult validateMetadata(Spell spell, SpellCastContext context, List<String> keys) {
        for (String key : keys) {
            if (SpellMetadata.has(context.target, spell, key)) {
                return SpellValidationResult.internalFail("Missing metadata: " + key);
            }
        }
        return SpellValidationResult.OK;
    }

    public static SpellValidationResult validateItem(Spell spell, SpellCastContext context) {
        return SpellValidationResult.OK; // placeholder hook
    }

    public static void showFailureIfNeeded(SpellCastContext context, SpellValidationResult result) {
        if (!result.success() && result.showToPlayer()) {
            HudAlertAttachment.addToEntity(
                    context.caster,
                    result.failureMessage(),
                    0xFF555500,
                    1,
                    20
            );
        }
    }

    public enum CastFailureReason {
        CASTER_CLASS_MISMATCH,
        CASTER_SUBCLASS_MISMATCH,
        CASTER_CLASS_LEVEL_TOO_LOW,
        OK
    }
}
