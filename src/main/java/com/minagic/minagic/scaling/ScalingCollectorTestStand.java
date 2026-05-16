package com.minagic.minagic.scaling;

public class ScalingCollectorTestStand {

    public static void main(String[] args){
        SpellStatCollector collector = new SpellStatCollector();

        collector.contribute(DefaultStats.Spell.AOE_RADIUS, new DefaultActions.OVERRIDE<>(), 5.f, 1, "testing scaling system");
        collector.beginCollection();
        collector.contribute(DefaultStats.Spell.AOE_RADIUS, new DefaultActions.MULTIPLY(), 2.f, 10, "testing scaling system");
        collector.contribute(DefaultStats.Spell.AOE_RADIUS, new DefaultActions.ADD(), 5.f, 1, "testing scaling system");
        collector.contribute(DefaultStats.Spell.AOE_RADIUS, new DefaultActions.ADD(), 5.f, 1, "testing scaling system");
        System.out.println(collector.dump());
        collector.resolve();
        System.out.println("Resulting value: "+ ((Float)collector.getStat(DefaultStats.Spell.AOE_RADIUS)).toString());

    }
}
