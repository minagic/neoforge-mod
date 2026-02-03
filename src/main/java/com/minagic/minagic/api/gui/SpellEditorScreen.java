package com.minagic.minagic.api.gui;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.SpellcastingItem;
import com.minagic.minagic.api.SpellcastingItemData;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.spellCasting.SpellRegistry;
import com.minagic.minagic.spellCasting.spellslots.SpellSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SpellEditorScreen<T extends SpellcastingItemData> extends AbstractContainerScreen<AbstractContainerMenu> {
    protected final Player player;
    protected final ItemStack stack;
    protected final SpellcastingItem<T> item;
    protected final T data;

    public SpellEditorScreen(Player player, SpellcastingItem<T> item, ItemStack stack) {
        super(new MerchantMenu(0, player.getInventory()), player.getInventory(), item.getName(stack));

        this.player = player;
        this.item = item;
        this.stack = stack;

        Minagic.LOGGER.debug("Initializing SpellEditorScreen for {}", item.getClass());
        this.data = item.getData(stack); // Pull existing or default data
        Minagic.LOGGER.trace("Loaded spellcasting data {}", data.getClass());
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

    protected List<Spell> getAvailableSpells(Player player, ItemStack stack) {
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
        List<Spell> available = getAvailableSpells(player, stack);

        Minecraft.getInstance().setScreen(new SpellSelectionScreen(available, selected -> {
            Minagic.LOGGER.debug("Selected spell {} for slot {}", selected.getString(), index);
            Minagic.LOGGER.trace("Inscribing the spell into the staff");
            assert Minecraft.getInstance().level != null;
            item.writeSpell(stack, Minecraft.getInstance().level, Minecraft.getInstance().player, index, selected);
        }));
    }
}
