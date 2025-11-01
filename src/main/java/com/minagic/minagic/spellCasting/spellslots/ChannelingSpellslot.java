package com.minagic.minagic.spellCasting.spellslots;

import com.minagic.minagic.abstractionLayer.spells.Spell;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class ChannelingSpellslot extends SimulacrumSpellSlot {
    public ChannelingSpellslot(
            ItemStack stack,
            int threshold,
            int maxLifetime,
            Spell spell
    ) {
        super(stack, threshold, maxLifetime, spell);
    }

    @Override
    public void tick(LivingEntity player, Level level, Consumer<ResourceLocation> onExpireCallback) {

        super.tick(player, level, onExpireCallback);
        // if we have gone full cycle and reset lifetime to 0, expire the spell slot
        if (getLifetime() == 0) {
            // Expire the spell slot
            onExpireCallback.accept(this.getSpellId());
        }
    }

    public static final Codec<ChannelingSpellslot> CODEC =
            SimulacrumSpellSlot.CODEC.xmap(
                    slot -> new ChannelingSpellslot(
                            slot.getStack(),
                            slot.getThreshold(),
                            slot.getMaxLifetime(),
                            slot.getSpell()
                    ),
                    ch -> ch // back-conversion is identity
            );
}
