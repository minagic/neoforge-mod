package com.minagic.minagic.sorcerer.celestial.spells.novaburst;

import com.minagic.minagic.DamageTypes;
import com.minagic.minagic.Minagic;
import com.minagic.minagic.MinagicDamage;
import com.minagic.minagic.api.spells.AutonomousChargedSpell;
import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.GatedSpell.SpellPolicyData;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.powersource.SorceryPowerSourceAttachment;
import com.minagic.minagic.capabilities.hudAlerts.*;
import com.minagic.minagic.common.registry.ModEntityTypes;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGateChain;
import com.minagic.minagic.utilities.MathUtils;
import com.minagic.minagic.utilities.SpellUtils;
import com.minagic.minagic.utilities.VisualUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@AutoDetection.Spell
public class NovaBurst extends AutonomousChargedSpell implements SorceryPowerSourceAttachment.ISorcerySpell {


    public NovaBurst(){
        this.spellName = "Nova Burst";
        this.idName = "nova_burst";
        // TODO: ADD ACTUAL VALUES
        this.manaCost = 100;
        this.cooldown = 15;

        this.simulacraThreshold = 60;
    }

    public void tick(SpellCastContext context, SimulacrumData simulacrumData){
        new SpellGateChain(this).addGate(new DefaultGates.SimulacrumGate())
                .setEffect(
                        ((ctx, simData) ->
                        {

                            ServerLevel level = (ServerLevel) ctx.level();
                            LivingEntity target = ctx.target;

                            @SuppressWarnings("DataFlowIssue") int t = (int) simData.remainingLifetime();

                            // Live LOS targeting (do NOT cache)
                            BlockPos impact = SpellUtils.getTargetBlockPos(target, 192);
                            if (impact == null) return;

                            double ix = impact.getX() + 0.5;
                            double iy = impact.getY() + 0.5;
                            double iz = impact.getZ() + 0.5;

                            // =========================
                            // PHASE 1: Targeting Beam
                            // =========================
                            if (t > 40) {

                                // Orbital ray from sky
                                for (int i = 0; i < 6; i++) {
                                    level.sendParticles(
                                            ParticleTypes.END_ROD,
                                            ix,
                                            iy + 20 + i * 2,
                                            iz,
                                            1,
                                            0, 0, 0,
                                            0
                                    );
                                }

                                // Rotating ground reticle
                                double radius = 3 + Math.sin(level.getGameTime() * 0.2) * 0.5;

                                for (int i = 0; i < 16; i++) {
                                    double angle = i * Math.PI * 2 / 16;
                                    level.sendParticles(
                                            ParticleTypes.GLOW,
                                            ix + Math.cos(angle) * radius,
                                            iy + 0.1,
                                            iz + Math.sin(angle) * radius,
                                            1,
                                            0, 0, 0,
                                            0
                                    );
                                }
                            }


                            if (t <= 40 && t > 0) {

                                // Blinking HUD alert every 10 ticks
                                if (t % 10 == 0) {
                                    HudAlertAttachment.addToEntity(
                                            target,
                                            "TARGET ACQUIRED. DETONATING.",
                                            0xFFFF3333,
                                            0,
                                            10
                                    );
                                }

                            }
                        })
                )
                .execute(context, simulacrumData);
    }

    public void cast(SpellCastContext context, SimulacrumData simulacrumData){
        BlockPos blockPos = SpellUtils.getTargetBlockPos(context.target, 192);
        if (blockPos == null) return;
        NovaImpactProxyEntity proxy = ModEntityTypes.NOVA_PROXY.get().create(context.level(), EntitySpawnReason.MOB_SUMMONED);
        BlockPos finalBlockPos = new BlockPos(blockPos.getX(), (int) SpellUtils.findSurfaceY(context.level(), blockPos.getX(), blockPos.getZ()) + 20, blockPos.getZ());
        assert proxy != null;
        proxy.setPos(MathUtils.blockPosToVec3(finalBlockPos));
        proxy.setLifetime(240);
        proxy.setCasterUUID(context.target.getUUID());
        proxy.setRadius(40);

        context.level().addFreshEntity(proxy);
        SpellCastContext castContext = new SpellCastContext(proxy);
        new NovaPulsePrecursor().perform(SpellEventPhase.START, castContext, null);
    }

    @AutoDetection.Spell
    public static class NovaPulse extends AutonomousSpell implements SorceryPowerSourceAttachment.ISorcerySpell {

        private static final ResourceLocation WHITE_FLASH_PRIMARY =
                ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "white_flash_primary");

