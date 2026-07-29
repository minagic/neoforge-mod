package com.minagic.minagic.capabilities.powersource;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.common.events.custom.StatCollectEvent;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class WizardryPowerSourceAttachment extends AbstractPowerSource{
    public WizardryPowerSourceAttachment() {
        super(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "power_source_wizardry"));
    }

    @Override
    public boolean checkPrerequisites(@NotNull Spell spell) {
        return true;
    }

    @Override
    public boolean canConsume(@NotNull SpellCastContext context, @NotNull SimulacrumData simulacrum, int magicCost) {
        return true;
    }

    @Override
    public void consume(@NotNull SpellCastContext context, @NotNull SimulacrumData simulacrum, int magicCost) {
        // no-op for now
    }

    @Override
    public void deactivate() {

    }

    @Override
    public <T extends StatCollectEvent> void contributeTo(T event) {

    }
}
