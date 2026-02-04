package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.InstanteneousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.baseProjectiles.SpellProjectileEntity;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerClassEnum;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
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

    public List<DefaultGates.ClassGate.MagicClassEntry> getAllowedClasses() {
        return List.of(new DefaultGates.ClassGate.MagicClassEntry(
                PlayerClassEnum.SORCERER,
                PlayerSubClassEnum.SORCERER_CELESTIAL,
                3
        ));
    }

    @Override
    public void cast(SpellCastContext ctx, SimulacrumData simData) {
        // spawn a TracerBulletProjectile
        SpellGatePolicyGenerator.build(SpellEventPhase.CAST, this.getAllowedClasses(), null, manaCost, null, false, this)
                .setEffect((context, simulacrumData) -> {
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
                })
                .execute(ctx, simData);


    }

    public static class TracerBulletProjectile extends SpellProjectileEntity implements ItemSupplier {
        private final SpellCastContext context;

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
        public @NotNull ItemStack getItem() {
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
        public void cast(SpellCastContext ctx, SimulacrumData simData) {
            // if context.target has invis apply glow
            SpellGatePolicyGenerator.build(SpellEventPhase.CAST, this.getAllowedClasses(), null, manaCost, null, false, this)
                    .setEffect((context, simulacrumData) -> {
                        Holder<MobEffect> holder = new Holder.Direct<>(MobEffects.INVISIBILITY).value();
                        if (context.target.hasEffect(holder)) {
                            context.target.addEffect(
                                    new MobEffectInstance(MobEffects.GLOWING, 20, 1)
                            );
                        }
                    })
                    .execute(ctx, simData);


        }

        @Override
        public void start(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
            SpellGatePolicyGenerator.build(SpellEventPhase.START, this.getAllowedClasses(), this.cooldown, this.manaCost, 0, false, this).setEffect(
                    ((ctx, simData) -> {
                        boolean existing = SimulacraAttachment.hasSpell(ctx.target, ModSpells.getId(this));
                        if (existing) return;
                        SimulacraAttachment.addSimulacrum(ctx.target, ctx, this, getSimulacrumThreshold(), getSimulacrumMaxLifetime());
                    })

            ).execute(context, simulacrumData);

        }
    }

}
