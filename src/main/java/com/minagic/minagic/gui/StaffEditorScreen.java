package com.minagic.minagic.gui;

import com.minagic.minagic.abstractionLayer.gui.SpellEditorScreen;
import com.minagic.minagic.abstractionLayer.SpellcastingItem;
import com.minagic.minagic.sorcerer.StaffData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class StaffEditorScreen extends SpellEditorScreen<StaffData> {
    public StaffEditorScreen(Player player, SpellcastingItem<StaffData> item, ItemStack stack) {
        super(player, item, stack);
    }
}
