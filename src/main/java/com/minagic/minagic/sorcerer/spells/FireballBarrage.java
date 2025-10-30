package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.abstractionLayer.spells.AutonomousSpell;
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
public class FireballBarrage extends AutonomousSpell {

    @Override
    public boolean canCast(SpellCastContext context) {
        return context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_INFERNAL) >= 10;
    }

    @Override
    public void cast(SpellCastContext context) {
        LivingEntity player = preCast(context);
        if (player == null) {
            return;
        }

        Level level = context.level;

        Vec3 look = player.getLookAngle();
        Vec3 spawnPos = player.getEyePosition().add(look.scale(0.5)); // spawn slightly in front of the player

        FireballEntity fireball = new FireballEntity(level, spawnPos, look);
        fireball.setOwner(player);
        level.addFreshEntity(fireball);

        // Play firing sound
        level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

        // Apply mana cost & cooldown through your base class logic
        applyMagicCosts(context);
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
    public int getMaxLifetime() {
        return 20; // No limit
    }

    @Override
    public String getString() {
        return "Fireball Barrage";
    }
}
