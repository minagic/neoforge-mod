package com.minagic.minagic.capabilities.hudAlerts;

import com.minagic.minagic.Minagic;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class HudOverrideRegistry {

    private static final Map<ResourceLocation, IHudOverride> REGISTRY = new HashMap<>();
    private static final Map<IHudOverride, ResourceLocation> REVERSE = new HashMap<>();

    // =========================
    // Registration
    // =========================
    public static void register(ResourceLocation id, IHudOverride override) {
        Minagic.LOGGER.trace("[HudOverrideRegistry] Registering {}", id);

        if (REGISTRY.containsKey(id)) {
            throw new IllegalStateException("Duplicate HudOverride registration: " + id);
        }

        REGISTRY.put(id, override);
        REVERSE.put(override, id);
    }

    // =========================
    // Lookup by id
    // =========================
    public static IHudOverride getCodec(ResourceLocation id) {
        Minagic.LOGGER.trace("[HudOverrideRegistry] Lookup by id {} within {} entries", id, REGISTRY.size());

        if (id != null && REGISTRY.containsKey(id)) {
            return REGISTRY.get(id);
        } else {
            throw new IllegalStateException("Unknown HudOverride id: " + id);
        }
    }

    // =========================
    // Reverse lookup
    // =========================
    public static ResourceLocation getId(IHudOverride codec) {
        Minagic.LOGGER.trace("[HudOverrideRegistry] Lookup by codec {}", codec);
        return REVERSE.get(codec);
    }


    public static int size() {
        return REGISTRY.size();
    }
}