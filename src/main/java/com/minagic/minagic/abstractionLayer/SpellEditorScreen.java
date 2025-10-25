package com.minagic.minagic.abstractionLayer;

import com.minagic.minagic.spellCasting.SpellRegistry;
import com.minagic.minagic.spellCasting.SpellSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.InputEvent;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SpellEditorScreen<T extends SpellcastingItemData> extends AbstractContainerScreen<AbstractContainerMenu> {
    protected final Player player;
    protected final ItemStack stack;
    protected final SpellcastingItem<T> item;
    protected T data;

    public SpellEditorScreen(Player player, SpellcastingItem<T> item, ItemStack stack) {
        super(new LoomMenu(0, player.getInventory()), player.getInventory(), item.getName(stack));

        this.player = player;
        this.item = item;
        this.stack = stack;

        System.out.println("Initializing SpellEditorScreen with "+item.getClass());
        this.data = item.getData(stack); // Pull existing or default data
        System.out.println("Initializing SpellEditorScreen with "+data.getClass());
    }

    @Override
    protected void init() {
        super.init();

        // Render spell slots from data.getSlots()
        List<SpellSlot> slots = data.getSlots();
        for (int i = 0; i < slots.size(); i++) {
            addSlotButton(i); // you'll define this method
        }
    }

    protected void addSlotButton(int index){
        // Placeholder: Override in subclasses to add buttons for each spell slot
    }

    protected List<Spell> getAvailableSpells(Player player){
        return SpellRegistry.getSpells(player);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean handled) {
        return super.mouseClicked(event, handled);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.fill(0, 0, this.width, this.height, 0x20202020); // simple dark background
    }
}
