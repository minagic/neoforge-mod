package com.minagic.minagic.sorcerer;

import com.minagic.minagic.api.SpellcastingItem;
import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.registries.ModDataComponents;

public class sorcererStaff extends SpellcastingItem<StaffData> {
    public sorcererStaff(Properties properties) {
        super(properties, ModDataComponents.STAFF_DATA.get(), StaffData::new);
    }

    @Override
    public boolean canPlayerClassUseSpellcastingItem(PlayerClass playerClass) {
        return playerClass.getMainClass() == PlayerClassEnum.SORCERER;
    }
}