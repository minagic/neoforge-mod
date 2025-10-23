//package com.minagic.minagic.gui;
//
//import com.minagic.minagic.spellCasting.SpellSlot;
//import com.minagic.minagic.spells.ISpell;
//import com.minagic.minagic.registries.ModDataComponents;
//import com.mojang.blaze3d.systems.RenderSystem;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.components.CycleButton;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.Mth;
//import com.minagic.minagic.sorcerer.StaffData;
//import com.minagic.minagic.registries.ModSpells;
//import com.minagic.minagic.sorcerer.sorcererStaff;
//
//import java.util.Arrays;
//
//import com.mojang.blaze3d.systems.RenderSystem;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.components.Button;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.Mth;
//import com.minagic.minagic.registries.ModAttachments;
//import net.minecraft.world.item.ItemStack;
//
//import java.util.*;
//
//public class StaffSpellScreen extends Screen {
//    private final Minecraft mc = Minecraft.getInstance();
//    private final StaffData originalData;
//    private final sorcererStaff staffItem;
//    private final ItemStack stack;
//    private final SpellSlot[] workingSlots;
//    private final List<Integer> selectedSpellIndices = new ArrayList<>();
//    private final List<CycleButton<ISpell>> spellSelectors = new ArrayList<>();
//
//    private static final int BOX_WIDTH = 100;
//    private static final int BOX_HEIGHT = 20;
//    private static final int PADDING = 5;
//
//    // "None" option to clear a slot
//    private static final Component NONE_LABEL = Component.literal("Not Assigned");
//
//    // Callback interface
//    public interface SubmitCallback {
//
//    }
//
//    public StaffSpellScreen(sorcererStaff staffItem, ItemStack stack) {
//        super(Component.literal("Manage Staff Spells"));
//        this.originalData = stack.get(ModDataComponents.STAFF_DATA);
//        this.workingSlots = Arrays.copyOf(originalData.slots(), originalData.slots().length);
//        this.staffItem = staffItem;
//        this.stack = stack;
//
//        // Read known spells from player's capability
//
//        // Add "null" option to clear slot
//        //this.knownSpells.add(0, );
//
//        // Initialize selected indices
//        for (SpellSlot slot : workingSlots) {
//            ResourceLocation spellId = slot.getSpellId();
//            ISpell current = spellId != null ? ModSpells.get(spellId) : null;
//            int index = knownSpells.indexOf(current);
//            selectedSpellIndices.add(index >= 0 ? index : 0); // fallback to "none"
//        }
//    }
//
//    @Override
//    protected void init() {
//        int startY = height / 4;
//        int centerX = width / 2;
//
//        spellSelectors.clear();
//
//        for (int i = 0; i < workingSlots.length; i++) {
//            final int slotIndex = i;
//            int y = startY + i * (BOX_HEIGHT + PADDING);
//
//            // Current assigned spell (nullable)
//            ISpell current = null;
//            ResourceLocation spellId = workingSlots[i].getSpellId();
//            if (spellId != null) current = ModSpells.get(spellId);
//
//            CycleButton<ISpell> selector = CycleButton.<ISpell>builder(spell -> {
//                        if (spell == null) return NONE_LABEL;
//                        ResourceLocation id = ModSpells.getId(spell);
//                        return Component.literal(id != null ? id.getPath() : "???");
//                    })
//                    .displayOnlyValue()
//                    .withValues(knownSpells)
//                    .withInitialValue(current)
//                    .create(centerX - BOX_WIDTH / 2, y, BOX_WIDTH, BOX_HEIGHT, Component.empty());
//
//            this.addRenderableWidget(selector);
//            spellSelectors.add(selector);
//        }
//
//        // Submit Button
//        addRenderableWidget(Button.builder(Component.literal("Submit"), btn -> {
//            for (int i = 0; i < spellSelectors.size(); i++) {
//                ISpell selected = spellSelectors.get(i).getValue();
//                staffItem.writeSpell(stack, mc.level, i, selected);
//            }
//            mc.setScreen(null); // Close screen
//        }).pos(centerX - 40, height - 40).size(80, 20).build());
//    }
//
//    @Override
//    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
//        graphics.fill(0, 0, width, height, 0x88000000); // semi-transparent background
//
//        int centerX = width / 2;
//        int startY = height / 4;
//
//        for (int i = 0; i < workingSlots.length; i++) {
//            int x = centerX - BOX_WIDTH / 2;
//            int y = startY + i * (BOX_HEIGHT + PADDING);
//
//            boolean isActive = (i == originalData.currentSlot());
//            if (isActive) {
//                graphics.drawString(font, "▶", x - 10, y + 6, 0x00FF00, false);
//            }
//        }
//
//        super.render(graphics, mouseX, mouseY, partialTicks);
//    }
//
//    @Override
//    public boolean isPauseScreen() {
//        return false;
//    }
//}