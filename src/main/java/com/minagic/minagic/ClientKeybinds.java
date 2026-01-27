package com.minagic.minagic;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class ClientKeybinds {
    public static KeyMapping CYCLE_SPELL;
    public static KeyMapping CYCLE_SPELL_DOWN;
    public static KeyMapping SHOW_SPELL_HUD;

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

        SHOW_SPELL_HUD = new KeyMapping(
                "key.minagic.show_spell_hud", // translation key
                GLFW.GLFW_KEY_V,
                KeyMapping.Category.MISC
        );
        event.register(SHOW_SPELL_HUD);
    }

}
