package com.minagic.minagic.capabilities.hudAlerts;

import com.minagic.minagic.capabilities.AutodetectionInterfaces;
import com.minagic.minagic.registries.ModAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

public final class WhiteFlashAttachment
        implements AutodetectionInterfaces.ILivingTickableAttachment,
        AutodetectionInterfaces.IRenderableAttachment {

    // =========================
    // INTERNAL STATE
    // =========================
    private int ticksRemaining;
    private int duration;

    // =========================
    // CONSTRUCTOR
    // =========================
    public WhiteFlashAttachment() {
        this.ticksRemaining = 0;
        this.duration = 0;
    }

    // =========================
    // INSTANCE GETTERS
    // =========================
    public int getTicksRemaining() {
        return ticksRemaining;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isActive() {
        return ticksRemaining > 0 && duration > 0;
    }

    // =========================
    // STATIC GETTERS
    // =========================
    public static WhiteFlashAttachment get(Entity host) {
        return host.getData(ModAttachments.WHITE_FLASH);
    }

    // =========================
    // INSTANCE SETTERS
    // =========================
    public void start(int duration) {
        this.duration = duration;
        this.ticksRemaining = duration;
    }

    public void clear() {
        this.duration = 0;
        this.ticksRemaining = 0;
    }

    // =========================
    // STATIC SETTERS
    // =========================
    public static void start(Entity host, int duration) {
        WhiteFlashAttachment att = host.getData(ModAttachments.WHITE_FLASH);
        att.start(duration);
        host.setData(ModAttachments.WHITE_FLASH, att);
    }

    public static void clear(Entity host) {
        WhiteFlashAttachment att = host.getData(ModAttachments.WHITE_FLASH);
        att.clear();
        host.setData(ModAttachments.WHITE_FLASH, att);
    }

    // =========================
    // TICK
    // =========================
    @Override
    public void tick(LivingEntity host) {
        if (ticksRemaining > 0) {
            ticksRemaining--;
        }
    }

    // =========================
    // RENDER CONTROL
    // =========================
    @Override
    public boolean shouldRender(LivingEntity host) {
        return isActive();
    }

    @Override
    public void render(LivingEntity host, GuiGraphics gui) {
        if (!isActive()) return;

        float alpha = (float) ticksRemaining / (float) duration;
        int a = Mth.clamp((int) (alpha * 255), 0, 255);

        int color = (a << 24) | 0xFFFFFF;

        Minecraft mc = Minecraft.getInstance();

        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        gui.fill(0, 0, w, h, color);
    }

    // =========================
    // CODEC
    // =========================
    public static final Codec<WhiteFlashAttachment> CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Codec.INT.fieldOf("ticksRemaining").forGetter(WhiteFlashAttachment::getTicksRemaining),
                    Codec.INT.fieldOf("duration").forGetter(WhiteFlashAttachment::getDuration)
            ).apply(inst, (ticks, duration) -> {
                WhiteFlashAttachment att = new WhiteFlashAttachment();
                att.ticksRemaining = ticks;
                att.duration = duration;
                return att;
            }));

    // =========================
    // SERIALIZER
    // =========================
    public static class Serializer implements IAttachmentSerializer<WhiteFlashAttachment> {

        private static final String KEY_TICKS = "ticks";
        private static final String KEY_DURATION = "duration";

        @Override
        public @NotNull WhiteFlashAttachment read(@NotNull IAttachmentHolder holder, ValueInput input) {
            WhiteFlashAttachment att = new WhiteFlashAttachment();

            input.read(KEY_TICKS, Codec.INT).ifPresent(v -> att.ticksRemaining = v);
            input.read(KEY_DURATION, Codec.INT).ifPresent(v -> att.duration = v);

            return att;
        }

        @Override
        public boolean write(WhiteFlashAttachment attachment, ValueOutput output) {
            if (attachment.ticksRemaining > 0) {
                output.store(KEY_TICKS, Codec.INT, attachment.ticksRemaining);
                output.store(KEY_DURATION, Codec.INT, attachment.duration);
            }
            return true;
        }
    }
}