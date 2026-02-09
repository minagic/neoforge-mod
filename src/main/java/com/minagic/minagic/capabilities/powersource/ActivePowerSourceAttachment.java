package com.minagic.minagic.capabilities.powersource;

import com.minagic.minagic.registries.ModAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class ActivePowerSourceAttachment {

    // =========================
    // INTERNAL STATE
    // =========================
    private @Nullable ResourceLocation activeSourceId;

    // =========================
    // INSTANCE API
    // =========================
    public boolean hasActive() {
        return activeSourceId != null;
    }

    public @Nullable ResourceLocation getActiveSourceId() {
        return activeSourceId;
    }

    public void setActive(@Nullable ResourceLocation id) {
        this.activeSourceId = id;
    }

    public void clear() {
        this.activeSourceId = null;
    }

    // =========================
    // STATIC HELPERS
    // =========================
    public static boolean hasActive(Entity host) {
        return getAttachment(host).hasActive();
    }

    public static @Nullable ResourceLocation getActiveSourceId(Entity host) {
        return getAttachment(host).getActiveSourceId();
    }


    public static void activate(Entity host, ResourceLocation id) {
        AbstractPowerSource active = getActivePowerSource(host);
        if (active != null) {
            active.deactivate();
        }
        ActivePowerSourceAttachment att = getAttachment(host);
        att.setActive(id);
        writeBack(host, att);
    }

    public static void clear(Entity host) {
        AbstractPowerSource active = getActivePowerSource(host);
        if (active != null) {
            active.deactivate();
        }
        ActivePowerSourceAttachment att = getAttachment(host);
        att.clear();

        writeBack(host, att);
    }


    public static @Nullable AbstractPowerSource getActivePowerSource(Entity host) {
        ActivePowerSourceAttachment att = getAttachment(host);
        ResourceLocation id = att.getActiveSourceId();

        if (id == null) return null;

        Supplier<AttachmentType<?>> supplier =
                POWER_SOURCES.get(id);

        if (supplier == null) return null;

        return (AbstractPowerSource) host.getData(supplier.get());
    }

    // =========================
    // PRIVATE HELPERS
    // =========================
    private static ActivePowerSourceAttachment getAttachment(Entity host) {
        return host.getData(ModAttachments.ACTIVE_POWER_SOURCE);
    }

    private static void writeBack(Entity host, ActivePowerSourceAttachment att) {
        host.setData(ModAttachments.ACTIVE_POWER_SOURCE, att);
    }

    // =========================
    // CODEC
    // =========================`
    public static final Codec<ActivePowerSourceAttachment> CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    ResourceLocation.CODEC
                            .optionalFieldOf("active")
                            .forGetter(att -> Optional.ofNullable(att.activeSourceId))
            ).apply(inst, opt -> {
                ActivePowerSourceAttachment att = new ActivePowerSourceAttachment();
                opt.ifPresent(att::setActive);
                return att;
            }));

    // =========================
// SERIALIZER
// =========================
    public static class Serializer implements IAttachmentSerializer<ActivePowerSourceAttachment> {

        private static final String KEY_ACTIVE = "active";

        public @NotNull ActivePowerSourceAttachment read(
                @NotNull IAttachmentHolder holder,
                ValueInput input
        ) {
            ActivePowerSourceAttachment att = new ActivePowerSourceAttachment();

            input.read(KEY_ACTIVE, ResourceLocation.CODEC)
                    .ifPresent(att::setActive);

            return att;
        }

        @Override
        public boolean write(
                ActivePowerSourceAttachment attachment,
                ValueOutput output
        ) {
            if (attachment.activeSourceId != null) {
                output.store(
                        KEY_ACTIVE,
                        ResourceLocation.CODEC,
                        attachment.activeSourceId
                );
            }
            return true;
        }
    }

    // =========================
    // STATIC REGISTRY (NOT SERIALIZED)
    // =========================
    private static final Map<
                ResourceLocation,
                Supplier<AttachmentType<?>>
                > POWER_SOURCES = new HashMap<>();

    public static void register(
            ResourceLocation id,
            Supplier<? extends AttachmentType<?>> type
    ) {
        if (POWER_SOURCES.containsKey(id)) {
            throw new IllegalStateException(
                    "Duplicate power source id: " + id
            );
        }
        POWER_SOURCES.put(id, (Supplier<AttachmentType<?>>) type);
    }
}
