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
        if (caster == null) return true;
        if (level() == null) return true;
        if (target == null) return true;
        if (!caster.isAlive() || !target.isAlive()) return true;
        return level().isClientSide();
    }

    public SpellCastContext inverted() {
        return new SpellCastContext(this.target, this.caster);
    }


}
