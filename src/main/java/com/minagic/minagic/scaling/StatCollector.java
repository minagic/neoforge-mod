package com.minagic.minagic.scaling;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public abstract class StatCollector {
    protected HashMap<SpellStat<?>, Object> stats = new HashMap<>();
    protected HashMap<SpellStat<?>, List<Contribution<?, ?>>> contributions = new HashMap<>();
    protected boolean frozen = true;

    protected SpellCastContext context;
    protected SimulacrumData simulacrumData;
    protected Spell spell;

    public <T, S extends SpellStat<T>, D> void contribute(S stat, StatAction<T, D> action, D data, int priority, String reason){
        if (frozen){
            Minagic.LOGGER.error("StatCollector is frozen");
            return;
        }
        if (!stats.containsKey(stat) || !contributions.containsKey(stat)){
            Minagic.LOGGER.error("Stat not found: {}", stat.name);
            return;
        }
        List<Contribution<?, ?>> list = contributions.get(stat);
        Contribution<T, D> contribution = new Contribution<>(stat, action, data, priority, list.size(), reason);
        list.add(contribution);
    }

    public void injectContext(Spell spell, SpellCastContext context, @Nullable SimulacrumData simData){
        this.spell = spell;
        this.context = context;
        this.simulacrumData = simData;
    }

    public void beginCollection(){
        if (this.spell == null){
            Minagic.LOGGER.error("Spell is not set, refusing collection");
            return;
        }
        if (this.context == null){
            Minagic.LOGGER.error("Context is not set, refusing collection");
            return;
        }
        if (!context.validate()){
            Minagic.LOGGER.error("Context is invalid, refusing collection");
            return;
        }
        if (simulacrumData!= null && !simulacrumData.validate()){
            Minagic.LOGGER.error("Simulacrum data present but is invalid, refusing collection");
            return;
        }
        this.frozen = false;
        postEvent();
        resolve();
    }

    public abstract void postEvent();

    public void resolve(){

        for (SpellStat<?> spellStat : stats.keySet()){
            List<Contribution<?, ?>> current = contributions.get(spellStat);
            current.sort(Comparator
                    .comparingInt(value -> ((Contribution<?, ?>)value).getPriority()).thenComparing(value -> ((Contribution<?, ?>)value).getIndex()));
            for (Contribution<?, ?> contribution: current){
                Object object = getStat(spellStat);
                setStat(spellStat, contribution.apply(object));
            }
        }
        this.frozen = true;
    }
    @SuppressWarnings("unchecked")
    public <T> T getStat(SpellStat<T> spellStat){
        if (!this.frozen){
            Minagic.LOGGER.warn("StatCollector is accessed but is not frozen. It may produce unexpected results");
        }
        return (T) stats.get(spellStat);
    }

    private  <T> void setStat(SpellStat<?> stat, Object value){
        if (frozen){
            Minagic.LOGGER.error("StatCollector is frozen");
            return;
        }
        unsafeSetStat(stat,value);
    }

    private void unsafeSetStat(SpellStat<?> stat, Object value) {
        stats.put(stat, stat.applyValidate(value));
    }

    public String dump(){
        StringBuilder res = new StringBuilder("=== BEGINNING STAT COLLECTOR DUMP ===\n");
        res.append("STATS:\n");
        for(SpellStat<?> stat: stats.keySet()){
            res.append("%s: %s\n".formatted(stat.name, getStat(stat).toString()));
        }
        res.append("CONTRIBUTIONS:\n");
        for(SpellStat<?> stat: contributions.keySet()){
            res.append(stat.name);
            res.append(":\n");
            for(Contribution<?, ?> contribution : contributions.get(stat)){
                res.append(contribution.describe());
                res.append("\n");
            }
        }
        return res.toString();
    }


    public Spell getSpell(){
        return this.spell;
    }

    public SpellCastContext getContext(){
        return this.context;
    }

    public @Nullable SimulacrumData getSimulacrumData(){
        return this.simulacrumData;
    }
}
