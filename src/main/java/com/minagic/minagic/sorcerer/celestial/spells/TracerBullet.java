package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.InstanteneousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.api.spells.SpellValidator;
import com.minagic.minagic.baseProjectiles.SpellProjectileEntity;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.SpellUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class TracerBullet extends InstanteneousSpell {
    public TracerBullet() {
        this.spellName = "Tracer Bullet";
        this.cooldown = 30;
        this.manaCost = 15;
    }

    @Override
    public SpellValidator.CastFailureReason canCast(SpellCastContext context) {
        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getMainClass() != PlayerClassEnum.SORCERER) {
            return SpellValidator.CastFailureReason.CASTER_CLASS_MISMATCH;
        }

        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_CELESTIAL) == 0) {
            return SpellValidator.CastFailureReason.CASTER_SUBCLASS_MISMATCH;
        }

        if (context.caster.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(PlayerSubClassEnum.SORCERER_CELESTIAL) < 3) {
            return SpellValidator.CastFailureReason.CASTER_CLASS_LEVEL_TOO_LOW;
        }
        return SpellValidator.CastFailureReason.OK;
    }

    @Override
    public void cast(SpellCastContext context, SimulacrumSpellData simulacrumData) {
        // spawn a TracerBulletProjectile


        Level level = context.level();
        LivingEntity player = context.caster;


        Vec3 look = player.getLookAngle();
        Vec3 spawnPos = player.getEyePosition().add(look.scale(0.5));

        TracerBulletProjectile projectile = new TracerBulletProjectile(
                level,
                spawnPos,
                look,
                context
        );
        context.level().addFreshEntity(projectile);
    }

    public static class TracerBulletProjectile extends SpellProjectileEntity implements ItemSupplier {
        private SpellCastContext context;
        public TracerBulletProjectile(EntityType<? extends TracerBulletProjectile> type, Level level) {
            super(type, level);
            this.speed = 0f;
            this.gravity = 0.01;
            this.setNoGravity(false);
            this.context = null;
        }

        public TracerBulletProjectile(Level level, Vec3 position, Vec3 direction, SpellCastContext context) {
            super(Minagic.TRACER_BULLET_PROJECTILE.get(), level);

            this.speed = 1;
            this.gravity = 0.01;
            this.isEntityPiercing = false;
            this.setNoGravity(false);
            this.setPos(position.x, position.y, position.z);
            this.setDeltaMovement(direction.normalize().scale(this.speed));
            this.context = context;
        }

        @Override
        public void tick() {

            super.tick();
            if (context == null || context.level().isClientSide()) return;
            List<LivingEntity> targets = SpellUtils.findEntitiesInRadius(
                    this.level(),
                    this.position(),
                    10,
                    LivingEntity.class,
                    entity -> true,
                    Set.of(context.target)
            );
            for (LivingEntity target : targets) {
                SpellCastContext currentContext = new SpellCastContext(
                        context.caster,
                        target
                );

                new Exposure().perform(SpellEventPhase.START, currentContext, null);
            }
            ServerLevel world = (ServerLevel) context.level();
            world.sendParticles(ParticleTypes.FALLING_NECTAR,
                    this.position().x,
                    this.position().y,
                    this.position().z,
                    1,
                    0,
                    0,
                    0,
                    0);

        }

        @Override
        public @NotNull ItemStack getItem(){
            return new ItemStack(Items.GLOWSTONE);
        }
    }

    public static class Exposure extends AutonomousSpell {
        public Exposure() {
            this.spellName = "Exposure";
            this.cooldown = 0;
            this.manaCost = 0;
            this.simulacraThreshold = 1;
            this.simulacraMaxLifetime = 200;
            this.isTechnical = true;
        }

        @Override
        public void cast(SpellCastContext context, SimulacrumSpellData simulacrumData) {
            // if context.target has invis apply glow
            Holder<MobEffect> holder = new Holder.Direct<>(MobEffects.INVISIBILITY).value();
            if (context.target.hasEffect(holder)) {
                context.target.addEffect(
                        new MobEffectInstance(MobEffects.GLOWING, 20, 1)
                );
            }
        }

        @Override
        public void start(SpellCastContext context, @Nullable SimulacrumSpellData simulacrumData){
            // Get target's simulacra attachment
            SimulacraAttachment sim = context.target.getData(ModAttachments.PLAYER_SIMULACRA.get());

            boolean existing = sim.hasSpell(ModSpells.getId(this));
            if (existing) return;
            SimulacraAttachment.addSimulacrum(context.target, context, this, getSimulacrumThreshold(), getSimulacrumMaxLifetime());
        }
    }

}
