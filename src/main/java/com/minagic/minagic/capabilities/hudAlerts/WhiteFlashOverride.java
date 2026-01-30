package com.minagic.minagic.capabilities.hudAlerts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class WhiteFlashOverride implements IHudOverride {

    private final int duration;
    private final int priority;

    public WhiteFlashOverride(int duration, int priority) {
        this.duration = duration;
        this.priority = priority;
    }

    @Override
    public void render(GuiGraphics gui, int ticksRemaining) {
        float alpha = (float) ticksRemaining / duration;

        int a = (int)(alpha * 255);
        int color = (a << 24) | 0xFFFFFF;

        gui.fill(0, 0,
                Minecraft.getInstance().getWindow().getGuiScaledWidth(),
                Minecraft.getInstance().getWindow().getGuiScaledHeight(),
                color);
    }

    @Override
    public int durationTicks() {
        return duration;
    }

    @Override
    public int priority() {
        return priority;
    }


}
