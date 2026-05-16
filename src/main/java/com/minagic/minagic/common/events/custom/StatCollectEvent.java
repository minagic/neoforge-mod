package com.minagic.minagic.common.events.custom;

import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.scaling.SpellStat;
import com.minagic.minagic.scaling.SpellStatCollector;
import com.minagic.minagic.scaling.StatAction;
import com.minagic.minagic.scaling.StatCollector;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.neoforged.bus.api.Event;

import javax.annotation.Nullable;

public class StatCollectEvent extends Event{
    private final StatCollector collector;

    public StatCollectEvent(StatCollector collector){
        this.collector = collector;
    }

    public Spell spell(){
        return collector.getSpell();
    }

    public SpellCastContext context(){
        return collector.getContext();
    }

    public @Nullable SimulacrumData simulacrumData(){
        return collector.getSimulacrumData();
    }

    public <T, S extends SpellStat<T>, D> void contribute(S stat, StatAction<T, D> action, D data, int priority, String reason){
        collector.contribute(stat, action, data, priority, reason);
    }
    public static class SpellStatCollectEvent extends StatCollectEvent{
        public SpellStatCollectEvent(SpellStatCollector collector) {
            super(collector);
        }
    }
}
