package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import com.minagic.minagic.spells.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AetherGlide extends AutonomousSpell {

    public AetherGlide() {
        this.spellName = "Aether Glide";
        this.manaCost = 0;
        this.cooldown = 0;
        this.simulacraThreshold = 0;
    }

    @Override
    public void start(SpellCastContext context, SimulacrumData data){
        super.start(context, data);
        SimulacraAttachment sim = context.target.getData(ModAttachments.PLAYER_SIMULACRA);
        if (sim.hasSpell(ModSpells.getId(this))){
            if (context.target instanceof Player player) {
                player.setPose(Pose.FALL_FLYING);
            }
        }

    }

    @Override
    public void exitSimulacrum(SpellCastContext context, SimulacrumData simulacrumData){
        if (context.target instanceof Player player) {
            player.setPose(Pose.STANDING);
            player.setOnGround(true);
        }
    }

    @Override
    public void tick(SpellCastContext context, SimulacrumData simulacrumData) {
        SpellGatePolicyGenerator.build(
                SpellEventPhase.TICK,
                this.getAllowedClasses(),
                null,
                null,
                1,
                false,
                this
        ).setEffect((ctx, simData) -> {

            LivingEntity target = ctx.target;
            Level level = target.level();

            if (level.isClientSide()) return;

// -----------------------------
// ALTITUDE FACTOR (0..1)
// -----------------------------
            double altitude = target.getY();
            double altitudeFactor = Mth.clamp((altitude - 64.0) / 192.0, 0.0, 1.0);

// -----------------------------
// THRUST & STEERING CURVES
// -----------------------------
            double thrust = 1.5 + altitudeFactor * 1.5;   // power grows with altitude
            double steer = Mth.lerp(altitudeFactor, 0.85, 0.45); // control degrades but never below 0.45
            double gravityBias = altitudeFactor * 0.6;   // more downward at high altitude

// -----------------------------
// DIRECTION VECTORS
// -----------------------------
            Vec3 look = target.getLookAngle().normalize();
            Vec3 gravity = new Vec3(0, -1, 0);

// tilt thrust toward gravity as altitude rises
            Vec3 thrustDir = look.lerp(gravity, gravityBias).normalize();

// -----------------------------
// VELOCITY BLEND
// -----------------------------
            Vec3 vel = target.getDeltaMovement();
            Vec3 desired = thrustDir.scale(thrust);

// steering loss at altitude
            Vec3 newVel = vel.lerp(desired, steer);

// -----------------------------
// SPEED CAP
// -----------------------------
            double maxSpeed = 0.6 + altitudeFactor * 2.0;
            if (newVel.length() > maxSpeed) {
                newVel = newVel.normalize().scale(maxSpeed);
            }

// -----------------------------
// APPLY
// -----------------------------
            target.setDeltaMovement(newVel);
            target.fallDistance = 0;
            target.hurtMarked = true;

            if (target instanceof Player player) {
                player.setPose(Pose.FALL_FLYING);
                player.setOnGround(false);
            }

            if (level instanceof ServerLevel server) {
                Vec3 pos = target.position();
                server.sendParticles(
                        ParticleTypes.END_ROD,
                        pos.x, pos.y, pos.z,
                        2,
                        0.1, 0.1, 0.1,
                        0.01
                );
            }


        }).execute(context, simulacrumData);
    }

    @Override
    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses() {
        return List.of(
                new DefaultGates.ClassGate.AllowedClass(
                        PlayerClassEnum.SORCERER,
                        PlayerSubClassEnum.SORCERER_CELESTIAL,
                        13
                )
        );
    }
}