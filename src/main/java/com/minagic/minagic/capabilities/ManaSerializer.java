package com.minagic.minagic.capabilities;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.minecraft.world.level.storage.ValueOutput;


public class ManaSerializer implements IAttachmentSerializer<Mana> {
    private static final String KEY_MANA = "mana";
    private static final String KEY_MAX_MANA = "maxMana";

    @Override
    public Mana read(IAttachmentHolder holder, ValueInput input) {
        Mana mana = new Mana();
        input.read(KEY_MAX_MANA, Codec.INT).ifPresent(mana::setMaxMana);
        // Read both mana and maxMana if present
        input.read(KEY_MANA, Codec.INT).ifPresent(value -> {
            // Clamp to ensure no invalid data
            mana.restoreMana(value - mana.getMana());
        });

        return mana;
    }

    @Override
    public boolean write(Mana attachment, ValueOutput output) {
        output.store(KEY_MANA, Codec.FLOAT, attachment.getMana());
        output.store(KEY_MAX_MANA, Codec.INT, attachment.getMaxMana());
        return true;
    }
}