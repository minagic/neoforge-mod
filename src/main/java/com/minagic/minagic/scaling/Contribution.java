package com.minagic.minagic.scaling;

public class Contribution <T, D>{
    private final SpellStat<T> stat;
    private final StatAction<T, D> action;
    private final D data;
    private final int priority;
    private final int index;
    private final String reason;

    public Contribution(SpellStat<T> stat, StatAction<T, D> action, D data, int priority, int index, String reason){
        this.stat = stat;
        this.action = action;
        this.data = data;
        this.priority = priority;
        this.index = index;
        this.reason = reason;
    }
    @SuppressWarnings("unchecked")
    public T apply(Object object){
        return action.apply((T)object, data);
    }


    public int getPriority(){return priority;}

    public int getIndex(){return index;}

    public String describe(){
        return """
                Contribution for stat %s
                With action: %s
                Action data: %s
                Priority %d / %d
                Reason: %s
                """.formatted(stat.name, action.getClass(), data.toString(), priority, index, reason);
    }
}
