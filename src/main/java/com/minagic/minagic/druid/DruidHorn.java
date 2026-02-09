package com.minagic.minagic.druid;

import com.minagic.minagic.api.SpellcastingItem;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.MagicClass;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerClassEnum;
import com.minagic.minagic.registries.ModDataComponents;

public class DruidHorn extends SpellcastingItem<HornData> {
    public DruidHorn(Properties properties) {
        super(properties, ModDataComponents.DRUID_HORN_DATA.get(), HornData::new);
    }

    @Override
    public boolean canCastSpell(Spell spell) {
        return false;
    }
}
