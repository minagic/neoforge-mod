package com.minagic.minagic.testing.spells;
import com.minagic.minagic.Config;
import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellCasting.SpellCastContext;
import java.util.Arrays;
import java.util.List;

public class AutonomousDevSpell extends AutonomousSpell {

    @Override
    public void cast(SpellCastContext context, SimulacrumData simulacrumData) {

        System.out.println("[AutonomousDevSpell] 🔄 Tick fired for "
                + context.caster.getName().getString());

    }

    public AutonomousDevSpell() {
        super();

        this.spellName = "AutonomousDevSpell";
        this.manaCost = 40;
        this.cooldown = 10;

        this.simulacraThreshold = 20;
        this.simulacraMaxLifetime = -1; // autonomous invariant
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
