package com.minagic.minagic.spellCasting;

import com.minagic.minagic.capabilities.SimulacrumSpellData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SpellCastContext {
    public LivingEntity caster;
    public LivingEntity target;
    public SimulacrumSpellData simulacrtumLifetime = null;


    public SpellCastContext(LivingEntity caster) {
        this.caster = caster;
        this.target = caster;
    }

    public SpellCastContext(LivingEntity caster, LivingEntity target) {
        this.caster = caster;
        this.target = target;
    }

    public Level level(){
        return target.level();
    }


}
