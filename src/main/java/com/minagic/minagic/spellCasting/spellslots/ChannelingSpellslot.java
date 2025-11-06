package com.minagic.minagic.spellCasting.spellslots;

import com.minagic.minagic.abstractionLayer.spells.Spell;
import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;
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
    public void tick(LivingEntity player, Level level) {

        if (!ItemStack.isSameItem(getStack(), player.getMainHandItem()) &&
                !ItemStack.isSameItem(getStack(), player.getOffhandItem())) {
            PlayerSimulacraAttachment.clearChanneling(player, level);
            return;
        }

        lifetime ++;
        SpellCastContext ctx = new SpellCastContext(player, level, stack);
        ctx.simulacrtumLifetime = lifetime;
        if (maxLifetime == 0) {
            PlayerSimulacraAttachment.clearChanneling(player, level);
            return;
        }

        this.getSpell().onTick(ctx);

        if (lifetime == threshold) {
            lifetime = 0;
            getSpell().onCast(ctx);
        }

        maxLifetime --;
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
