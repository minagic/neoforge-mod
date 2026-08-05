package com.minagic.minagic.wizard.starships.spells;

import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.api.spells.ChanneledAutonomousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertAttachment;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import com.minagic.minagic.wizard.starships.utilities.PrimaryData;
import com.minagic.minagic.wizard.starships.utilities.ShipWeapons;
import org.jetbrains.annotations.Nullable;

@AutoDetection.Spell
public class FirePrimary extends ChanneledAutonomousSpell {

    public FirePrimary() {
        super(new SpellProperties(0, 0, 0, -1, -1, "Fire Primary", "fire_primary", false, false));
    }

    @Override
    public void cast(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
    }

    @Override
    public void tick(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        SpellGatePolicyGenerator.build(
                        SpellEventPhase.TICK,
                        null,
                        null,
                        null,
                        false,
                        this
                )
                .setEffect((ctx, simData) -> {
                    if (!(context.target.getVehicle() instanceof ArcaneShipEntity ship)){
                        HudAlertAttachment.addToEntity(context.caster, "Error: no ship available", 0xFFF0000, 1, 60);
                        return;
                    }
                    ShipWeapons.firePrimary(ship, (int) simulacrumData.lifetime());
                })
                .execute(context, simulacrumData);
    }
}
