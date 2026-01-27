package com.minagic.minagic.spellCasting;

import net.minecraft.world.entity.LivingEntity;
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

    public Level level() {
        return target.level();
    }

    public boolean validate() {
        if (caster == null) return false;
        if (level() == null) return false;
        if (target == null) return false;
        if (!caster.isAlive() || !target.isAlive()) return false;
        return !level().isClientSide();
    }

    public SpellCastContext inverted() {
        return new SpellCastContext(this.target, this.caster);
    }


}
