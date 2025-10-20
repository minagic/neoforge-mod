package com.minagic.minagic.spellCasting;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SpellCastContext {
    public Player caster;
    public Level level;

    public SpellCastContext(Player caster, Level level) {
        this.caster = caster;
        this.level = level;
    }
}
