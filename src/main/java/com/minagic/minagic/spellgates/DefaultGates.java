package com.minagic.minagic.spellgates;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerClassEnum;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.*;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertAttachment;
import com.minagic.minagic.capabilities.powersource.AbstractPowerSource;
import com.minagic.minagic.capabilities.powersource.ActivePowerSourceAttachment;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DefaultGates {
    public static class PowerSourcePrerequisiteGate implements ISpellGate {

        private final Spell spell;
        private String failureMessage;

        public PowerSourcePrerequisiteGate(Spell spell) {
            this.spell = spell;
        }

        @Override
        public boolean check(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            AbstractPowerSource source =
                    ActivePowerSourceAttachment.getActivePowerSource(ctx.caster);

            if (source == null) {
                failureMessage = "No active power source.";
                return false;
            }

            if (!source.checkPrerequisites(spell)) {
                failureMessage = "The invoked power rejects this spell.";
                return false;
            }

            return true;
        }

        @Override
        public void onFail(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            HudAlertAttachment.addToEntity(
                    ctx.caster,
                    failureMessage,
                    0xFF5555,
                    0,
                    60
            );
        }
    }

    public static class PowerSourceCostGate implements ISpellGate {

        private final int magicCost;
        private final Spell spell;

        public PowerSourceCostGate(int magicCost, Spell spell) {
            this.magicCost = magicCost;
            this.spell = spell;
        }

        @Override
        public boolean check(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            AbstractPowerSource source =
                    ActivePowerSourceAttachment.getActivePowerSource(ctx.caster);

            if (source == null) {
                return false;
            }

            return source.canConsume(ctx, simData, magicCost);
        }

        @Override
        public void onFail(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            AbstractPowerSource source =
                    ActivePowerSourceAttachment.getActivePowerSource(ctx.caster);

            if (source != null) {
                source.onFail(ctx.caster, magicCost);
                return;
            }

            HudAlertAttachment.addToEntity(
                    ctx.caster,
                    "You lack the power to cast " + spell.getString() + ".",
                    0x3366FF,
                    0,
                    60
            );
        }

        @Override
        public void post(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            AbstractPowerSource source =
                    ActivePowerSourceAttachment.getActivePowerSource(ctx.caster);

            if (source != null) {
                source.consume(ctx, simData, magicCost);
            }
        }
    }

    public static class PowerSourceSustainGate implements ISpellGate {

        private final int magicCost;

        public PowerSourceSustainGate(int magicCost) {
            this.magicCost = magicCost;
        }

        @Override
        public boolean check(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            AbstractPowerSource source =
                    ActivePowerSourceAttachment.getActivePowerSource(ctx.caster);

            if (source == null) {
                return false;
            }

            return source.canConsume(ctx, simData, magicCost);
        }

        @Override
        public void onFail(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            if (simData != null) {
                simData.expireSimulacrum();
            }

            AbstractPowerSource source =
                    ActivePowerSourceAttachment.getActivePowerSource(ctx.caster);

            if (source != null) {
                source.onFail(ctx.caster, magicCost);
                return;
            }

            HudAlertAttachment.addToEntity(
                    ctx.caster,
                    "The sustaining power collapses.",
                    0xAA00FF,
                    0,
                    40
            );
        }

        @Override
        public void post(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            AbstractPowerSource source =
                    ActivePowerSourceAttachment.getActivePowerSource(ctx.caster);

            if (source != null) {
                source.consume(ctx, simData, magicCost);
            }
        }
    }

    public static class CooldownGate implements ISpellGate {
        private final Spell spell;
        private final int cooldown;

        public CooldownGate(Spell spell, int cooldown) {
            this.spell = spell;
            this.cooldown = cooldown;
        }

        @Override
        public boolean check(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            return !CooldownAttachment.isOnCooldown(ctx.caster, spell.getID());
        }

        @Override
        public void onFail(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            HudAlertAttachment.addToEntity(
                    ctx.caster,
                    "Spell is on cooldown!",
                    0xFF5555,
                    0,
                    40
            );
        }

        @Override
        public void post(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            CooldownAttachment.applyCooldown(ctx.caster, spell.getID(), cooldown);
        }
    }

    public static class SimulacrumGate extends ISpellGate.SafetySpellGate {

        private static final String REASON_NULL = "SimulacrumData is null";
        private static final String REASON_EXPIRED = "Simulacrum remaining lifetime is 0";
        private static final String REASON_THRESHOLD = "Simulacrum lifetime exceeds threshold";
        private static final String REASON_MAX_LIFETIME = "Simulacrum lifetime exceeds maxLifetime";
        private static final String REASON_REMAINING_MAX = "Simulacrum remaining lifetime exceeds maxLifetime";

        private String lastFailureReason = null;

        @Override
        public boolean check(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            lastFailureReason = getFailureReason(simData);
            return lastFailureReason == null;
        }

        private @Nullable String getFailureReason(@Nullable SimulacrumData simData) {
            if (simData == null) {
                return REASON_NULL;
            }

            if (simData.remainingLifetime() == 0) {
                return REASON_EXPIRED;
            }

            if (simData.lifetime() > simData.maxLifetime()) {
                return REASON_MAX_LIFETIME;
            }

            if (simData.remainingLifetime() > simData.maxLifetime()) {
                return REASON_REMAINING_MAX;
            }

            return null; // all good
        }

         @Override
        public void onFail(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
            Minagic.LOGGER.warn(
                    "Simulacrum gate failed for caster {}: {}",
                    context.caster.getName().getString(),
                    lastFailureReason != null ? lastFailureReason : "Unknown reason"
            );
        }
    }

    public static class MetadataGate extends ISpellGate.SafetySpellGate{
        private final Spell spell;
        private final List<String> requiredKeys;
        private final boolean exitSimulacrumOnFail;

        public MetadataGate(Spell spell, List<String> requiredKeys, boolean exitSimulacrumOnFail) {
            this.spell = spell;
            this.requiredKeys = requiredKeys;
            this.exitSimulacrumOnFail = exitSimulacrumOnFail;
        }

        @Override
        public boolean check(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            for (String key : requiredKeys) {
                if (SpellMetadata.has(ctx.target, spell, key)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onFail(SpellCastContext ctx, SimulacrumData simData) {
            if (this.exitSimulacrumOnFail) {
                simData.expireSimulacrum();
            }
        }
    }

}
