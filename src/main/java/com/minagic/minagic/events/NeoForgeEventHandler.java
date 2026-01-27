package com.minagic.minagic.events;

import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellCasting.spellslots.SimulacrumSpellSlot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;

@EventBusSubscriber
public class NeoForgeEventHandler {
    @SubscribeEvent
    public static void onLivingHurtPost(LivingDamageEvent.Post event) {

        LivingEntity target = event.getEntity();

        List<SimulacrumSpellSlot> simulacra = SimulacraAttachment.getAllSpellslots(target);

        for (SimulacrumSpellSlot slot : simulacra) {
            Spell spell = slot.getSpell();

            if (spell instanceof DefaultEventInterface.ILivingDamageSpellHandler handler) {
                slot.resolveContext(((ServerLevel) target.level()).getServer());

                SpellCastContext context = slot.getContext(); // Or reconstruct if needed
                SimulacrumData data = slot.getSpellData();
                if (context == null) return;
                if (!context.validate()) return;

                try {
                    handler.onLivingDamage(event, context, data);
                } catch (Exception ex) {
                    System.err.println("Exception in onLivingDamage for spell " + spell.getString());
                }
            }
        }

    }
}
