package com.minagic.minagic.sorcerer;

import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerClassSorcererTest {

    @Test
    void sorcererSubclassAcceptedWhenMainClassMatches() {
        PlayerClass playerClass = new PlayerClass();
        playerClass.setMainClass(PlayerClassEnum.SORCERER);

        assertTrue(playerClass.setSubclassLevel(PlayerSubClassEnum.SORCERER_CELESTIAL, 5));
        assertEquals(5, playerClass.getSubclassLevel(PlayerSubClassEnum.SORCERER_CELESTIAL));
    }

    @Test
    void sorcererSubclassRejectedWhenMainClassDiffers() {
        PlayerClass playerClass = new PlayerClass();
        playerClass.setMainClass(PlayerClassEnum.WIZARD);

        assertFalse(playerClass.setSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE, 3));
        assertEquals(0, playerClass.getSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE));
    }

    @Test
    void sorcererSubclassClearedWhenChangingMainClass() {
        PlayerClass playerClass = new PlayerClass();
        playerClass.setMainClass(PlayerClassEnum.SORCERER);
        assertTrue(playerClass.setSubclassLevel(PlayerSubClassEnum.SORCERER_INFERNAL, 2));

        playerClass.setMainClass(PlayerClassEnum.CLERIC);

        assertEquals(0, playerClass.getSubclassLevel(PlayerSubClassEnum.SORCERER_INFERNAL));
        assertTrue(playerClass.getAllSubclasses().isEmpty(), "Changing main class should clear incompatible subclasses");
    }
}
