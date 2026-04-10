package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.api.spells.InstantaneousSpell;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.powersource.SorceryPowerSourceAttachment;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@AutoDetection.Spell
public class VoidBlast extends InstantaneousSpell implements SorceryPowerSourceAttachment.ISorcerySpell {
    public VoidBlast() {
        super();

        this.spellName = "VoidBlast";
        this.idName = "void_blast";
        this.cooldown = 20 * 3; // 60 ticks
        this.manaCost = 30;
        // simulacrum values untouched
    }

    @Override
    public void cast(SpellCastContext ctx, SimulacrumData simData) {
        LivingEntity player = ctx.caster;
        Level level = ctx.level();

        Vec3 look = player.getLookAngle();
        Vec3 spawnPos = player.getEyePosition().add(look.scale(0.5)); // start just in front of face

        VoidBlastEntity voidBlast = new VoidBlastEntity(level, spawnPos, look);
        voidBlast.setOwner(player);
        level.addFreshEntity(voidBlast);

    }

    @Override
    public String getRequiredBloodline() {
        return SorceryPowerSourceAttachment.BLOODLINE_VOIDBOURNE;
    }

    @Override
    public int getRequiredAffinityLevel() {
        return 3;
    }
}
