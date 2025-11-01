package com.minagic.minagic.spellCasting.spellslots;

import com.minagic.minagic.abstractionLayer.spells.AutonomousSpell;
import com.minagic.minagic.abstractionLayer.spells.ChanneledAutonomousSpell;
import com.minagic.minagic.abstractionLayer.spells.Spell;
import com.minagic.minagic.capabilities.PlayerSpellCooldowns;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class SimulacrumSpellSlot extends SpellSlot {
    private int lifetime = 0;
    private int threshold;
    private int maxLifetime; // -1 means no limit
    private ItemStack stack = ItemStack.EMPTY;

    public SimulacrumSpellSlot(
            ItemStack stack,
            int threshold,
            int maxLifetime,
            Spell spell
    ) {
        super(spell);
        this.threshold = threshold;
        this.maxLifetime = maxLifetime;
    }

    // Getters

    public int getThreshold() {
        return threshold;
    }
    public int getMaxLifetime() {
        return maxLifetime;
    }
    public int getLifetime() {
        return lifetime;
    }

    public ItemStack getStack() {
        return stack;
    }


    // Setters

    public void setLifetime(Integer lifetime) {
        this.lifetime = lifetime;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public void setMaxLifetime(Integer maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public void tick(LivingEntity player, Level level, Consumer<ResourceLocation> onExpireCallback) {
        System.out.println("[SimulacrumSpellSlot] Ticking simulacrum spell slot for spell: " + getSpell().getString());
        if (maxLifetime == 0) {
            // Expire the spell slot
            System.out.println("[SimulacrumSpellSlot] No spell lifetime found, expiring spell slot for spell: " + getSpell().getString());
            // TODO: figure out a more elegant way to handle cooldowns for autonomous spells
            if (getSpell() instanceof AutonomousSpell || getSpell() instanceof ChanneledAutonomousSpell) {
                System.out.println("[SimulacrumSpellSlot] Applying cooldown for autonomous spell: " + getSpell().getString());
                PlayerSpellCooldowns cd = player.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS);
                cd.setCooldown(ModSpells.getId(getSpell()), getSpell().getCooldownTicks());
                player.setData(ModAttachments.PLAYER_SPELL_COOLDOWNS, cd);

            }
            onExpireCallback.accept(ModSpells.getId(getSpell()));

            return;
        }

        lifetime ++;
        SpellCastContext ctx = new SpellCastContext(player, level, stack);
        ctx.simulacrtumLifetime = lifetime;
        this.getSpell().tick(ctx);

        if (lifetime == threshold) {
            lifetime = 0;
            getSpell().cast(ctx);
        }

        maxLifetime --;

        System.out.println("[SimulacrumSpellSlot] SPELL TICK COMPLETED. Lifetime: " + lifetime + " / " + threshold + ", MaxLifetime: " + maxLifetime) ;
    }

    public static final Codec<ItemStack> SAFE_ITEMSTACK =
            ItemStack.CODEC.optionalFieldOf("stack", ItemStack.EMPTY).codec();

    public static final Codec<SimulacrumSpellSlot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SAFE_ITEMSTACK.fieldOf("stack").forGetter(SimulacrumSpellSlot::getStack),
            Codec.INT.fieldOf("threshold").forGetter(SimulacrumSpellSlot::getThreshold),
            Codec.INT.fieldOf("max_lifetime").forGetter(SimulacrumSpellSlot::getMaxLifetime),
            Codec.INT.optionalFieldOf("lifetime", 0).forGetter(SimulacrumSpellSlot::getLifetime),
            ModSpells.SPELL_CODEC.fieldOf("spell").forGetter(SimulacrumSpellSlot::getSpell)
    ).apply(instance, (stack, threshold, maxLifetime, lifetime, spell) -> {
        SimulacrumSpellSlot slot = new SimulacrumSpellSlot(stack, threshold, maxLifetime, spell);
        slot.setLifetime(lifetime);
        return slot;
    }));

}
