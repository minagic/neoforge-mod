package com.minagic.minagic.spellCasting;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SpellCastContext {
    public LivingEntity caster;
    public Level level;
    public ItemStack stack;
    public int simulacrtumLifetime = -1;

    public SpellCastContext(LivingEntity caster, Level level, ItemStack stack) {
        this.stack = stack;
        this.caster = caster;
        this.level = level;
    }


}
