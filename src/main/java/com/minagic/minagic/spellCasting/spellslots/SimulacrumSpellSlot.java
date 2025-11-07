package com.minagic.minagic.spellCasting.spellslots;

import com.minagic.minagic.abstractionLayer.spells.AutonomousSpell;
import com.minagic.minagic.abstractionLayer.spells.ChanneledAutonomousSpell;
import com.minagic.minagic.abstractionLayer.spells.Spell;
import com.minagic.minagic.capabilities.PlayerSimulacraAttachment;
import com.minagic.minagic.capabilities.PlayerSpellCooldowns;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimulacrumSpellSlot extends SpellSlot {
    // COLD STATE
    public UUID casterUUID;
    public UUID targetUUID;
    protected ItemStack stack;

    // NUMERIC STATE
    protected int lifetime = 0;
    protected int threshold;
    protected int maxLifetime; // -1 means no limit
    public int originalMaxLifetime; // for reference

    // HOT STATE
    public SpellCastContext context;

    public SimulacrumSpellSlot(
            ItemStack stack,
            UUID targetUUID,
            UUID casterUUID,
            int threshold,
            int maxLifetime,
            int originalMaxLifetime,
            Spell spell
    ) {
        super(spell);
        this.stack = stack;
        this.threshold = threshold;
        this.maxLifetime = maxLifetime;
        this.targetUUID = targetUUID;
        this.casterUUID = casterUUID;
        this.originalMaxLifetime = originalMaxLifetime;
    }

    public SimulacrumSpellSlot(
            SpellCastContext context,
            int threshold,
            int maxLifetime,
            int originalMaxLifetime,
            Spell spell
    ) {
        super(spell);
        this.stack = context.stack;
        this.casterUUID = context.caster.getUUID();
        if (context.target != null) {
            this.targetUUID = context.target.getUUID();
        } else {
            this.targetUUID = context.caster.getUUID();
        }

        this.threshold = threshold;
        this.maxLifetime = maxLifetime;
        this.context = context;
        this.originalMaxLifetime = originalMaxLifetime;
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

    // Lifecycle methods

    public void resolveContext(Level level) {
        if (context != null) {return;} // optimization: context already resolved
        if (level.isClientSide()) {return;} // context resolution only on server side
        Entity caster = level.getEntity(casterUUID);
        Entity target = level.getEntity(targetUUID);

        if (caster instanceof LivingEntity livingCaster) {
            if (target instanceof LivingEntity livingTarget) {
                context = new SpellCastContext(livingCaster, stack, livingTarget);
            } else {
                context = new SpellCastContext(livingCaster, stack);
            }
        }
    }

    public void tick() {
        if (context == null) return;
        LivingEntity target = context.target;

        System.out.println("[SimulacrumSpellSlot] Ticking simulacrum spell slot for spell: " + getSpell().getString());
        lifetime ++;
        context.simulacrtumLifetime = SimulacrumSpellData.fromSlot(this);
        if (maxLifetime == 0) {
            PlayerSimulacraAttachment.removeSimulacrum(target, ModSpells.getId(getSpell()));
            return;
        }

        this.getSpell().onTick(context);

        if (lifetime == threshold) {
            lifetime = 0;
            getSpell().onCast(context);
        }

        maxLifetime --;

        System.out.println("[SimulacrumSpellSlot] SPELL TICK COMPLETED. Lifetime: " + lifetime + " / " + threshold + ", MaxLifetime: " + maxLifetime) ;
    }

    public void exitSpellSlot() {
//        System.out.println("exitSpellSlot called for spell: " + getSpell().getString());
//        System.out.println("Dump: casterUUID=" + casterUUID + ", targetUUID=" + targetUUID + ", lifetime=" + lifetime + ", maxLifetime=" + maxLifetime);
//        System.out.println("Stack: " + stack);
//        System.out.println("context: " + context);
//        System.out.println("lifetime: " + lifetime);
        if (context == null) return;
        context.simulacrtumLifetime = SimulacrumSpellData.fromSlot(this);
        getSpell().onExitSimulacrum(context);
    }

    // CODEC

    public static final Codec<ItemStack> SAFE_ITEMSTACK =
            ItemStack.CODEC.optionalFieldOf("stack", ItemStack.EMPTY).codec();

    public static final Codec<UUID> UUID_CODEC =
            Codec.STRING.xmap(UUID::fromString, UUID::toString);

    public static final Codec<SimulacrumSpellSlot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SAFE_ITEMSTACK.fieldOf("stack").forGetter(SimulacrumSpellSlot::getStack),
            UUID_CODEC.fieldOf("caster_uuid").forGetter(slot -> slot.casterUUID),
            UUID_CODEC.fieldOf("target_uuid").forGetter(slot -> slot.targetUUID),
            Codec.INT.fieldOf("threshold").forGetter(SimulacrumSpellSlot::getThreshold),
            Codec.INT.fieldOf("max_lifetime").forGetter(SimulacrumSpellSlot::getMaxLifetime),
            Codec.INT.optionalFieldOf("lifetime", 0).forGetter(SimulacrumSpellSlot::getLifetime),
            Codec.INT.fieldOf("original_max_lifetime").forGetter(slot -> slot.originalMaxLifetime),
            ModSpells.SPELL_CODEC.fieldOf("spell").forGetter(SimulacrumSpellSlot::getSpell)

    ).apply(instance, (stack, casterUUID, targetUUID, threshold, maxLifetime, originalMaxLifetime, lifetime, spell) -> {
        SimulacrumSpellSlot slot = new SimulacrumSpellSlot(stack, casterUUID, targetUUID, threshold, maxLifetime, originalMaxLifetime, spell);
        slot.setLifetime(lifetime);
        return slot;
    }));

}
