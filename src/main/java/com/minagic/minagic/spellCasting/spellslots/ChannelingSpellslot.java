package com.minagic.minagic.spellCasting.spellslots;

import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class ChannelingSpellslot extends SimulacrumSpellSlot {
    public ChannelingSpellslot(
            UUID targetUUID,
            UUID casterUUID,
            int threshold,
            int maxLifetime,
            int originalMaxLifetime,
            Spell spell
    ) {
        super(targetUUID, casterUUID, threshold, maxLifetime, originalMaxLifetime, spell);
    }

    public ChannelingSpellslot(
            SpellCastContext context,
            int threshold,
            int maxLifetime,
            int originalMaxLifetime,
            Spell spell
    ) {
        super(context, threshold, maxLifetime, originalMaxLifetime, spell);
    }

    @Override
    public void tick() {

        if (context == null)return;
        LivingEntity target = context.target;
        System.out.println("Ticking channeling spellslot for spell: " + getSpell().getString()
                + " on target: " + target.getName().getString());
        System.out.println("MaxLifetime: " + maxLifetime + ", Threshold: " + threshold);

        lifetime ++;
        context.simulacrtumLifetime = SimulacrumSpellData.fromSlot(this);

        if (maxLifetime == 0) {
            PlayerSimulacraAttachment.clearChanneling(target);
            return;
        }

        this.getSpell().perform(SpellEventPhase.TICK, context);

        if (lifetime == threshold) {
            getSpell().perform(SpellEventPhase.CAST, context);
            lifetime = 0;
        }
        context.simulacrtumLifetime = SimulacrumSpellData.fromSlot(this);
        maxLifetime --;
    }

    public static final Codec<ChannelingSpellslot> CODEC =
            SimulacrumSpellSlot.CODEC.xmap(
                    slot -> new ChannelingSpellslot(
                            slot.targetUUID,
                            slot.casterUUID,
                            slot.getThreshold(),
                            slot.getMaxLifetime(),
                            slot.originalMaxLifetime,
                            slot.getSpell()
                    ),
                    ch -> ch // back-conversion is identity
            );
}
