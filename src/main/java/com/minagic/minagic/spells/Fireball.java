package com.minagic.minagic.spells;

import com.minagic.minagic.api.spells.InstanteneousSpell;
import com.minagic.minagic.api.spells.SpellValidator;
import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class Fireball extends InstanteneousSpell {

    public Fireball() {
        super();

        this.spellName = "Fireball";
        this.cooldown = 40;
        this.manaCost = 30;
        // manaCost and cooldown inherited / preset elsewhere
    }

    @Override
    public SpellValidator.CastFailureReason canCast(SpellCastContext context){

        // Check if player is a Mage subclass
        PlayerClass playerClass = context.caster.getData(ModAttachments.PLAYER_CLASS);
        if(playerClass.getMainClass() != PlayerClassEnum.SORCERER && playerClass.getMainClass() != PlayerClassEnum.WIZARD) {
            return SpellValidator.CastFailureReason.CASTER_CLASS_MISMATCH;
        }

        if (playerClass.getSubclassLevel(PlayerSubClassEnum.SORCERER_INFERNAL) == 0 &&
            playerClass.getSubclassLevel(PlayerSubClassEnum.WIZARD_ELEMANCY) == 0) {
            return SpellValidator.CastFailureReason.CASTER_SUBCLASS_MISMATCH;
        }

        if (playerClass.getSubclassLevel(PlayerSubClassEnum.SORCERER_INFERNAL) < 3 &&
                playerClass.getSubclassLevel(PlayerSubClassEnum.WIZARD_ELEMANCY) < 3) {
            return SpellValidator.CastFailureReason.CASTER_CLASS_LEVEL_TOO_LOW;
        }

        return SpellValidator.CastFailureReason.OK;
    }


    @Override
    public void cast(SpellCastContext context, SimulacrumSpellData simulacrumData) {

        Level level = context.level();
        LivingEntity player = context.caster;


        Vec3 look = player.getLookAngle();
        Vec3 spawnPos = player.getEyePosition().add(look.scale(0.5)); // start just in front of face

        FireballEntity fireball = new FireballEntity(level, spawnPos, look);
        fireball.setOwner(player);
        level.addFreshEntity(fireball);


        // Optional: play sound or trigger animation
        level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
