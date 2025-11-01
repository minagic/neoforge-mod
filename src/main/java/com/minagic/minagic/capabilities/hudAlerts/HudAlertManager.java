package com.minagic.minagic.capabilities.hudAlerts;

import com.minagic.minagic.registries.ModAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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

public class HudAlertManager {
    private List<HudAlertInstance> ACTIVE_ALERTS = new ArrayList<>();

    public void addAlert(String msg, int color, int priority, int durationTicks) {
        // Remove lower-priority duplicates
        ACTIVE_ALERTS.removeIf(a -> a.getAlert().message().equals(msg));
        ACTIVE_ALERTS.add(new HudAlertInstance(new HudAlert(msg, color, priority, durationTicks)));
        ACTIVE_ALERTS.sort(Comparator.comparingInt(a -> -a.getAlert().priority()));
    }

    public List<HudAlertInstance> getActiveAlerts() {
        return ACTIVE_ALERTS;
    }

    public void setActiveAlerts(List<HudAlertInstance> alerts) {
        ACTIVE_ALERTS.clear();
        ACTIVE_ALERTS.addAll(alerts);
        ACTIVE_ALERTS.sort(Comparator.comparingInt(a -> -a.getAlert().priority()));
    }

    public void tick() {
        ACTIVE_ALERTS.removeIf(HudAlertInstance::tickAndShouldRemove);
    }

    public void render(GuiGraphics gui, int width, int height) {
        int y = 20; // Centered messages near top of screen
        for (int i = 0; i < ACTIVE_ALERTS.size(); i++) {
            HudAlertInstance instance = ACTIVE_ALERTS.get(i);
            int alpha = instance.getAlpha();
            int color = (alpha << 24) | (instance.getAlert().color() & 0xFFFFFF);

            gui.drawCenteredString(
                    Minecraft.getInstance().font,
                    instance.getAlert().message(),
                    width / 2,
                    y + i * 12,
                    color
            );
        }
    }

    // tick handler
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        HudAlertManager data = entity.getData(ModAttachments.HUD_ALERTS);
        data.tick();
        entity.setData(ModAttachments.HUD_ALERTS, data);

    }



    // CODEC
    public static final Codec<HudAlertManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            HudAlertInstance.CODEC.listOf().fieldOf("active_alerts")
                    .forGetter(HudAlertManager::getActiveAlerts)
    ).apply(instance, alerts -> {
        HudAlertManager m = new HudAlertManager();
        m.setActiveAlerts(alerts);
        return m;
    }));
    // SERIALIZER
    public static class Serializer implements IAttachmentSerializer<HudAlertManager> {

        @Override
        public @NotNull HudAlertManager read(@NotNull IAttachmentHolder holder, ValueInput input) {
            HudAlertManager result = new HudAlertManager();
            input.read("hud_alert_manager", HudAlertManager.CODEC)
                    .ifPresentOrElse(
                            stored -> result.setActiveAlerts(stored.getActiveAlerts()),
                            () -> {
                                // fallback: start empty
                            });
            return result;
        }

        @Override
        public boolean write(@NotNull HudAlertManager attachment, ValueOutput output) {
            output.store("hud_alert_manager", HudAlertManager.CODEC, attachment);
            return true;
        }
    }

}



