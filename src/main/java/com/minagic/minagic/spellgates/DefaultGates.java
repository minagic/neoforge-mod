package com.minagic.minagic.spellgates;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.*;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertManager;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DefaultGates {
    public static class ClassGate implements ISpellGate {
        private final List<AllowedClass> allowedClasses;
        private String failureMessage;
        public ClassGate(List<AllowedClass> classes) {
            this.allowedClasses = classes;
        }

        @Override
        public boolean check(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            Entity caster = ctx.caster;

            boolean anyClassMatch = false;
            boolean anySubclassMatch = false;

            PlayerClass playerData = caster.getData(ModAttachments.PLAYER_CLASS);


            for (AllowedClass allowed : allowedClasses) {
                PlayerClassEnum playerClass = playerData.getMainClass();
                for (PlayerSubClassEnum subClass : PlayerSubClassEnum.values()) {
                    int level = playerData.getSubclassLevel(subClass);

                    if (playerClass != allowed.mainClass()) {
                        continue;
                    }

                    anyClassMatch = true;

                    if (subClass != allowed.subClass()) {
                        continue;
                    }

                    anySubclassMatch = true;

                    if (level >= allowed.level()) {
                        // Full match
                        return true;
                    } else {
                        // Best case failure — level too low
                        failureMessage = allowed.mainClass().getLevelTooLowMessage();
                    }
                }
            }

            // Determine fallback message if full match not found
            if (!anyClassMatch) {
                // Worst case — wrong class
                PlayerClassEnum actualClass = playerData.getMainClass();
                failureMessage = actualClass.getUnknownSpellMessage();
            } else if (!anySubclassMatch) {
                // Mid-case — correct class, wrong subclass
                PlayerClassEnum actualClass = playerData.getMainClass();
                failureMessage = actualClass.getSubclassMismatchMessage();
            }

            return false;
        }

        @Override
        public void onFail(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            HudAlertManager.addToEntity(
                    ctx.caster,
                    failureMessage,
                    0xFF5555,
                    0,
                    60
            );
        }

        public record AllowedClass(
                PlayerClassEnum mainClass,
                PlayerSubClassEnum subClass,
                int level
        ) {
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
            var cooldowns = ctx.caster.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get());
            return cooldowns.getCooldown(ModSpells.getId(spell)) <= 0;
        }

        @Override
        public void onFail(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            HudAlertManager.addToEntity(
                    ctx.caster,
                    "Spell is on cooldown!",
                    0xFF5555,
                    0,
                    40
            );
        }

        @Override
        public void post(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            var cooldowns = ctx.caster.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get());
            cooldowns.setCooldown(ModSpells.getId(spell), cooldown);
            ctx.caster.setData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get(), cooldowns);
        }
    }

    public static class ManaGate implements ISpellGate {
        private final int manaCost;
        private final Spell spell;

        public ManaGate(int manaCost, Spell spell) {
            this.manaCost = manaCost;
            this.spell = spell;
        }

        @Override
        public boolean check(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            Minagic.LOGGER.debug("Starting mana prerequisite check for {} with {} mana cost for {}", spell, manaCost, ctx.caster);
            var mana = ctx.caster.getData(ModAttachments.MANA.get());
            Minagic.LOGGER.debug("Resolved mana attachment: {}, readings: {} / {} mana, test will {}", mana, mana.getMana(), mana.getMaxMana(), mana.getMana() >= manaCost ? "succeed." : "fail.");
            return mana.getMana() >= manaCost;
        }

        @Override
        public void onFail(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            HudAlertManager.addToEntity(
                    ctx.caster,
                    "Not enough mana to cast " + spell.getString() + ".",
                    0x3366FF, // Blue
                    0,
                    60
            );
        }

        @Override
        public void post(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            var mana = ctx.caster.getData(ModAttachments.MANA.get());
            mana.drainMana(manaCost);
            ctx.caster.setData(ModAttachments.MANA.get(), mana);
        }
    }

    public static class ManaSustainGate implements ISpellGate {
        private final int manaCost;

        public ManaSustainGate(int manaCost) {
            this.manaCost = manaCost;
        }

        @Override
        public boolean check(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            var mana = ctx.caster.getData(ModAttachments.MANA.get());
            return mana.getMana() >= manaCost;
        }

        @Override
        public void onFail(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            if (simData != null) {
                simData.expireSimulacrum();
            }
            HudAlertManager.addToEntity(
                    ctx.caster,
                    "Simulacrum ended: insufficient mana.",
                    0xAA00FF,
                    0,
                    40
            );
        }

        @Override
        public void post(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            var mana = ctx.caster.getData(ModAttachments.MANA.get());
            mana.drainMana(manaCost);
            ctx.caster.setData(ModAttachments.MANA.get(), mana);
        }
    }

    public static class SimulacrumGate implements ISpellGate {
        @Override
        public boolean check(SpellCastContext ctx, @Nullable SimulacrumData simData) {
            return simData != null && simData.remainingLifetime() != 0;
        }

        @Override
        public void onFail(SpellCastContext context, @Nullable SimulacrumData simulacrumData){
            Minagic.LOGGER.warn("Simulacrum gate failed for caster {}", context.caster.getName().getString());
        }
    }

    public static class MetadataGate implements ISpellGate {
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
