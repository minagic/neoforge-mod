package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.abstractionLayer.spells.AutonomousSpell;
import com.minagic.minagic.abstractionLayer.spells.ChanneledAutonomousSpell;
import com.minagic.minagic.abstractionLayer.spells.InvertedAutonomousSpell;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spells.FireballEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * A powerful spell that rapidly fires fireballs forward.
 * Intended for Celestial / Pyromancer classes.
 */
public class FireballBarrage extends InvertedAutonomousSpell {

    @Override
    public CastFailureReason canCast(SpellCastContext context) {
        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getMainClass() != PlayerClassEnum.SORCERER) {
            return CastFailureReason.CASTER_CLASS_MISMATCH;
        }

        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_INFERNAL) == 0) {
            return CastFailureReason.CASTER_SUBCLASS_MISMATCH;
        }

        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_INFERNAL) < 20) {
            return CastFailureReason.CASTER_CLASS_LEVEL_TOO_LOW;
        }
        return CastFailureReason.OK;
    }

    @Override
    public void cast(SpellCastContext context) {
        LivingEntity player = context.target;

        Level level = context.level();

        Vec3 look = player.getLookAngle();
        Vec3 spawnPos = player.getEyePosition().add(look.scale(0.5)); // spawn slightly in front of the player

        FireballEntity fireball = new FireballEntity(level, spawnPos, look);
        fireball.setOwner(player);
        level.addFreshEntity(fireball);

        // Play firing sound
        level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

        // Apply mana cost & cooldown through your base class logic
    }

    @Override
    public int getManaCost() {
        // Cost per shot; adjust for balance
        return 15;
    }

    @Override
    public int getCooldownTicks() {
        return 200;
    }

    @Override
    public int getSimulacrumThreshold() {
        return 5;
    }

    @Override
    public String getString() {
        return "Fireball Barrage";
    }
}
