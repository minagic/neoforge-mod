package com.minagic.minagic.gui;

import com.minagic.minagic.abstractionLayer.Spell;
import com.minagic.minagic.abstractionLayer.SpellEditorScreen;
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

    @Override
    protected void addSlotButton(int index) {
        int x = this.leftPos + 10 + (index % 5) * 20;
        int y = this.topPos + 20 + (index / 5) * 20;
        SpellSlot slot = data.getSlots().get(index);
        String spellName = (slot != null && slot.getSpell() != null)
                ? slot.getSpell().getString()
                : "None";


        var btn = Button.builder(Component.literal("S"), button -> openSpellSelection(index))
                .tooltip(Tooltip.create(Component.literal("Select Spell for Slot " + (index + 1) + " Current Spell: " + spellName)))
                .pos(x, y)
                .size(18, 18)
                .build();
        this.addRenderableWidget(btn);
    }

    private void openSpellSelection(int index) {
        List<Spell> available = getAvailableSpells(player);

        Minecraft.getInstance().setScreen(new SpellSelectionScreen(available, selected -> {
            System.out.println("Selected spell: " + selected.getString() + " for slot " + index);
            System.out.println("Inscribing the spell into the staff.");
            item.writeSpell(stack, Minecraft.getInstance().level, index, selected);
        }));
    }
}
