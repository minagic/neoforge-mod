package com.minagic.minagic.capabilities;

import com.mojang.serialization.Codec;

public enum Deity {
    // Cleric Deities (mythological)
    THOR(DeityType.CLERIC),
    LOKI(DeityType.CLERIC),
    THOTH(DeityType.CLERIC),
    ANUBIS(DeityType.CLERIC),
    ATHENA(DeityType.CLERIC),
    HERMES(DeityType.CLERIC),
    PERUN(DeityType.CLERIC),
    MOKOSH(DeityType.CLERIC),
    INANNA(DeityType.CLERIC),
    ENKI(DeityType.CLERIC),

    // Warlock Deities (fantastical/cosmic)
    ASTRAEL(DeityType.WARLOCK),
    SOLNAR(DeityType.WARLOCK),
    NERTHUL(DeityType.WARLOCK),
    XURTHAN(DeityType.WARLOCK),
    ZYTHOR(DeityType.WARLOCK),
    VORATH(DeityType.WARLOCK),
    IMRAHN_DURN(DeityType.WARLOCK),
    LILITH(DeityType.WARLOCK),
    SYLVARIS(DeityType.WARLOCK),
    ELYNDRA(DeityType.WARLOCK),

    // Fallback
    UNDECLARED(DeityType.NONE);

    public static final Codec<Deity> CODEC = Codec.STRING.xmap(Deity::valueOf, Deity::name);

    private final DeityType type;

    Deity(DeityType type) {
        this.type = type;
    }

    public boolean isCleric() {
        return type == DeityType.CLERIC ;
    }

    public boolean isWarlock() {
        return type == DeityType.WARLOCK;
    }

    public DeityType getType() {
        return type;
    }

    public enum DeityType {
        CLERIC,
        WARLOCK,
        NONE
    }
}
