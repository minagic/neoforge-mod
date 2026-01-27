package com.minagic.minagic.api.gui;

import com.minagic.minagic.api.spells.Spell;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SpellSelectionScreen extends Screen {
    private static final int VISIBLE_ENTRIES = 8;
    private static final int ENTRY_HEIGHT = 20;
    private static final int LIST_WIDTH = 200;

    private final List<Spell> spells;
    private final Consumer<Spell> onSelect;
    private final List<EntryHitbox> entryHitboxes = new ArrayList<>();

    private int selectedIndex;
    private int scrollOffset;
    private Button confirmButton;

    public SpellSelectionScreen(List<Spell> spells, Consumer<Spell> onSelect) {
        super(Component.literal("Select a Spell"));
        this.spells = spells;
        this.onSelect = onSelect;
    }

    @Override
    protected void init() {
        super.init();
        selectedIndex = 0;
        scrollOffset = 0;

        int listCenterX = this.width / 2;
        int buttonY = this.height - 40;

        confirmButton = Button.builder(Component.literal("Select"), btn -> confirmSelection())
                .pos(listCenterX - 160, buttonY)
                .size(150, 20)
                .build();
        Button cancelButton = Button.builder(Component.literal("Cancel"), btn -> this.onClose())
                .pos(listCenterX + 10, buttonY)
                .size(150, 20)
                .build();
        this.addRenderableWidget(confirmButton);
        this.addRenderableWidget(cancelButton);
        updateConfirmButton();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (spells.isEmpty()) {
            return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
        }

        double delta = Math.abs(deltaY) > Math.abs(deltaX) ? deltaY : deltaX;
        if (delta != 0) {
            scrollBy(delta > 0 ? -1 : 1);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean handled) {
        if (handled) {
            return super.mouseClicked(event, true);
        }

        if (event.button() == 0) {
            double mouseX = event.x();
            double mouseY = event.y();
            for (EntryHitbox hitbox : entryHitboxes) {
                if (hitbox.contains(mouseX, mouseY)) {
                    if (hitbox.index() == selectedIndex) {
                        confirmSelection();
                    } else {
                        setSelection(hitbox.index());
                    }
                    return true;
                }
            }
        }

        return super.mouseClicked(event, false);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        gfx.fill(0, 0, this.width, this.height, 0xBF101010);
        super.render(gfx, mouseX, mouseY, partialTicks);

        gfx.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
        renderList(gfx);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        if (keyCode == GLFW.GLFW_KEY_UP) {
            moveSelection(-1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            moveSelection(1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            moveSelection(-VISIBLE_ENTRIES);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            moveSelection(VISIBLE_ENTRIES);
            return true;
        }

        return super.keyPressed(event);
    }

    private void renderList(GuiGraphics gfx) {
        entryHitboxes.clear();

        int listCenterX = this.width / 2;
        int listLeft = listCenterX - LIST_WIDTH / 2;
        int listTop = 40;
        int visible = Math.min(VISIBLE_ENTRIES, spells.size());

        gfx.fill(listLeft - 6, listTop - 6,
                listLeft + LIST_WIDTH + 6, listTop + visible * ENTRY_HEIGHT + 6,
                0x80202020);

        if (spells.isEmpty()) {
            gfx.drawCenteredString(this.font, Component.literal("No spells available"), listCenterX, listTop + 10, 0xFFAAAAAA);
            confirmButton.active = false;
            return;
        }

        int maxOffset = Math.max(0, spells.size() - VISIBLE_ENTRIES);
        scrollOffset = Mth.clamp(scrollOffset, 0, maxOffset);

        for (int i = 0; i < visible; i++) {
            int spellIndex = scrollOffset + i;
            Spell spell = spells.get(spellIndex);
            int entryTop = listTop + i * ENTRY_HEIGHT;
            int entryBottom = entryTop + ENTRY_HEIGHT - 2;
            int color = spellIndex == selectedIndex ? 0xFF2255AA : 0xFF000000;
            gfx.fill(listLeft, entryTop, listLeft + LIST_WIDTH, entryBottom, color);
            gfx.drawString(this.font, spell.getString(), listLeft + 6, entryTop + 5,
                    spellIndex == selectedIndex ? 0xFFFFFFFF : 0xFFDDDDDD);

            entryHitboxes.add(new EntryHitbox(spellIndex, listLeft, entryTop, LIST_WIDTH, ENTRY_HEIGHT));
        }

        String rangeText = String.format("%d / %d", selectedIndex + 1, spells.size());
        gfx.drawCenteredString(this.font, Component.literal(rangeText + " • Scroll or use ↑/↓"), listCenterX,
                listTop + visible * ENTRY_HEIGHT + 24, 0xFF888888);
    }

    private void scrollBy(int entries) {
        if (spells.isEmpty()) {
            return;
        }

        int maxOffset = Math.max(0, spells.size() - VISIBLE_ENTRIES);
        scrollOffset = Mth.clamp(scrollOffset + entries, 0, maxOffset);

        // keep selected entry inside viewport by shifting selection
        int minVisible = scrollOffset;
        int maxVisible = scrollOffset + VISIBLE_ENTRIES - 1;
        if (selectedIndex < minVisible) {
            setSelection(minVisible);
        } else if (selectedIndex > maxVisible) {
            setSelection(maxVisible);
        } else {
            updateConfirmButton();
        }
    }

    private void setSelection(int index) {
        if (spells.isEmpty()) {
            return;
        }
        selectedIndex = Mth.clamp(index, 0, spells.size() - 1);
        ensureSelectionVisible();
        updateConfirmButton();
    }

    private void ensureSelectionVisible() {
        int maxOffset = Math.max(0, spells.size() - VISIBLE_ENTRIES);
        if (selectedIndex < scrollOffset) {
            scrollOffset = selectedIndex;
        } else if (selectedIndex >= scrollOffset + VISIBLE_ENTRIES) {
            scrollOffset = Math.min(selectedIndex - VISIBLE_ENTRIES + 1, maxOffset);
        }
    }

    private void moveSelection(int delta) {
        if (spells.isEmpty()) {
            return;
        }
        int newIndex = Mth.clamp(selectedIndex + delta, 0, spells.size() - 1);
        setSelection(newIndex);
    }

    private void confirmSelection() {
        if (spells.isEmpty()) {
            return;
        }
        onSelect.accept(spells.get(selectedIndex));
        this.onClose();
    }

    private void updateConfirmButton() {
        if (confirmButton == null) {
            return;
        }
        boolean hasSpells = !spells.isEmpty();
        confirmButton.active = hasSpells;
        if (hasSpells) {
            confirmButton.setMessage(Component.literal("Select: " + spells.get(selectedIndex).getString()));
        } else {
            confirmButton.setMessage(Component.literal("Select"));
        }
    }

    private record EntryHitbox(int index, int x, int y, int width,
                               int height) {
        boolean contains(double px, double py) {
            return px >= x && px <= x + width && py >= y && py <= y + height;
        }
    }
}
