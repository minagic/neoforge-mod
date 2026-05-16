package com.minagic.minagic.scaling;

import java.util.Objects;
import java.util.function.Function;

public class SpellStat<T> {
    public T defaultValue;
    public Class<T> type;
    public String name;
    public Function<T, T> validate;

    public SpellStat(T defaultValue, String name, Function<T, T> validate){
        this.defaultValue = defaultValue;
        this.name = name;
        this.validate = validate;
    }
    @SuppressWarnings("unchecked")
    public T applyValidate(Object o){
        return validate.apply((T) o);
    }
}
