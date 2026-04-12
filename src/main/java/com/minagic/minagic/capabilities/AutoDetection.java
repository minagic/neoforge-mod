package com.minagic.minagic.capabilities;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class AutoDetection {
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

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Spell {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface ProjectilePortalRenderer {

    }



}
