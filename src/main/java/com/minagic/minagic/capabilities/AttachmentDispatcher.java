package com.minagic.minagic.capabilities;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.registries.ModAttachments;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.ArrayList;
import java.util.List;


public final class AttachmentDispatcher {

    private static final List<AttachmentType<?>> ALL = new ArrayList<>();
    private static List<AttachmentType<?>> TICKABLE = new ArrayList<>();
    private static List<AttachmentType<?>> RENDERABLE = new ArrayList<>();

    private static boolean resolved = false;

    // =========================
    // RESOLVE ON FIRST ENTITY
    // =========================
    private static void resolveForEntity(LivingEntity entity) {
        if (resolved) return;
        resolved = true;

        for (var entry : ModAttachments.ATTACHMENTS.getEntries()) {
            AttachmentType<?> type = entry.get();
            ALL.add(type);

            Object attachment;
            try {
                attachment = entity.getData(type);
            } catch (Throwable t) {
                Minagic.LOGGER.error("Failed to resolve attachment {}", type, t);
                continue;
            }

            if (attachment instanceof AutodetectionInterfaces.ILivingTickableAttachment) {
                TICKABLE.add(type);
            }

            if (attachment instanceof AutodetectionInterfaces.IRenderableAttachment) {
                RENDERABLE.add(type);
            }
        }
        TICKABLE = List.copyOf(TICKABLE);
        RENDERABLE = List.copyOf(RENDERABLE);
    }

    // =========================
    // TICK
    // =========================
    public static void tickAll(LivingEntity entity) {
        if (!resolved) resolveForEntity(entity);

        for (var type : TICKABLE) {
            tickAttachment(type, entity);
        }
    }

    private static <T> void tickAttachment(AttachmentType<T> type, LivingEntity living) {
        T attachment = living.getData(type);
        try {

            AutodetectionInterfaces.ILivingTickableAttachment tickable = (AutodetectionInterfaces.ILivingTickableAttachment) attachment;
            tickable.tick(living);

            // write back ONLY if mutated
            living.setData(type, attachment);
        }
        catch (Throwable t){
            Minagic.LOGGER.error("Attachment of type {} crashed during tick: {}", attachment, t);
        }
    }
    // =========================
    // RENDER
    // =========================
    public static void renderAll(LivingEntity entity, GuiGraphics gui) {
        if (!resolved) resolveForEntity(entity);

        for (var type : RENDERABLE) {
            renderAttachment(type, entity, gui);
        }
    }

    private static <T> void renderAttachment(AttachmentType<T> type,
                                             LivingEntity entity,
                                             GuiGraphics gui) {
        T attachment = entity.getData(type);
        try {

            AutodetectionInterfaces.IRenderableAttachment renderable = (AutodetectionInterfaces.IRenderableAttachment) attachment;
            if (renderable.shouldRender(entity)) {
                renderable.render(entity, gui);
            }

        }
        catch (Throwable t){
            Minagic.LOGGER.error("Attachment of type {} crashed during render: {}", attachment, t);
        }
    }

    // =========================
    // EVENT SUBSCRIBERS
    // =========================

    @SubscribeEvent
    public static void onLivingEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity living)) return;

        tickAll(living);
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        renderAll(mc.player, event.getGuiGraphics());
    }
}