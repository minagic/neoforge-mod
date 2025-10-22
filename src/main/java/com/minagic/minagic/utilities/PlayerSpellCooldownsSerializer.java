package com.minagic.minagic.utilities;

import com.minagic.minagic.capabilities.PlayerSpellCooldowns;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

public class PlayerSpellCooldownsSerializer implements IAttachmentSerializer<PlayerSpellCooldowns> {
    @Override
    public PlayerSpellCooldowns read(IAttachmentHolder holder, ValueInput input) {
        PlayerSpellCooldowns data = new PlayerSpellCooldowns();
        // For each key stored, read via ValueInput
        input.read("cooldowns", Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT))
                .ifPresent(map -> data.replaceAll(map));
        return data;
    }

    @Override
    public boolean write(PlayerSpellCooldowns attachment, ValueOutput output) {
        output.store("cooldowns", Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT), attachment.view());
        return true;
    }
}