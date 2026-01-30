package com.minagic.minagic.capabilities.hudAlerts;

import com.minagic.minagic.registries.ModAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HudOverrideManager {

    // CODEC
    public static final Codec<HudOverrideManager> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    HudOverrideInstance.CODEC.listOf().fieldOf("active_overrides")
                            .forGetter(HudOverrideManager::getActiveOverrides)
            ).apply(instance, overrides -> {
                HudOverrideManager m = new HudOverrideManager();
                m.setActiveOverrides(overrides);
                return m;
            })
    );

    private final List<HudOverrideInstance> ACTIVE_OVERRIDES = new ArrayList<>();

    // =========================
    // Static helper
    // =========================
    public static void addToEntity(Entity entity, IHudOverride override) {
        HudOverrideManager data = entity.getData(ModAttachments.HUD_OVERRIDES);
        data.addOverride(override);
        entity.setData(ModAttachments.HUD_OVERRIDES, data);
    }

    // =========================
    // Core logic
    // =========================
    public void addOverride(IHudOverride override) {
        // Remove lower-priority duplicates of same class
        ACTIVE_OVERRIDES.removeIf(o ->
                o.getOverride().getClass() == override.getClass()
        );

        ACTIVE_OVERRIDES.add(new HudOverrideInstance(override));
        ACTIVE_OVERRIDES.sort(Comparator.comparingInt(o -> -o.getOverride().priority()));
    }

    public List<HudOverrideInstance> getActiveOverrides() {
        return ACTIVE_OVERRIDES;
    }

    public void setActiveOverrides(List<HudOverrideInstance> overrides) {
        ACTIVE_OVERRIDES.clear();
        ACTIVE_OVERRIDES.addAll(overrides);
        ACTIVE_OVERRIDES.sort(Comparator.comparingInt(o -> -o.getOverride().priority()));
    }

    public void tick() {
        ACTIVE_OVERRIDES.removeIf(HudOverrideInstance::tickAndShouldRemove);
    }

    // =========================
    // Render
    // =========================
    public void render(GuiGraphics gui, int width, int height) {
        for (HudOverrideInstance instance : ACTIVE_OVERRIDES) {
            instance.getOverride().render(gui, instance.getTicksRemaining());
        }
    }

    // =========================
    // Tick handler
    // =========================
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        HudOverrideManager data = entity.getData(ModAttachments.HUD_OVERRIDES);
        data.tick();
        entity.setData(ModAttachments.HUD_OVERRIDES, data);
    }

    // =========================
    // SERIALIZER
    // =========================
    public static class Serializer implements IAttachmentSerializer<HudOverrideManager> {

        @Override
        public @NotNull HudOverrideManager read(@NotNull IAttachmentHolder holder, ValueInput input) {
            HudOverrideManager result = new HudOverrideManager();
            input.read("hud_override_manager", HudOverrideManager.CODEC)
                    .ifPresentOrElse(
                            stored -> result.setActiveOverrides(stored.getActiveOverrides()),
                            () -> {
                                // fallback empty
                            }
                    );
            return result;
        }

        @Override
        public boolean write(@NotNull HudOverrideManager attachment, ValueOutput output) {
            output.store("hud_override_manager", HudOverrideManager.CODEC, attachment);
            return true;
        }
    }
}