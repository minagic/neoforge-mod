package com.minagic.minagic.registries;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.spellCasting.SpellRegistry;
import com.minagic.minagic.utilities.ProjectilePortal;
import org.reflections.Reflections;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Set;

public class ProjectilePortalRenderers {
    public static void register() {
        Reflections reflections = new Reflections("com.minagic.minagic");

        Set<Class<?>> projectilePortalRenderers =
                reflections.getTypesAnnotatedWith(AutoDetection.ProjectilePortalRenderer.class);

        Minagic.LOGGER.debug("Located {} @AutoDetection.ProjectilePortalRenderer classes: {}", projectilePortalRenderers.size(), projectilePortalRenderers);

        for (Class<?> rendererClass : projectilePortalRenderers) {
            try {
                if (!ProjectilePortal.ProjectilePortalRenderer.class.isAssignableFrom(rendererClass)){
                    throw new IllegalClassFormatException("A non-spell class was registered with @AutoDetection.Spell: "+rendererClass.getName());
                }
                ProjectilePortal.ProjectilePortalRenderer renderer = (ProjectilePortal.ProjectilePortalRenderer) rendererClass.getDeclaredConstructor().newInstance();
                ProjectilePortalRendererRegistry.register(renderer);
                Minagic.LOGGER.info("Auto-registered projectile portal renderer: " + renderer.getId());
            }
            catch (Exception e) {
                Minagic.LOGGER.error("Failed to auto-register projectile portal renderer: " + rendererClass.getName(), e);
            }
        }


    }
}
