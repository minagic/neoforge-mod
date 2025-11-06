package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.abstractionLayer.spells.InstanteneousSpell;
import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class InstantFireballImbueSpell extends InstanteneousSpell {

    @Override
    public void cast(SpellCastContext ctx) {
        LivingEntity caster = ctx.caster;

        // Compute target yourself (raycast)
        LivingEntity target = findLookTarget(caster, 32);
        if (target == null) {
            return; // fail silently or add a "no target" message
        }

        // Fancy particle line
        spawnBeamParticles(caster, target);

        // Apply the forced fireball barrage
        applyForcedFireballBarrage(target, ctx);
    }

    @Override
    public int getManaCost() {
        return 20;
    }

    @Override
    public int getCooldownTicks() {
        return 30;
    }


    // ============================================================
    // Target: raycast to find entity in line of sight
    // ============================================================

    private LivingEntity findLookTarget(LivingEntity caster, double maxDist) {
        Level level = caster.level();

        Vec3 eye = caster.getEyePosition();
        Vec3 look = caster.getLookAngle();
        Vec3 end = eye.add(look.scale(maxDist));

        AABB box = caster.getBoundingBox().expandTowards(look.scale(maxDist)).inflate(1.0);

        LivingEntity best = null;
        double bestDist = maxDist * maxDist;

        for (Entity e : level.getEntities(caster, box, ent -> ent instanceof LivingEntity && ent != caster)) {
            AABB hitbox = e.getBoundingBox().inflate(0.3);
            Optional<Vec3> clip = hitbox.clip(eye, end);

            if (clip.isPresent()) {
                double d = eye.distanceToSqr(clip.get());
                if (d < bestDist) {
                    bestDist = d;
                    best = (LivingEntity) e;
                }
            }
        }

        return best;
    }


    // ============================================================
    // Particles
    // ============================================================

    private void spawnBeamParticles(LivingEntity caster, LivingEntity target) {
        Level level = caster.level();
        if (level.isClientSide()) return;

        Vec3 from = caster.getEyePosition();
        Vec3 to = target.getEyePosition();
        Vec3 diff = to.subtract(from);

        int steps = (int) (diff.length() * 4);
        Vec3 step = diff.normalize().scale(diff.length() / steps);

        for (int i = 0; i < steps; i++) {
            Vec3 pos = from.add(step.scale(i));
            ((ServerLevel) level).sendParticles(
                    ParticleTypes.END_ROD,
                    pos.x, pos.y, pos.z,
                    1, 0, 0, 0, 0.0
            );
        }
    }


    // ============================================================
    // The actual effect
    // ============================================================

    private void applyForcedFireballBarrage(LivingEntity target, SpellCastContext context) {
        context.target = target;
        PlayerSimulacraAttachment.addSimulacrum(
                context,
                new FireballBarrage(),
                5, // threshold
                200 // max lifetime
        );

    }


    @Override
    public String getString() {
        return "Fireball Barrage Imbue";
    }
}
