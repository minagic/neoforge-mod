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

    public static final Codec<PlayerClassEnum> CODEC = Codec.STRING.xmap(PlayerClassEnum::valueOf, PlayerClassEnum::name);


}
