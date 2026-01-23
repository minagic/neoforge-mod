package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacrumData;

public interface ISimulacrumSpell {
    int getSimulacrumThreshold();
    int getSimulacrumMaxLifetime();
    float progress(SimulacrumData data);
}
