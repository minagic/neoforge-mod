package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.api.spells.InstanteneousSpell;
import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.registries.ModAttachments;
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

    public InstantFireballImbueSpell() {
        super();

        this.spellName = "Fireball Barrage Imbue";
        this.manaCost = 20;
        this.cooldown = 30;
        // no simulacrum values provided → defaults apply
    }

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
        PlayerClass targetClass = target.getData(ModAttachments.PLAYER_CLASS);
        targetClass.setMainClass(PlayerClassEnum.SORCERER);
        targetClass.setSubclassLevel(PlayerSubClassEnum.SORCERER_INFERNAL, 20);
        target.setData(ModAttachments.PLAYER_CLASS, targetClass);
        System.out.println("Applying forced fireball barrage to target: " + target);
        SimulacraAttachment.addSimulacrum(
                target,
                new SpellCastContext(target, context.caster),
                new FireballBarrage(),
                20, // threshold
                200 // max lifetime
        );

    }
}
