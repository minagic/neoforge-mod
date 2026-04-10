package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.powersource.SorceryPowerSourceAttachment;
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
@AutoDetection.Spell
public class FireballBarrage extends AutonomousSpell implements SorceryPowerSourceAttachment.ISorcerySpell {
    public FireballBarrage() {
        super();

        this.spellName = "Fireball Barrage";
        this.idName = "fireball_barrage";
        this.manaCost = 15;
        this.cooldown = 0;
        this.simulacraThreshold = 5;
    }

    @Override
    public void cast(SpellCastContext ctx, SimulacrumData simData) {
        LivingEntity player = ctx.target;

        Level level = ctx.level();

        Vec3 look = player.getLookAngle();
        Vec3 spawnPos = player.getEyePosition().add(look.scale(0.5)); // spawn slightly in front of the player

        FireballEntity fireball = new FireballEntity(level, spawnPos, look);
        fireball.setOwner(player);
        level.addFreshEntity(fireball);

        level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public String getRequiredBloodline() {
        return SorceryPowerSourceAttachment.BLOODLINE_INFERNAL;
    }

    @Override
    public int getRequiredAffinityLevel() {
        return 0;
    }
}
