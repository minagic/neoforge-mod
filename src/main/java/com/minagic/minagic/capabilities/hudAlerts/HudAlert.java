package com.minagic.minagic.capabilities.hudAlerts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record HudAlert(String message, int color, int priority, int durationTicks) {
    public static final Codec<HudAlert> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("message").forGetter(HudAlert::message),
            Codec.INT.fieldOf("color").forGetter(HudAlert::color),
            Codec.INT.fieldOf("priority").forGetter(HudAlert::priority),
            Codec.INT.fieldOf("durationTicks").forGetter(HudAlert::durationTicks)
    ).apply(instance, HudAlert::new));
}
