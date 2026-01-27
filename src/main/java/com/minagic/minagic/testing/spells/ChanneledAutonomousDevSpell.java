package com.minagic.minagic.testing.spells;


import com.minagic.minagic.Config;
import com.minagic.minagic.api.spells.ChanneledAutonomousSpell;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellCasting.SpellCastContext;
import java.util.Arrays;
import java.util.List;

public class ChanneledAutonomousDevSpell extends ChanneledAutonomousSpell {

    @Override
    public void cast(SpellCastContext context, SimulacrumData simulacrumData) {

        System.out.println("[ChanneledAutonomousDevSpell] 🔫 Pew Pew Pew! lifetime="
                + simulacrumData+ " Player=" + context.caster.getName().getString());

    }

    public ChanneledAutonomousDevSpell() {
        super();

        this.spellName = "ChanneledAutonomousDevSpell";
        this.manaCost = 2;

        this.simulacraThreshold = 5;       // once every 0.5 seconds
        this.simulacraMaxLifetime = -1;    // channeled = infinite lifetime
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
