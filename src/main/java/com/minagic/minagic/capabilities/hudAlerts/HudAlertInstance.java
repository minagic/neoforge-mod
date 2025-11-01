package com.minagic.minagic.capabilities.hudAlerts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class HudAlertInstance {
    private final HudAlert alert;
    private int ticksExisted = 0;

    public HudAlertInstance(HudAlert alert) {
        this.alert = alert;
    }

    public boolean tickAndShouldRemove() {
        ticksExisted++;
        return ticksExisted > alert.durationTicks();
    }

    public int getTicksExisted() {
        return ticksExisted;
    }

    public int getAlpha() {
        int fadeOutStart = alert.durationTicks() - 20; // last second fades
        if (ticksExisted < fadeOutStart) return 255;
        return Math.max(0, 255 - (ticksExisted - fadeOutStart) * 12);
    }

    public HudAlert getAlert() {
        return alert;
    }

    public void setTicksExisted(int ticksExisted) {
        this.ticksExisted = ticksExisted;
    }

    // CODEC
    public static final Codec<HudAlertInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            HudAlert.CODEC.fieldOf("alert").forGetter(HudAlertInstance::getAlert),
            Codec.INT.fieldOf("ticksExisted").forGetter(HudAlertInstance::getTicksExisted)
    ).apply(instance, (alert, ticks) -> {
        HudAlertInstance i = new HudAlertInstance(alert);
        // restore runtime tick value
        i.setTicksExisted(ticks);
        return i;
    }));

}
