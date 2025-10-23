package com.minagic.minagic.gui;

import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.ISpellcastingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public class CooldownOverlay {
    @SubscribeEvent
    public void onRenderOverlay(RenderGuiEvent.Pre event) {
        Player player = Minecraft.getInstance().player;

        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof ISpellcastingItem item)) return;

        double cooldown =  item.getRemainingCooldown(stack, player);

        int x = 10;
        int y = 10;
        GuiGraphics gui = event.getGuiGraphics();

        gui.fill(x, y, x + 50, y + 50, 0x80000000); // Background

        String spell_slot = item.getActiveSpellSlotKey(stack);
        int spellSlotWidth = Minecraft.getInstance().font.width(spell_slot);
        gui.drawString(Minecraft.getInstance().font, spell_slot, x + 25 - spellSlotWidth / 2, y + 5, 0xFFFFFFFF);

        String text_cooldown = cooldown > 0 ? String.format("%.1f s", cooldown) : "Ready";
        int textWidth = Minecraft.getInstance().font.width(text_cooldown);
        gui.drawString(Minecraft.getInstance().font, text_cooldown, x + 25 - textWidth / 2, y + 20, cooldown > 0 ? 0xFFFF0000 : 0xFF00FF00);

        // render class and mana in bottom left of the overlay

        int x2 = 10;
        int y2 = 70;

        String playerClass = player.getData(ModAttachments.PLAYER_CLASS.get()).getMainClass().toString();

        int classWidth = Minecraft.getInstance().font.width(playerClass);
        gui.drawString(Minecraft.getInstance().font, playerClass, x2 + 25 - classWidth / 2, y2 + 35, 0xFFFFFF00);

        // subclasses and their levels
        StringBuilder subclassesText = new StringBuilder("Subclasses: ");
        player.getData(ModAttachments.PLAYER_CLASS).getAllSubclasses().forEach((subclass, level) -> {
            subclassesText.append(subclass.toString()).append(" (Lv ").append(level).append("), ");
        });
        // render subclasses text
        String subclassesFinalText = subclassesText.toString();
        if (subclassesFinalText.endsWith(", ")) {
            subclassesFinalText = subclassesFinalText.substring(0, subclassesFinalText.length() - 2);
        }
        int subclassesWidth = Minecraft.getInstance().font.width(subclassesFinalText);
        gui.drawString(Minecraft.getInstance().font, subclassesFinalText, x2 + 25 - subclassesWidth / 2, y2 + 50, 0xFFFFFFAA);

        float mana = player.getData(ModAttachments.MANA.get()).getMana();
        int maxMana = player.getData(ModAttachments.MANA.get()).getMaxMana();
        String manaText = "Mana: " + mana + "/" + maxMana;
        int manaWidth = Minecraft.getInstance().font.width(manaText);
        gui.drawString(Minecraft.getInstance().font, manaText, x2 + 25 - manaWidth / 2, y2 + 75, 0xFF00FFFF);

    }
}
