package com.minagic.minagic.gui;

import com.minagic.minagic.spellCasting.SpellcastingItem;
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
        if (!(stack.getItem() instanceof SpellcastingItem item)) return;

        double cooldown =  item.getRemainingCooldown(stack, player);

        System.out.println("This is Cooldown Overlay reporting: Remaining cooldown is " + cooldown);

        int x = 10;
        int y = 10;
        GuiGraphics gui = event.getGuiGraphics();

        gui.fill(x, y, x + 50, y + 50, 0x80000000); // Background

        String spell_slot = item.getActiveSpellSlotKey(stack);
        //System.out.println("This is Cooldown Overlay reporting: Active spell slot key is " + spell_slot);
        int spellSlotWidth = Minecraft.getInstance().font.width(spell_slot);
        gui.drawString(Minecraft.getInstance().font, spell_slot, x + 25 - spellSlotWidth / 2, y + 5, 0xFFFFFFFF);

        String text_cooldown = cooldown > 0 ? String.format("%.1f s", cooldown) : "Ready";
        int textWidth = Minecraft.getInstance().font.width(text_cooldown);
        gui.drawString(Minecraft.getInstance().font, text_cooldown, x + 25 - textWidth / 2, y + 20, cooldown > 0 ? 0xFFFF0000 : 0xFF00FF00);
    }
}