        private static final ResourceLocation WHITE_FLASH_SECONDARY =
                ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "white_flash_secondary");

        private static final double RADIUS = 40.0;

        public NovaPulse() {
            this.spellName = "Nova Pulse";
            this.idName = "nova_pulse";
            this.isTechnical = true;
            this.manaCost = 0;
            this.cooldown = 0;
            this.simulacraMaxLifetime = 200;
            this.simulacraThreshold = 5;
        }

        // =========================================
        // START — primary flash to nearby entities
        // =========================================
        @Override
        public void start(SpellCastContext context, SimulacrumData simulacrumData) {
            System.out.println("NovaPulse sequence attempting to start");
            super.start(context, simulacrumData);
            System.out.println("NovaPulse sequence start attempt complete");
            SimulacraAttachment sim = context.target.getData(ModAttachments.PLAYER_SIMULACRA);
        }

        // =========================================
        // CAST — secondary flash pulse
        // =========================================
        @Override
        public void cast(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {

            @SuppressWarnings("DataFlowIssue") float progress = 1 - simulacrumData.progress();

            LivingEntity caster = context.target;
            Level level = context.level();

            List<LivingEntity> entities = SpellUtils.findEntitiesInRadius(
                    level,
                    caster.position(),
                    5 + (RADIUS - 5) * progress * progress,
                    LivingEntity.class,
                    LivingEntity::isAlive,
                    Set.of(caster)
            );

            for (LivingEntity entity : entities) {
                WhiteFlashAttachment.start(entity, 200);
                if ((context.target instanceof NovaImpactProxyEntity proxy)) {
                    MinagicDamage damage = new MinagicDamage(SpellUtils.resolveLivingEntityAcrossDimensions(proxy.getCasterUUID(), Objects.requireNonNull(context.level().getServer())),
                            entity,
                            context.target,
                            20f,
                            Set.of(DamageTypes.RADIANT, DamageTypes.MAGIC));
                    damage.hurt((ServerLevel) context.level());
                }
                entity.addEffect(new MobEffectInstance(new MobEffectInstance(MobEffects.DARKNESS, 400, 1)));

            }
            //VisualUtils.spawnExplosionVFX((ServerLevel) ctx.level(), ctx.target.position(), RADIUS*progress);
        }

        @Override
        protected SpellPolicyData getPolicyData(SpellEventPhase phase) {
            SpellPolicyData base = super.getPolicyData(phase);
            if (phase == SpellEventPhase.CAST) {
                return new SpellPolicyData(
                        base.cooldownTicks(),
                        base.manaCostOnCast(),
                        base.manaSustainPerTick(),
                        true
                );
            }
            return base;
        }
        @Override
        public String getRequiredBloodline() {
            return SorceryPowerSourceAttachment.BLOODLINE_CELESTIAL;
        }

        @Override
        public int getRequiredAffinityLevel() {
            return 20;
        }

    }

    @AutoDetection.Spell
    public static class NovaPulsePrecursor extends AutonomousChargedSpell implements SorceryPowerSourceAttachment.ISorcerySpell {
        public NovaPulsePrecursor(){
            this.spellName = "Nova Pulse Precursor";
            this.idName = "nova_pulse_precursor";
            this.simulacraThreshold = 40;

            this.isTechnical = true;
            this.manaCost = 0;
            this.cooldown = 0;
        }

        public void cast(SpellCastContext ctx, SimulacrumData simData){
            SpellCastContext context = new SpellCastContext(ctx.caster);
            new NovaPulse().perform(SpellEventPhase.START, context, null);
            if (! (ctx.target instanceof NovaImpactProxyEntity novaProxy)) return;
            @SuppressWarnings("DataFlowIssue") LivingEntity player = SpellUtils.resolveLivingEntityAcrossDimensions(novaProxy.getCasterUUID(), ctx.level().getServer());
            Vec3 dir = player.position().subtract(ctx.target.position()).normalize();
            player.push(dir.x * 0.5, 0.4, dir.z * 0.5);
            ctx.level().playSound(ctx.target, ctx.target.getOnPos(), SoundEvents.ENDER_DRAGON_DEATH, SoundSource.HOSTILE, 1.5f, 1.2f);
            ctx.level().playSound(ctx.target, ctx.target.getOnPos(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 1.5f, 1.2f);

        }

        public void tick(SpellCastContext context, SimulacrumData simulacrumData){
            new SpellGateChain(this).addGate(new DefaultGates.SimulacrumGate())
                    .setEffect(
                            (ctx, simData)->
                            {
                                RandomSource rand = ctx.level().random;
                                @SuppressWarnings("DataFlowIssue") double angle = (1-simData.progress())*2*Math.PI;
                                Vec3 origin = ctx.target.position();
                                Vec3[] targets = MathUtils.twoVectorsWithAngle(origin, angle, rand.nextDouble()*9+6, rand);
                                for (Vec3 target: targets){
                                    VisualUtils.createParticleRay((ServerLevel) ctx.level(), origin, target, ParticleTypes.SOUL_FIRE_FLAME, 100);
                                }
                                ctx.level().playSound(ctx.target, ctx.target.getOnPos(), SoundEvents.BEACON_AMBIENT, SoundSource.AMBIENT, 3f, 0.5f + 3*simData.progress());
                            }
                    )
                    .execute(context,simulacrumData);
        }
        @Override
        public String getRequiredBloodline() {
            return SorceryPowerSourceAttachment.BLOODLINE_CELESTIAL;
        }

        @Override
        public int getRequiredAffinityLevel() {
            return 20;
        }
    }




    @Override
    public String getRequiredBloodline() {
        return SorceryPowerSourceAttachment.BLOODLINE_CELESTIAL;
    }

    @Override
    public int getRequiredAffinityLevel() {
        return 20;
    }
}
