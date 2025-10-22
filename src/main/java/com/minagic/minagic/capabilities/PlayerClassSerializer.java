package com.minagic.minagic.capabilities;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.Optional;

public class PlayerClassSerializer implements IAttachmentSerializer<PlayerClass> {
    private static final String KEY = "playerClass";

    // Use the same Codec as defined earlier
    private static final Codec<PlayerClassEnum> ENUM_CODEC =
            Codec.STRING.xmap(PlayerClassEnum::valueOf, PlayerClassEnum::name);

    @Override
    public PlayerClass read(IAttachmentHolder holder, ValueInput input) {
        PlayerClass data = new PlayerClass();
        input.read(KEY, ENUM_CODEC).ifPresent(data::setPlayerClass);
        return data;
    }

    @Override
    public boolean write(PlayerClass attachment, ValueOutput output) {
        output.store(KEY, ENUM_CODEC, attachment.getPlayerClass());
        return true;
    }
}
