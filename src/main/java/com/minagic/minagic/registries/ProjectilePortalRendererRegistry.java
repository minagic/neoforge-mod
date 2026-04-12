package com.minagic.minagic.registries;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.utilities.ProjectilePortal;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ProjectilePortalRendererRegistry {
    public static final Map<String , ProjectilePortal.ProjectilePortalRenderer> REGISTRY = new HashMap<>();
    private static final Map<ProjectilePortal.ProjectilePortalRenderer, String> REVERSE = new HashMap<>();


    public static void register(ProjectilePortal.ProjectilePortalRenderer renderer) {
        String id = renderer.getId();
        if (REGISTRY.containsKey(id)){
            throw new IllegalArgumentException("ProjectilePortalRenderer with ID " + id + " is already registered");
        }

        REGISTRY.put(id, renderer);
        REVERSE.put(renderer, id);
        Minagic.LOGGER.debug("Successfully registered renderer: {}/{}", id, renderer);
    }

    public static ProjectilePortal.ProjectilePortalRenderer getProjectilePortalRenderer(String id) {
        Minagic.LOGGER.trace("[ProjectilePortalRendererRegistry] Lookup by id {} within {} entries", id, REGISTRY.size());

        if (id != null && REGISTRY.containsKey(id)) {
            return REGISTRY.get(id);
        } else {
            return null;
        }
    }

    public static String getId(ProjectilePortal.ProjectilePortalRenderer renderer) {
        Minagic.LOGGER.trace("[ProjectilePortalRendererRegistry] Lookup by renderer {}", renderer == null ? "null" : renderer.getClass().getSimpleName());
        return REVERSE.get(renderer);
    }
}
