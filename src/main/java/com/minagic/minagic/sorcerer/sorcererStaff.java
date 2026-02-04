package com.minagic.minagic.sorcerer;

import com.minagic.minagic.api.SpellcastingItem;
import com.minagic.minagic.capabilities.MagicClass;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerClassEnum;
import com.minagic.minagic.registries.ModDataComponents;
import net.minecraft.world.entity.LivingEntity;

public class sorcererStaff extends SpellcastingItem<StaffData> {
    public sorcererStaff(Properties properties) {
        super(properties, ModDataComponents.STAFF_DATA.get(), StaffData::new);
    }

    @Override
    public boolean canLivingUseSpellcastingItem(LivingEntity player) {
        return MagicClass.getMainClass(player) == PlayerClassEnum.SORCERER;
    }
}
