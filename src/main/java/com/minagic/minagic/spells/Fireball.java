package com.minagic.minagic.spells;

import com.minagic.minagic.api.spells.InstanteneousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerClassEnum;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class Fireball extends InstanteneousSpell {

    public Fireball() {
        super();

        this.spellName = "Fireball";
        this.cooldown = 40;
        this.manaCost = 30;
        // manaCost and cooldown inherited / preset elsewhere
    }

    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses() {
        return List.of(
                new DefaultGates.ClassGate.AllowedClass(
                        PlayerClassEnum.SORCERER,
                        PlayerSubClassEnum.SORCERER_INFERNAL,
                        3
                ),
                new DefaultGates.ClassGate.AllowedClass(
                        PlayerClassEnum.WIZARD,
                        PlayerSubClassEnum.WIZARD_ELEMANCY,
                        3
                )
        );
    }


    @Override
    public void cast(SpellCastContext ctx, SimulacrumData simData) {
        SpellGatePolicyGenerator.build(SpellEventPhase.CAST, this.getAllowedClasses(), null, manaCost, null, false, this)
                .setEffect((context, simulacrumData) -> {
                    Level level = context.level();
                    LivingEntity player = context.caster;


                    Vec3 look = player.getLookAngle();
                    Vec3 spawnPos = player.getEyePosition().add(look.scale(0.5)); // start just in front of face

                    FireballEntity fireball = new FireballEntity(level, spawnPos, look);
                    fireball.setOwner(player);
                    level.addFreshEntity(fireball);


                    // Optional: play sound or trigger animation
                    level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
                })
                .execute(ctx, simData);
    }
}
