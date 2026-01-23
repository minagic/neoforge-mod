package com.minagic.minagic.spellCasting;

import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.utilities.SpellValidationResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SpellCastContext {
    public LivingEntity caster;
    public LivingEntity target;


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

    public boolean validate(){
        if (caster == null) return false;
        if (level() == null) return false;
        if (target == null) return false;
        if (!caster.isAlive() || !target.isAlive()) return false;
        if (level().isClientSide()) return false;
        return true;
    }

    public SpellCastContext inverted(){
        return new SpellCastContext(this.target, this.caster);
    }




}
