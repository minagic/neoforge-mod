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

    @Override
    public PlayerClass read(IAttachmentHolder holder, ValueInput input) {
        PlayerClass result = new PlayerClass();
        input.read("player_class", PlayerClass.CODEC)
                .ifPresentOrElse(
                        pc -> {
                            result.setMainClass(pc.getMainClass());
                            pc.getAllSubclasses().forEach(result::setSubclassLevel);
                            pc.getDeity().ifPresent(result::setDeity);
                        },
                        () -> {
                            // fallback or leave default UNDECLARED
                        });
        return result;
    }

    @Override
    public boolean write(PlayerClass attachment, ValueOutput output) {
        output.store("player_class", PlayerClass.CODEC, attachment);
        return true;
    }
}