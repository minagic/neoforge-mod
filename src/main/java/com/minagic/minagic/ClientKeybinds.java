package com.minagic.minagic;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ClientKeybinds {
    public static final String CATEGORY = "key.categories.minagic";
    public static KeyMapping CYCLE_SPELL;
    public static KeyMapping CYCLE_SPELL_DOWN;

    public static void registerKeybinds(RegisterKeyMappingsEvent event) {
        CYCLE_SPELL = new KeyMapping(
                "key.minagic.cycle_spell", // translation key
                GLFW.GLFW_KEY_R,
                KeyMapping.Category.MISC
        );
        event.register(CYCLE_SPELL);

        CYCLE_SPELL_DOWN = new KeyMapping(
                "key.minagic.cycle_spell_down", // translation key
                GLFW.GLFW_KEY_Y,
                KeyMapping.Category.MISC
        );

        event.register(CYCLE_SPELL_DOWN);
    }

}