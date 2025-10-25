package com.minagic.minagic.wizard;

import com.minagic.minagic.abstractionLayer.SpellEditorScreen;
import com.minagic.minagic.abstractionLayer.SpellcastingItem;
import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.gui.StaffEditorScreen;
import com.minagic.minagic.gui.WizardWandEditorScreen;
import com.minagic.minagic.registries.ModDataComponents;
import com.minagic.minagic.sorcerer.StaffData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class WizardWand extends SpellcastingItem<WizardWandData> {
    public WizardWand(Properties properties) {
        super(properties, ModDataComponents.WIZARD_WAND_DATA.get(), WizardWandData::new);
    }
    @Override
    public boolean canPlayerClassUseSpellcastingItem(PlayerClass playerClass) {
        return playerClass.getMainClass() == PlayerClassEnum.WIZARD;
    }

    @Override
    public SpellEditorScreen<WizardWandData> getEditorScreen(Player player, ItemStack stack) {
        System.out.println("Opening WIZARD Editor Screen via WIZARDWAND");
        return new WizardWandEditorScreen(player, this, stack);
    }

}
