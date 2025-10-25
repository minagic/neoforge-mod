package com.minagic.minagic.abstractionLayer;

import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.network.chat.Component;

public abstract class Spell {
    public boolean cast(SpellCastContext context) {
        context.caster.sendSystemMessage(Component.literal("No spell is bound to this slot."));
        return true;
    }

    public String getString() {
        return "No Spell";
    }

    public int getCooldownTicks() {
        return 0;
    }

    public int getManaCost() {
        return 0;
    }

    public boolean canPlayerClassCastSpell(PlayerClass playerClass) {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
