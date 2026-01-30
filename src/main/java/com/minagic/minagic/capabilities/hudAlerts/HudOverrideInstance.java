package com.minagic.minagic.capabilities.hudAlerts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public class HudOverrideInstance {

    public static final Codec<HudOverrideInstance> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("override")
                            .forGetter(hudOverrideInstance -> HudOverrideRegistry.getId(hudOverrideInstance.getOverride())),
                    Codec.INT.fieldOf("ticksRemaining")
                            .forGetter(HudOverrideInstance::getTicksRemaining)
            ).apply(instance, (resourceLocation, integer) -> new HudOverrideInstance(HudOverrideRegistry.getCodec(resourceLocation), integer))
    );

    private final IHudOverride override;
    private int ticksRemaining;

    public HudOverrideInstance(IHudOverride override) {
        this.override = override;
        this.ticksRemaining = override.durationTicks();
    }

    public HudOverrideInstance(IHudOverride override, int ticksRemaining) {
        this.override = override;
        this.ticksRemaining = ticksRemaining;
    }

    public boolean tickAndShouldRemove() {
        ticksRemaining--;
        return ticksRemaining <= 0;
    }

    public IHudOverride getOverride() {
        return override;
    }

    public int getTicksRemaining() {
        return ticksRemaining;
    }
}
