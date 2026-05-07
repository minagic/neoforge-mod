package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.api.spells.InstantaneousSpell;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.powersource.SorceryPowerSourceAttachment;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertAttachment;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@AutoDetection.Spell
public class RadiantBlink extends InstantaneousSpell implements SorceryPowerSourceAttachment.ISorcerySpell {

    public RadiantBlink() {
        super(InstantaneousSpell.defaultProperties()
                .withSpellName("Radiant Blink")
                .withIdName("radiant_blink")
                .withManaCost(20)
                .withCooldown(60));
    }

    @Override
    public void cast(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        ServerLevel level = (ServerLevel) context.level();
        LivingEntity target = context.target;

        boolean openSky = level.canSeeSky(target.blockPosition());
        int maxDistance = openSky ? 13 : 10;

        Vec3 eyePos = target.getEyePosition();
        Vec3 look = target.getLookAngle().normalize();

        // Search for teleport location
        for (int i = 1; i <= maxDistance; i++) {
            BlockPos targetPos = BlockPos.containing(eyePos.add(look.scale(i)));
            BlockPos headPos = targetPos.above();

            if (level.isEmptyBlock(targetPos) && level.isEmptyBlock(headPos)) {

                // Particles at current position
                level.sendParticles(ParticleTypes.GLOW,
                        target.getX(), target.getY(), target.getZ(),
                        1, 0.2, 0.2, 0.2, 0.0
                );

                // Teleport
                target.teleportTo(
                        targetPos.getX() + 0.5,
                        targetPos.getY(),
                        targetPos.getZ() + 0.5
                );
                target.hurtMarked = true;

                // Arrival particles
                level.sendParticles(ParticleTypes.END_ROD,
                        target.getX(), target.getY(), target.getZ(),
                        20, 0.2, 0.5, 0.2, 0.01
                );

                // Sky trail
                if (openSky) {
                    for (int j = 0; j < 5; j++) {
                        Vec3 trail = eyePos.add(look.scale(i * j / 5.0));
                        level.sendParticles(ParticleTypes.GLOW,
                                trail.x, trail.y, trail.z,
                                1, 0, 0, 0, 0.01
                        );
                    }
                }

                return;
            }
        }

        // No space found
        HudAlertAttachment.addToEntity(context.caster,
                "No safe space to blink!",
                0xFFDD7700,
                1,
                40
        );
    }

    @Override
    public String getRequiredBloodline() {
        return SorceryPowerSourceAttachment.BLOODLINE_CELESTIAL;
    }

    @Override
    public int getRequiredAffinityLevel() {
        return 7;
    }
}
