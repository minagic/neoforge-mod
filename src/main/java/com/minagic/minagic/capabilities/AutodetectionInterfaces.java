package com.minagic.minagic.capabilities;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;

public class AutodetectionInterfaces {
    public interface IRenderableAttachment {
        // SIDE EFFECTS ARE FORBIDDEN: CLIENT SIDE ONLY
        // =========================
        // API
        // =========================
        void render(LivingEntity host, GuiGraphics gui);
        boolean shouldRender(LivingEntity host);
    }

    public interface ILivingTickableAttachment {
        // =========================
        // API
        // =========================
        void tick(LivingEntity host); // DO NOT RESOLVE ATTACHMENT, WORK WITH CURRENT STATE!!!
    }

}
