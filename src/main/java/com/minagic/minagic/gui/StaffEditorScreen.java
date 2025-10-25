package com.minagic.minagic.gui;

import com.minagic.minagic.abstractionLayer.Spell;
import com.minagic.minagic.abstractionLayer.SpellEditorScreen;
import com.minagic.minagic.abstractionLayer.SpellSelectionScreen;
import com.minagic.minagic.abstractionLayer.SpellcastingItem;
import com.minagic.minagic.sorcerer.StaffData;
import com.minagic.minagic.spellCasting.SpellSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class StaffEditorScreen extends SpellEditorScreen<StaffData> {
    public StaffEditorScreen(Player player, SpellcastingItem<StaffData> item, ItemStack stack) {
        super(player, item, stack);
    }
}
