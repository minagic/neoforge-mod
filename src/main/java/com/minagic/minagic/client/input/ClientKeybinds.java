package com.minagic.minagic.client.input;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class ClientKeybinds {
    public static KeyMapping ROLL_RIGHT;
    public static  KeyMapping ROLL_LEFT;
    public static KeyMapping CYCLE_SPELL;
    public static KeyMapping CYCLE_SPELL_DOWN;
    public static KeyMapping SHOW_SPELL_HUD;
    public static KeyMapping DESCEND;
    public static KeyMapping SHIP_FREELOOK;
    public static KeyMapping SHIP_TEST;

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

        DESCEND = new KeyMapping(

                "key.minagic.ship.descend",

                GLFW.GLFW_KEY_C,
                KeyMapping.Category.MISC


        );

        event.register(DESCEND);

        ROLL_LEFT = new KeyMapping(

                "key.minagic.ship.roll_left",

                GLFW.GLFW_KEY_Q,

                KeyMapping.Category.MISC
        );

        event.register(ROLL_LEFT);

        ROLL_RIGHT = new KeyMapping(

                "key.minagic.ship.roll_right",

                GLFW.GLFW_KEY_E,
                KeyMapping.Category.MISC

        );

        event.register(ROLL_RIGHT);

        SHIP_FREELOOK = new KeyMapping(

                "key.minagic.ship.free_look",

                GLFW.GLFW_KEY_Z,
                KeyMapping.Category.MISC

        );

        event.register(SHIP_FREELOOK);

        SHIP_TEST = new KeyMapping(
                "key.minagic.ship.testRot",
                GLFW.GLFW_KEY_B,
                KeyMapping.Category.MISC
        );

        event.register(SHIP_TEST);
    }

}
