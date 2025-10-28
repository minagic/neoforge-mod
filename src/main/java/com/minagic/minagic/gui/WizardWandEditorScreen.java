package com.minagic.minagic.gui;

import com.minagic.minagic.abstractionLayer.gui.SpellEditorScreen;
import com.minagic.minagic.abstractionLayer.SpellcastingItem;
import com.minagic.minagic.wizard.WizardWandData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class WizardWandEditorScreen extends SpellEditorScreen<WizardWandData> {
    public WizardWandEditorScreen(Player player, SpellcastingItem<WizardWandData> item, ItemStack stack) {
        super(player, item, stack);
    }
}
