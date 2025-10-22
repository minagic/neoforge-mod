package com.minagic.minagic.spells;

import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.resources.ResourceLocation;

public interface ISpell {
    default boolean cast(SpellCastContext context) {
        return true;
        // return true if cast was successful
    }

    ResourceLocation getId();

    default int getCooldownTicks() {
        return 0;
    }

    default int getManaCost() {
        return 0;
    }
}
