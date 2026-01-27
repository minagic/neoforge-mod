package com.minagic.minagic.testing.spells;
import com.minagic.minagic.Config;
import com.minagic.minagic.api.spells.AutonomousChargedSpell;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellCasting.SpellCastContext;
import java.util.Arrays;
import java.util.List;

public class AutonomousChargedDevSpell extends AutonomousChargedSpell {

    @Override
    public void cast(SpellCastContext context, SimulacrumData simulacrumData) {
        System.out.println("[AutonomousChargedDevSpell] Detonated after delay! "
                + context.caster.getName().getString());

    }

    public AutonomousChargedDevSpell() {
        super();

        this.spellName = "AutonomousChargedDevSpell";
        this.manaCost = 15;
        this.cooldown = 100;

        this.simulacraThreshold = 60;
        this.simulacraMaxLifetime = 60; // autonomous charged invariant
    }

    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses() {
        if (!Config.ENABLE_DEV_SPELLS.get()) {
            return List.of();
        }

        return Arrays.stream(PlayerSubClassEnum.values())
                .map(subClass -> new DefaultGates.ClassGate.AllowedClass(
                        subClass.getParentClass(),
                        subClass,
                        0
                ))
                .toList();
    }
}
