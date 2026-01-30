package com.minagic.minagic.wizard;

import com.minagic.minagic.api.SpellcastingItem;
import com.minagic.minagic.api.gui.SpellEditorScreen;
import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.gui.WizardWandEditorScreen;
import com.minagic.minagic.registries.ModDataComponents;
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
        return new WizardWandEditorScreen(player, this, stack);
    }

}
