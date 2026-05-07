package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.InstantaneousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.baseProjectiles.SpellProjectileEntity;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.powersource.SorceryPowerSourceAttachment;
import com.minagic.minagic.common.registry.ModEntityTypes;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.SpellUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
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

    @AutoDetection.Spell
    public class TracerBullet extends InstantaneousSpell implements SorceryPowerSourceAttachment.ISorcerySpell {
        public TracerBullet() {
            super(InstantaneousSpell.defaultProperties()
                    .withSpellName("Tracer Bullet")
                    .withIdName("tracer_bullet")
                    .withCooldown(30)
                    .withManaCost(15));
        }

        @Override
        public void cast(SpellCastContext ctx, SimulacrumData simData) {
            // spawn a TracerBulletProjectile
            Level level = ctx.level();
            LivingEntity player = ctx.caster;


            Vec3 look = player.getLookAngle();
            Vec3 spawnPos = player.getEyePosition().add(look.scale(0.5));

            TracerBulletProjectile projectile = new TracerBulletProjectile(
                    level,
                    spawnPos,
                    look,
                    ctx
            );
            ctx.level().addFreshEntity(projectile);


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
                super(ModEntityTypes.TRACER_BULLET_PROJECTILE.get(), level);

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

        @AutoDetection.Spell
        public static class Exposure extends AutonomousSpell implements SorceryPowerSourceAttachment.ISorcerySpell {
            public Exposure() {
                super(AutonomousSpell.defaultProperties()
                        .withSpellName("Exposure")
                        .withIdName("exposure")
                        .withCooldown(0)
                        .withManaCost(0)
                        .withSimulacraThreshold(1)
                        .withSimulacraMaxLifetime(200)
                        .withTechnical(true));
            }

            @Override
            public void cast(SpellCastContext ctx, SimulacrumData simData) {
                // if context.target has invis apply glow
                Holder<MobEffect> holder = new Holder.Direct<>(MobEffects.INVISIBILITY).value();
                if (ctx.target.hasEffect(holder)) {
                    ctx.target.addEffect(
                            new MobEffectInstance(MobEffects.GLOWING, 20, 1)
                    );
                }


            }

            @Override
            public void start(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
                boolean existing = SimulacraAttachment.hasSpell(context.target, getID());
                if (existing) return;
                SimulacraAttachment.addSimulacrum(context.target, context, this, getSimulacrumThreshold(), getSimulacrumMaxLifetime());
            }

            @Override
            public String getRequiredBloodline() {
                return SorceryPowerSourceAttachment.BLOODLINE_CELESTIAL;
            }

            @Override
            public int getRequiredAffinityLevel() {
                return 3;
            }
        }

        @Override
        public String getRequiredBloodline() {
            return SorceryPowerSourceAttachment.BLOODLINE_CELESTIAL;
        }

        @Override
        public int getRequiredAffinityLevel() {
            return 3;
        }
    }
