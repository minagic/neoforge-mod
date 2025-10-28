package com.minagic.minagic.abstractionLayer;

import com.minagic.minagic.abstractionLayer.spells.Spell;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;


public class SpellSelectionScreen extends Screen {
    private final List<Spell> spells;
    private final Consumer<Spell> onSelect;


    private final int buttonWidth = 150;
    private final int buttonHeight = 20;
    private final int spacing = 5;

    public SpellSelectionScreen(List<Spell> spells, Consumer<Spell> onSelect) {
        super(Component.literal("Select a Spell"));
        this.spells = spells;
        this.onSelect = onSelect;
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - buttonWidth) / 2;
        int y = 20;

        for (int i = 0; i < spells.size(); i++) {
            Spell spell = spells.get(i);
            this.addRenderableWidget(Button.builder(
                    Component.literal(spell.getString()), // Customize this
                    btn -> {
                        onSelect.accept(spell);
                        this.onClose(); // Close after selection
                    }
            ).pos(x, y + i * (buttonHeight + spacing)).size(buttonWidth, buttonHeight).build());
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        gfx.fill(0, 0, this.width, this.height, 0xFF202020); // simple dark background
        super.render(gfx, mouseX, mouseY, partialTicks);
        gfx.drawCenteredString(this.font, this.title, this.width / 2, 5, 0xFFFFFF);
    }
}