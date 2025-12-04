package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.api.spells.InstanteneousSpell;
import com.minagic.minagic.api.spells.SpellValidator;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VoidBlast extends InstanteneousSpell {
    @Override
    public SpellValidator.CastFailureReason canCast(SpellCastContext context){
        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getMainClass() != PlayerClassEnum.SORCERER) {
            return SpellValidator.CastFailureReason.CASTER_CLASS_MISMATCH;
        }

        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE) == 0) {
            return SpellValidator.CastFailureReason.CASTER_SUBCLASS_MISMATCH;
        }

        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE) < 3) {
            return SpellValidator.CastFailureReason.CASTER_CLASS_LEVEL_TOO_LOW;
        }
        return SpellValidator.CastFailureReason.OK;
    }

    public VoidBlast() {
        super();

        this.spellName = "VoidBlast";
        this.cooldown = 20 * 3; // 60 ticks
        this.manaCost = 30;
        // simulacrum values untouched
    }

    @Override
    public void cast(SpellCastContext context) {
        LivingEntity player = context.caster;
        Level level = context.level();


        Vec3 look = player.getLookAngle();
        Vec3 spawnPos = player.getEyePosition().add(look.scale(0.5)); // start just in front of face

        VoidBlastEntity voidBlast = new VoidBlastEntity(level, spawnPos, look);
        voidBlast.setOwner(player);
        level.addFreshEntity(voidBlast);
    }
}
