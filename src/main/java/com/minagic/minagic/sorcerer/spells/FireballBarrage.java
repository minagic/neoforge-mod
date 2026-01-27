package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import com.minagic.minagic.spells.FireballEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * A powerful spell that rapidly fires fireballs forward.
 * Intended for Celestial / Pyromancer classes.
 */
public class FireballBarrage extends AutonomousSpell {
    public FireballBarrage() {
        super();

        this.spellName = "Fireball Barrage";
        this.manaCost = 15;
        this.cooldown = 0;
        this.simulacraThreshold = 5;
        // simulacraMaxLifetime left to superclass default
    }

    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses() {
        return List.of(new DefaultGates.ClassGate.AllowedClass(
                PlayerClassEnum.SORCERER,
                PlayerSubClassEnum.SORCERER_INFERNAL,
                0
        ));
    }

    @Override
    public void cast(SpellCastContext ctx, SimulacrumData simData) {
        SpellGatePolicyGenerator.build(SpellEventPhase.CAST, this.getAllowedClasses(), null, manaCost, null, false, this)
                .setEffect((context, simulacrumData) -> {
                    LivingEntity player = context.target;

                    Level level = context.level();

                    Vec3 look = player.getLookAngle();
                    Vec3 spawnPos = player.getEyePosition().add(look.scale(0.5)); // spawn slightly in front of the player

                    FireballEntity fireball = new FireballEntity(level, spawnPos, look);
                    fireball.setOwner(player);
                    level.addFreshEntity(fireball);

                    level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
                })
                .execute(ctx, simData);
    }

}
