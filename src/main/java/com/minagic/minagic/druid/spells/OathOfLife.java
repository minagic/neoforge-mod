package com.minagic.minagic.druid.spells;

import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.capabilities.AutoDetection;

@AutoDetection.Spell
public class OathOfLife extends AutonomousSpell {
    public OathOfLife() {
        this.manaCost = 8;
        this.cooldown = 0;
        this.spellName = "Oath of Life";
        this.idName = "oath_of_life";
        this.simulacraThreshold = 5;
        this.simulacraMaxLifetime = 20; // 1 second (20 ticks)
        this.isTechnical = true;
    }

}
