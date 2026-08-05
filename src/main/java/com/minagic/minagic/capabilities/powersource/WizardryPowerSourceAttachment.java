package com.minagic.minagic.capabilities.powersource;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.common.events.custom.StatCollectEvent;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

public class WizardryPowerSourceAttachment extends AbstractPowerSource {
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




    public boolean shouldRender(LivingEntity host) {
        return true;
    }


    public static class Serializer implements IAttachmentSerializer<WizardryPowerSourceAttachment> {

        @Override
        public WizardryPowerSourceAttachment read(IAttachmentHolder holder, ValueInput input) {
            return new WizardryPowerSourceAttachment();
        }

        @Override
        public boolean write(WizardryPowerSourceAttachment attachment, ValueOutput output) {
            return true;
        }
    }
}
