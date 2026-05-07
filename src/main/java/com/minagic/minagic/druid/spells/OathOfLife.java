package com.minagic.minagic.druid.spells;

import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.capabilities.AutoDetection;

@AutoDetection.Spell
public class OathOfLife extends AutonomousSpell {
    public OathOfLife() {
        super(AutonomousSpell.defaultProperties()
                .withManaCost(8)
                .withCooldown(0)
                .withSpellName("Oath of Life")
                .withIdName("oath_of_life")
                .withSimulacraThreshold(5)
                .withSimulacraMaxLifetime(20)
                .withTechnical(true));
    }

}
