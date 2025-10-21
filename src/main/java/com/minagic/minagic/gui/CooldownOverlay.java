package com.minagic.minagic.gui;

import com.minagic.minagic.spellCasting.SpellcastingItem;
import com.minagic.minagic.utilities.LocalCooldownManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

public class CooldownOverlay {
    @SubscribeEvent
    public void onRenderOverlay(RenderGuiEvent.Pre event) {
        Player player = Minecraft.getInstance().player;

        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof SpellcastingItem)) return;

        double cooldown =  LocalCooldownManager.getRemainingCooldown(stack); // Once per frame

        int x = 10;
        int y = 10;
        GuiGraphics gui = event.getGuiGraphics();

        gui.fill(x, y, x + 50, y + 50, 0x80000000); // Background

        String text = cooldown > 0 ? String.format("Cooldown: %.1f s", cooldown) : "Spell Matrix Online";

        int textWidth = Minecraft.getInstance().font.width(text);
        gui.drawString(Minecraft.getInstance().font, text, x + 25 - textWidth / 2, y + 20, 0xFFFFFFFF);
    }
}
