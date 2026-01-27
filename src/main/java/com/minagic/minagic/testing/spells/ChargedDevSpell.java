package com.minagic.minagic.testing.spells;

import com.minagic.minagic.Config;
import com.minagic.minagic.api.spells.ChargedSpell;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellCasting.SpellCastContext;
import java.util.Arrays;
import java.util.List;

public class ChargedDevSpell extends ChargedSpell {

    @Override
    public void cast(SpellCastContext context, SimulacrumData simulacrumData) {
        float chargeRatio = (float) simulacrumData.lifetime() / simulacrumData.maxLifetime();
        System.out.println("[ChargedDevSpell] ⚡ BOOM! ChargeRatio=" + chargeRatio
                + " Player=" + context.caster.getName().getString());
    }

    public ChargedDevSpell() {
        super();

        this.spellName = "ChargedDevSpell";
        this.manaCost = 30;
        this.cooldown = 60;

        this.simulacraThreshold = 0;
        this.simulacraMaxLifetime = 100; // charge time / max lifetime
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
