package com.minagic.minagic.capabilities;

import com.mojang.serialization.Codec;

public enum PlayerClassEnum {
    WIZARD,
    SORCERER,
    WARLOCK,
    CLERIC,
    DRUID,
    BARD,
    BLADESINGER,
    MONK,
    HERBALIST,
    ALCHEMIST,
    RUNE_ARTIFICER,
    SHAMAN,
    UNDECLARED;

    public final String getUnknownSpellMessage() {
        return switch (this) {
            case WIZARD -> "Arcane pattern unrecognized.";
            case SORCERER -> "Your blood rejects this spell.";
            case WARLOCK -> "Patron denies this power.";
            case CLERIC -> "Your deity forbids this magic.";
            case DRUID -> "Nature spurns this spell.";
            case BARD -> "You cannot recall this melody.";
            case BLADESINGER -> "You lack the rhythm of this form.";
            case MONK -> "Your ki cannot shape this art.";
            case HERBALIST -> "These essences elude your touch.";
            case ALCHEMIST -> "Formula unbalanced — reaction fails.";
            case RUNE_ARTIFICER -> "Runic pattern not bound to your craft.";
            case SHAMAN -> "The spirits do not answer this call.";
            case UNDECLARED -> "You lack the knowledge to cast this spell.";
        };
    }

    public final String getSubclassMismatchMessage() {
        return switch (this) {
            case WIZARD -> "This school lies beyond your study.";
            case SORCERER -> "Your lineage bears no trace of this power.";
            case WARLOCK -> "Your pact grants no claim to this magic.";
            case CLERIC -> "Your domain holds no sway here.";
            case DRUID -> "This circle does not dance with such forces.";
            case BARD -> "That tune belongs to another college.";
            case BLADESINGER -> "Your voice is not aligned with this discipline.";
            case MONK -> "Your way rejects this technique.";
            case HERBALIST -> "Those herbs belong to another garden.";
            case ALCHEMIST -> "Your specialty lacks the reagents for this craft.";
            case RUNE_ARTIFICER -> "Your codex rejects these sigils.";
            case SHAMAN -> "The totems you know are silent to this rite.";
            case UNDECLARED -> "You have not yet chosen your path.";
        };
    }

    public final String getLevelTooLowMessage() {
        return switch (this) {
            case WIZARD -> "Your mastery is insufficient for this incantation.";
            case SORCERER -> "Your essence burns too dim for such might.";
            case WARLOCK -> "Your patron withholds this boon — grow stronger.";
            case CLERIC -> "Your faith has not yet earned this blessing.";
            case DRUID -> "Your bond with nature is not yet deep enough.";
            case BARD -> "Your voice falters at the complexity of this song.";
            case BLADESINGER -> "Your blade-dance lacks the precision required.";
            case MONK -> "Your spirit has not yet reached that harmony.";
            case HERBALIST -> "Your hands tremble at such potent mixtures.";
            case ALCHEMIST -> "Your knowledge of reactions remains too basic.";
            case RUNE_ARTIFICER -> "You lack the attunement to carve such runes.";
            case SHAMAN -> "The great spirits deem you unready.";
            case UNDECLARED -> "You lack the foundation to attempt this power.";
        };
    }


    public static final Codec<PlayerClassEnum> CODEC = Codec.STRING.xmap(PlayerClassEnum::valueOf, PlayerClassEnum::name);


}
