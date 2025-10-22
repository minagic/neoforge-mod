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

    public static final Codec<PlayerClassEnum> CODEC = Codec.STRING.xmap(PlayerClassEnum::valueOf, PlayerClassEnum::name);
}
