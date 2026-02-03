package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.api.spells.InstanteneousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerClassEnum;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertAttachment;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RadiantBlink extends InstanteneousSpell {

    public RadiantBlink() {
        this.spellName = "Radiant Blink";
        this.manaCost = 20;
        this.cooldown = 60;
    }

    @Override
    public void cast(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        SpellGatePolicyGenerator.build(
                        SpellEventPhase.CAST,
                        this.getAllowedClasses(),
                        cooldown,
                        manaCost,
                        0, // sustain cost
                        false,
                        this
                )
                .setEffect((ctx, simData) -> {
                    ServerLevel level = (ServerLevel) ctx.level();
                    LivingEntity target = ctx.target;

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
                    HudAlertAttachment.addToEntity(ctx.caster,
                            "No safe space to blink!",
                            0xFFDD7700,
                            1,
                            40
                    );
                })
                .execute(context, simulacrumData);
    }

    @Override
    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses() {
        return List.of(new DefaultGates.ClassGate.AllowedClass(
                PlayerClassEnum.SORCERER,
                PlayerSubClassEnum.SORCERER_CELESTIAL,
                7
        ));
    }
}