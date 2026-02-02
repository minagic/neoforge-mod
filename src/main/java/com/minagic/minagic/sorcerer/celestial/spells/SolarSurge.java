package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.api.spells.InstanteneousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerClassEnum;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SolarSurge extends InstanteneousSpell {

    public SolarSurge() {
        this.spellName = "Solar Surge";
        this.manaCost = 25;
        this.cooldown = 240;
    }

    @Override
    public void cast(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        SpellGatePolicyGenerator.build(SpellEventPhase.CAST, this.getAllowedClasses(), cooldown, manaCost, 0, false, this)
                .setEffect((ctx, simData) -> {
                    ServerLevel level = (ServerLevel) ctx.level();
                    LivingEntity target = ctx.target;

                    // Check for open sky
                    boolean isUnderOpenSky = level.canSeeSky(target.blockPosition());
                    double upwardVelocity = 0.9 + (isUnderOpenSky ? 2 : 0.0);

                    // Launch caster upward
                    target.setDeltaMovement(target.getDeltaMovement().add(0, upwardVelocity, 0));
                    target.hurtMarked = true; // force motion sync

                    // Give slow falling if in open air
                    if (isUnderOpenSky) {
                        target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20 * 4, 0));
                    }

                    // Visuals: burst at feet
                    level.sendParticles(ParticleTypes.FLAME,
                            target.getX(), target.getY(), target.getZ(),
                            20, 0.2, 0, 0.2, 0.01);

                    // Visuals: ascending trail
                    for (int i = 0; i < 10; i++) {
                        Vec3 pos = target.position().add(0, i * 0.3, 0);
                        level.sendParticles(ParticleTypes.END_ROD,
                                pos.x, pos.y, pos.z,
                                2, 0.1, 0.1, 0.1, 0.01);
                    }
                })
                .execute(context, simulacrumData);


    }

    @Override
    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses() {
        return List.of(new DefaultGates.ClassGate.AllowedClass(
                PlayerClassEnum.SORCERER, PlayerSubClassEnum.SORCERER_CELESTIAL, 6
        ));
    }
}