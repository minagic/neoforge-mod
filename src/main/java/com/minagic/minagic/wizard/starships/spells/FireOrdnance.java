package com.minagic.minagic.wizard.starships.spells;

import com.minagic.minagic.api.spells.InstantaneousSpell;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertAttachment;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import com.minagic.minagic.wizard.starships.utilities.ShipWeapons;
import org.jetbrains.annotations.Nullable;

@AutoDetection.Spell
public class FireOrdnance extends InstantaneousSpell {

    public FireOrdnance() {
        super(new Spell.SpellProperties(0, 0, 0, -1, -1, "Fire Ordnance", "fire_ordnance", false, false));}


    @Override
    public void cast(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        SpellGatePolicyGenerator.build(
                        SpellEventPhase.CAST,
                        null,
                        0,
                        null,
                        false,
                        this
                )
                .setEffect((ctx, simData) -> {
                    if (!(context.target.getVehicle() instanceof ArcaneShipEntity ship)){
                        HudAlertAttachment.addToEntity(context.caster, "Error: no ship available", 0xFFF0000, 1, 60);
                        return;
                    }
                    ShipWeapons.fireOrdnance(ship);
                })
                .execute(context, simulacrumData);
    }
}
