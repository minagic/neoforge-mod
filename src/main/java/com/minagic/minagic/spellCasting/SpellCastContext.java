package com.minagic.minagic.spellCasting;

import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.utilities.SpellValidationResult;
import net.minecraft.server.level.ServerPlayer;
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

    public SpellValidationResult validate(){
        if (caster == null) return SpellValidationResult.internalFail("No caster.");
        if (level() == null) return SpellValidationResult.internalFail("No level.");
        if (caster.asLivingEntity() == null) return SpellValidationResult.internalFail("Caster must be living.");
        if (target == null) return SpellValidationResult.internalFail("No target.");
        if (target.asLivingEntity() == null) return SpellValidationResult.internalFail("Target must be living.");
        if (!caster.isAlive() || !target.isAlive()) return SpellValidationResult.internalFail("Caster or target dead.");
        if (level().isClientSide()) return SpellValidationResult.internalFail("Cannot cast on client.");
        return SpellValidationResult.OK;
    }

    public SpellCastContext inverted(){
        return new SpellCastContext(this.target, this.caster);
    }




}
