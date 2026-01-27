package com.minagic.minagic.testing.spells;

import com.minagic.minagic.Config;
import com.minagic.minagic.api.spells.InstanteneousSpell;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellCasting.SpellCastContext;
import java.util.Arrays;
import java.util.List;

public class InstantaneousDevSpell extends InstanteneousSpell {

    @Override
    public void cast(SpellCastContext context, SimulacrumData simulacrumData) {

        System.out.println("[InstantaneousDevSpell] Cast by " + context.caster.getName().getString()
                + " at " + context.level().dimension().location());
    }

    public InstantaneousDevSpell() {
        super();

        this.spellName = "InstantaneousDevSpell";
        this.manaCost = 10;
        this.cooldown = 40;

        this.simulacraThreshold = 0;
        this.simulacraMaxLifetime = 0;
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
