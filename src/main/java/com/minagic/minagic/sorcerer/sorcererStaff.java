package com.minagic.minagic.sorcerer;

import com.minagic.minagic.api.SpellcastingItem;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.MagicClass;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerClassEnum;
import com.minagic.minagic.capabilities.powersource.SorceryPowerSourceAttachment;
import com.minagic.minagic.registries.ModDataComponents;

public class sorcererStaff extends SpellcastingItem<StaffData> {
    public sorcererStaff(Properties properties) {
        super(properties, ModDataComponents.STAFF_DATA.get(), StaffData::new);
    }

    @Override
    public boolean canCastSpell(Spell spell) {
        return spell instanceof SorceryPowerSourceAttachment.ISorcerySpell;
    }
}
