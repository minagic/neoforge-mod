package com.minagic.minagic.spellCasting;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SpellCastContext {
    public Player caster;
    public Level level;
    public ItemStack stack;

    public SpellCastContext(Player caster, Level level, ItemStack stack) {
        this.stack = stack;
        this.caster = caster;
        this.level = level;
    }
}
