package com.minagic.minagic.api.spells;

import com.minagic.minagic.capabilities.SimulacrumSpellData;

public interface ISimulacrumSpell {
    int getSimulacrumThreshold();
    int getSimulacrumMaxLifetime();
    float progress(SimulacrumSpellData data);
}
