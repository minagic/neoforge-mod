package com.minagic.minagic.scaling;

public class DefaultStats {
    public static class Spell{
        public static SpellStat<Float> AOE_RADIUS = new SpellStat<Float>(0.0f, "AOE RADIUS", (value) -> Math.max(value, 0));

    }
}
