package com.minagic.minagic.scaling;

import com.minagic.minagic.common.events.custom.StatCollectEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;

public class SpellStatCollector extends StatCollector{
    public SpellStatCollector(){
        stats.put(DefaultStats.Spell.AOE_RADIUS, DefaultStats.Spell.AOE_RADIUS.defaultValue);
        contributions.put(DefaultStats.Spell.AOE_RADIUS, new ArrayList<>());
    }

    public void postEvent(){
        NeoForge.EVENT_BUS.post(new StatCollectEvent.SpellStatCollectEvent(this));
    }
}
