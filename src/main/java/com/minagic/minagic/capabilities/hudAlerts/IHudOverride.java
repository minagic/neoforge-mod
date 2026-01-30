package com.minagic.minagic.capabilities.hudAlerts;

import com.mojang.serialization.Codec;
import net.minecraft.client.gui.GuiGraphics;

public interface IHudOverride {
    void render(GuiGraphics gui, int ticksRemaining);

    int durationTicks();
    int priority();

}