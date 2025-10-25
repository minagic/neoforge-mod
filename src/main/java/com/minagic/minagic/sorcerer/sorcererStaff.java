package com.minagic.minagic.sorcerer;

import com.minagic.minagic.abstractionLayer.SpellEditorScreen;
import com.minagic.minagic.abstractionLayer.SpellcastingItem;
import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.gui.StaffEditorScreen;
import com.minagic.minagic.registries.ModDataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class sorcererStaff extends SpellcastingItem<StaffData> {
        public sorcererStaff(Properties properties) {
            super(properties, ModDataComponents.STAFF_DATA.get(), StaffData::new);
        }

        @Override
        public boolean canPlayerClassUseSpellcastingItem(PlayerClass playerClass) {
            return playerClass.getMainClass() == PlayerClassEnum.SORCERER;
        }
        // override screen method
//        @Override
//        public SpellEditorScreen<StaffData> getEditorScreen(Player player, ItemStack stack) {
//            System.out.println("Opening Staff Editor Screen via sorcererStaff");
//            return new StaffEditorScreen(player, this, stack);
//        }
}