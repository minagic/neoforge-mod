package com.minagic.minagic.spellCasting;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SpellCastContext {
    public ServerPlayer caster;
    public Level level;

    public SpellCastContext(ServerPlayer caster, Level level) {
        this.caster = caster;
        this.level = level;
    }
}
