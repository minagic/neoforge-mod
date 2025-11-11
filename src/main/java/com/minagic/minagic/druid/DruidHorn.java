package com.minagic.minagic.druid;

import com.minagic.minagic.abstractionLayer.SpellcastingItem;
import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.registries.ModDataComponents;
import com.minagic.minagic.sorcerer.StaffData;

public class DruidHorn extends SpellcastingItem<HornData> {
    public DruidHorn(Properties properties) {
        super(properties, ModDataComponents.DRUID_HORN_DATA.get(), HornData::new);
    }

    @Override
    public boolean canPlayerClassUseSpellcastingItem(PlayerClass playerClass) {
        return playerClass.getMainClass() == PlayerClassEnum.DRUID;
    }
}
