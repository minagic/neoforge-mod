package com.minagic.minagic.druid.spells;

import com.minagic.minagic.api.spells.AutonomousSpell;

public class OathOfLife extends AutonomousSpell {
    public OathOfLife() {
        this.manaCost = 8;
        this.cooldown = 0;
        this.spellName = "Oath of Life";
        this.simulacraThreshold = 5;
        this.simulacraMaxLifetime = 20; // 1 second (20 ticks)
        this.isTechnical = true;
    }

}
