package com.minagic.minagic.sorcerer.celestial.spells.novaburst;

import com.minagic.minagic.DamageTypes;
import com.minagic.minagic.Minagic;
import com.minagic.minagic.MinagicDamage;
import com.minagic.minagic.api.spells.AutonomousChargedSpell;
import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertManager;
import com.minagic.minagic.capabilities.hudAlerts.HudOverrideManager;
import com.minagic.minagic.capabilities.hudAlerts.HudOverrideRegistry;
import com.minagic.minagic.capabilities.hudAlerts.IHudOverride;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGateChain;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import com.minagic.minagic.utilities.MathUtils;
import com.minagic.minagic.utilities.SpellUtils;
import com.minagic.minagic.utilities.VisualUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class NovaBurst extends AutonomousChargedSpell  {


    public NovaBurst(){
        this.spellName = "Nova Burst";
        // TODO: ADD ACTUAL VALUES
        this.manaCost = 100;
        this.cooldown = 15;

        this.simulacraThreshold = 60;
    }

    public void tick(SpellCastContext context, SimulacrumData simulacrumData){
        new SpellGateChain().addGate(new DefaultGates.SimulacrumGate())
                .setEffect(
                        ((ctx, simData) ->
                        {

                            ServerLevel level = (ServerLevel) ctx.level();
                            LivingEntity target = ctx.target;

                            int t = (int) simData.remainingLifetime();

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
                                    HudAlertManager.addToEntity(
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
        SpellGatePolicyGenerator.build(SpellEventPhase.CAST, this.getAllowedClasses(), cooldown, manaCost, 0, false, this)
                .setEffect(
                        (ctx, simData) -> {
                            BlockPos blockPos = SpellUtils.getTargetBlockPos(ctx.target, 192);
                            if (blockPos == null) return;
                            NovaImpactProxyEntity proxy = Minagic.NOVA_PROXY.get().create((ServerLevel) ctx.level(), EntitySpawnReason.MOB_SUMMONED);
                            proxy.setPos(MathUtils.blockPosToVec3(blockPos));
                            proxy.setLifetime(240);
                            proxy.setCasterUUID(ctx.target.getUUID());
                            proxy.setRadius(40);

                            ((ServerLevel)ctx.level()).addFreshEntity(proxy);
                            SpellCastContext castContext = new SpellCastContext(proxy);
                            new NovaPulsePrecursor().perform(SpellEventPhase.START, castContext, null);
                        }
                )
                .execute(context, simulacrumData);
    }

    @Override
    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses(){
        return List.of(new DefaultGates.ClassGate.AllowedClass(PlayerClassEnum.SORCERER, PlayerSubClassEnum.SORCERER_CELESTIAL, 20));
    }

    public static class NovaPulse extends AutonomousSpell {

        private static final ResourceLocation WHITE_FLASH_PRIMARY =
                ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "white_flash_primary");

        private static final ResourceLocation WHITE_FLASH_SECONDARY =
                ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "white_flash_secondary");

        private static final double RADIUS = 40.0;

        public NovaPulse() {
            this.spellName = "Nova Pulse";
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

            SpellGatePolicyGenerator.build(
                            SpellEventPhase.CAST,
                            this.getAllowedClasses(),
                            null,
                            null,
                            null,
                            true,
                            this
                    )
                    .setEffect((ctx, simData) -> {
                        float progress = 1-simData.progress();


                        LivingEntity caster = ctx.target;
                        Level level = ctx.level();

                        IHudOverride primaryFlash = HudOverrideRegistry.getCodec(WHITE_FLASH_PRIMARY);

                        List<LivingEntity> entities = SpellUtils.findEntitiesInRadius(
                                level,
                                caster.position(),
                                RADIUS*progress,
                                LivingEntity.class,
                                e -> e.isAlive(),
                                Set.of(caster)
                        );

                        for (LivingEntity entity : entities) {
                            HudOverrideManager.addToEntity(entity, primaryFlash);
                            if ((ctx.target instanceof NovaImpactProxyEntity proxy)) {
                                MinagicDamage damage = new MinagicDamage(SpellUtils.resolveLivingEntityAcrossDimensions(proxy.getCasterUUID(), Objects.requireNonNull(ctx.level().getServer())),
                                        entity,
                                        ctx.target,
                                        20f,
                                        Set.of(DamageTypes.RADIANT, DamageTypes.MAGIC));
                                damage.hurt((ServerLevel) ctx.level());
                            }
                            entity.addEffect(new MobEffectInstance(new MobEffectInstance(MobEffects.DARKNESS, 400, 1)));

                        }
                        VisualUtils.spawnExplosionVFX((ServerLevel) ctx.level(), ctx.target.position(), RADIUS*progress);


//                        for (LivingEntity entity : entities) {
//                            HudOverrideManager.addToEntity(entity, secondaryFlash);
//                        }

                    })
                    .execute(context, simulacrumData);
        }

        @Override
        public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses() {
            return List.of();
        }


    }

    public static class NovaPulsePrecursor extends AutonomousChargedSpell {
        public NovaPulsePrecursor(){
            this.spellName = "Nova Pulse Precursor";
            this.simulacraThreshold = 40;

            this.isTechnical = true;
            this.manaCost = 0;
            this.cooldown = 0;
        }

        public void cast(SpellCastContext ctx, SimulacrumData simData){
            SpellCastContext context = new SpellCastContext(ctx.caster);
            new NovaPulse().perform(SpellEventPhase.START, context, null);
        }

        @Override
        public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses() {
            return List.of();
        }
    }




}
