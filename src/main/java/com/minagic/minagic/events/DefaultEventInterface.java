package com.minagic.minagic.events;

import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.Nullable;

public class DefaultEventInterface {
    public interface ILivingDamageSpellHandler {
            void onLivingDamage(LivingDamageEvent.Post event, SpellCastContext ctx, @Nullable SimulacrumData simData);
    }
}
