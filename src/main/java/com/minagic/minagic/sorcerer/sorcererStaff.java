package com.minagic.minagic.sorcerer;

import com.minagic.minagic.abstractionLayer.SpellcastingItem;
import com.minagic.minagic.abstractionLayer.SpellcastingItemData;
import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.spellCasting.ISpellcastingItem;
import com.minagic.minagic.registries.ModDataComponents;
import com.minagic.minagic.spellCasting.SpellSlot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public class sorcererStaff extends SpellcastingItem<StaffData> implements ISpellcastingItem {
        public sorcererStaff(Properties properties) {
            super(properties, ModDataComponents.STAFF_DATA.get(), StaffData::new);
        }

        @Override
        public boolean canPlayerClassUseSpellcastingItem(PlayerClass playerClass) {
            return playerClass.getMainClass() == PlayerClassEnum.SORCERER;
        }
}