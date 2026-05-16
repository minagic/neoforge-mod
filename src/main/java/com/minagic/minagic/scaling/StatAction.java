package com.minagic.minagic.scaling;

public abstract class StatAction<T, D> {
    public abstract T apply(T object, D data);
}
