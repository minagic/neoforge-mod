package com.minagic.minagic.gui;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.SpellcastingItem;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertManager;
import com.minagic.minagic.registries.ModAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public class CooldownOverlay {
    private static final int COOLDOWN_COLOR_SWITCH_RENDERS = 50;
    private static boolean cooldownColorFlag = false;
    private static int cooldownRenderCounter = 0;

    @SubscribeEvent
    public void onRenderOverlay(RenderGuiEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        Minagic.LOGGER.trace("[-COOLDOWN OVERLAY-] Rendering overlay for {}", player.getName().getString());

        ItemStack stack = player.getMainHandItem();

        if (!(stack.getItem() instanceof SpellcastingItem<?>)) return;

        GuiGraphics gui = event.getGuiGraphics();

        // Render spell cooldown if applicable
        renderCooldown(gui, player, stack);

        // subclasses and their levels
        var playerClassData = player.getData(ModAttachments.PLAYER_CLASS.get());
        playerClassData.render(gui);

        // Render mana
        var manaData = player.getData(ModAttachments.MANA);
        manaData.render(gui);


        // Render a channeled spell indicator if applicable
        var simulacraData = player.getData(ModAttachments.PLAYER_SIMULACRA);
        simulacraData.render(gui);


        // render hud alerts
        HudAlertManager hudAlertManager = player.getData(ModAttachments.HUD_ALERTS);
        hudAlertManager.render(gui, gui.guiWidth(), gui.guiHeight());

    }

    public void renderCooldown(GuiGraphics gui, LivingEntity entity, ItemStack stack) {
        if (!(entity instanceof Player player)) return;
        if (!(stack.getItem() instanceof SpellcastingItem<?> item)) return;

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        // --- Core data ---
        double cooldown = item.getRemainingCooldown(stack, player);
        Spell spell = item.getData(stack).getActive().getSpell();

        // --- Layout ---
        int x = 10;
        int y = 150; // fixed vertical position
        int width = 60;
        int height = 50;

        // --- Colors ---
        final int COLOR_TEXT_READY = 0xFF00FF00;  // green
        final int COLOR_TEXT_CD = 0xFFFF4444;     // red
        final int COLOR_TEXT_CD_ALT = 0xFFFFFF00; // yellow
        final int COLOR_TEXT_LABEL = 0xFFFFFFFF;  // white
        final int COLOR_FILL = 0xFFFF4444;        // cooldown bar color

        // --- Spell name ---
        String spellName = (spell != null) ? spell.getString() : "No Spell";
        int spellWidth = font.width(spellName);
        gui.drawString(font, spellName, x + width / 2 - spellWidth / 2, y + 18, COLOR_TEXT_LABEL, false);

        // --- Cooldown text ---
        String cdText = cooldown > 0 ? String.format("[ %.1f s ]", cooldown) : "Ready to cast!";
        int cdWidth = font.width(cdText);
        gui.drawString(font, cdText, x + width / 2 - cdWidth / 2, y + 30, cooldown > 0 ? cooldownColorFlag ? COLOR_TEXT_CD_ALT : COLOR_TEXT_CD : COLOR_TEXT_READY, false);

        cooldownRenderCounter++;
        if (cooldownRenderCounter >= COOLDOWN_COLOR_SWITCH_RENDERS) {
            cooldownColorFlag = !cooldownColorFlag;
            cooldownRenderCounter = 0;
        }

    }
}
