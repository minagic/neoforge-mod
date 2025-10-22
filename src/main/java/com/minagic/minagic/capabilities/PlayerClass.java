package com.minagic.minagic.capabilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class PlayerClass {
    PlayerClassEnum playerClass;

    public PlayerClass() {
        this.playerClass = PlayerClassEnum.UNDECLARED;
    }

    public PlayerClassEnum getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(PlayerClassEnum playerClass) {
        this.playerClass = playerClass;
    }

    // --- CODEC ---

    public static final Codec<PlayerClass> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlayerClassEnum.CODEC.fieldOf("playerClass").forGetter(PlayerClass::getPlayerClass)
    ).apply(instance, playerClassEnum -> {
        PlayerClass pc = new PlayerClass();
        pc.setPlayerClass(playerClassEnum);
        return pc;
    }));

}
