package com.minagic.minagic.abstractionLayer;

import com.minagic.minagic.spells.ISpell;

public abstract class Spell implements ISpell {
    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
