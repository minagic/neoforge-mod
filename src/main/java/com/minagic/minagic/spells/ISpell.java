package com.minagic.minagic.spells;

import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface ISpell {
    default boolean cast(SpellCastContext context) {
        context.caster.sendSystemMessage(Component.literal("No spell is bound to this slot."));
        return true;
    }

    default String getString() {
        return "No Spell";
    }

    default int getCooldownTicks() {
        return 0;
    }

    default int getManaCost() {
        return 0;
    }

    default boolean canPlayerClassCastSpell(PlayerClass playerClass) {
        return true;
    }

}
