package com.minagic.minagic.sorcerer;

import com.minagic.minagic.capabilities.MagicClass;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerClassEnum;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerSubClassEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerClassSorcererTest {

    @Test
    void sorcererSubclassAcceptedWhenMainClassMatches() {
        MagicClass magicClass = new MagicClass();
        magicClass.setMainClass(PlayerClassEnum.SORCERER);

        assertTrue(magicClass.setSubclassLevel(PlayerSubClassEnum.SORCERER_CELESTIAL, 5));
        assertEquals(5, magicClass.getSubclassLevel(PlayerSubClassEnum.SORCERER_CELESTIAL));
    }

    @Test
    void sorcererSubclassRejectedWhenMainClassDiffers() {
        MagicClass magicClass = new MagicClass();
        magicClass.setMainClass(PlayerClassEnum.WIZARD);

        assertFalse(magicClass.setSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE, 3));
        assertEquals(0, magicClass.getSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE));
    }

    @Test
    void sorcererSubclassClearedWhenChangingMainClass() {
        MagicClass magicClass = new MagicClass();
        magicClass.setMainClass(PlayerClassEnum.SORCERER);
        assertTrue(magicClass.setSubclassLevel(PlayerSubClassEnum.SORCERER_INFERNAL, 2));

        magicClass.setMainClass(PlayerClassEnum.CLERIC);

        assertEquals(0, magicClass.getSubclassLevel(PlayerSubClassEnum.SORCERER_INFERNAL));
        assertTrue(magicClass.getAllSubclasses().isEmpty(), "Changing main class should clear incompatible subclasses");
    }
}
