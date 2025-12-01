package com.minagic.minagic.spellCasting.spellslots;

import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;
import java.util.function.Function;

public class SimulacrumSpellSlot extends SpellSlot {
    // COLD STATE
    public UUID casterUUID;
    public UUID targetUUID;
    public UUID hostId; // NEW — the actual entity hosting the simulacrum

    // NUMERIC STATE
    protected int lifetime = 0;
    protected int threshold;
    protected int maxLifetime; // -1 = no limit
    public int originalMaxLifetime;

    // HOT STATE
    public SpellCastContext context;
    public LivingEntity resolvedHostEntity; // runtime only

    // CONSTRUCTOR for deserialization
    public SimulacrumSpellSlot(
            UUID hostId,
            UUID targetUUID,
            UUID casterUUID,
            int threshold,
            int maxLifetime,
            int originalMaxLifetime,
            Spell spell
    ) {
        super(spell);
        this.hostId = hostId;
        this.targetUUID = targetUUID;
        this.casterUUID = casterUUID;
        this.threshold = threshold;
        this.maxLifetime = maxLifetime;
        this.originalMaxLifetime = originalMaxLifetime;
    }

    // CONSTRUCTOR from SpellCastContext
    public SimulacrumSpellSlot(
            SpellCastContext context,
            UUID hostId,
            int threshold,
            int maxLifetime,
            int originalMaxLifetime,
            Spell spell
    ) {
        super(spell);
        this.context = context;
        this.casterUUID = context.caster.getUUID();
        this.targetUUID = context.target != null ? context.target.getUUID() : context.caster.getUUID();
        this.hostId = hostId;
        this.threshold = threshold;
        this.maxLifetime = maxLifetime;
        this.originalMaxLifetime = originalMaxLifetime;
    }

    // GETTERS
    public int getThreshold() { return threshold; }
    public int getMaxLifetime() { return maxLifetime; }
    public int getLifetime() { return lifetime; }

    public UUID getHostId() { return hostId; }

    // SETTERS
    public void setLifetime(int lifetime) { this.lifetime = lifetime; }

    // CONTEXT RESOLUTION
    public void resolveContext(Level level) {
        System.out.println("Attempting to resolve SimulacrumSpellSlot context...");
        if (level.isClientSide()) return;
        Entity host = level.getEntity(hostId);

        if (host instanceof LivingEntity livingHost) {
            resolvedHostEntity = livingHost;
        }
        else{
            System.out.println("Warning: SimulacrumSpellSlot could not resolve host entity for ID: " + hostId);
        }
        if (context != null) return;

        Entity caster = level.getEntity(casterUUID);
        Entity target = level.getEntity(targetUUID);


        if (caster instanceof LivingEntity livingCaster) {
            if (target instanceof LivingEntity livingTarget) {
                context = new SpellCastContext(livingCaster, livingTarget);
            } else {
                context = new SpellCastContext(livingCaster);
            }
        }
        System.out.println("SimulacrumSpellSlot resolved context: Caster=" + caster + ", Target=" + target + ", Host=" + host);
    }

    public void tick() {
        if (context == null) {
            System.out.println("Warning: SimulacrumSpellSlot tick called without resolved context.");
            return;
        }
        if (resolvedHostEntity == null) {
            System.out.println("Warning: SimulacrumSpellSlot tick called without resolved host entity.");
            return;
        }

        lifetime++;
        context.simulacrtumLifetime = SimulacrumSpellData.fromSlot(this);
        System.out.println("[SimulacrumSpellSlot] TICK START | Lifetime: " + lifetime + "/" + threshold + ", Max: " + maxLifetime);
        System.out.println("[SimulacrumSpellSlot] Resolved Host Entity: " + resolvedHostEntity + " | Host ID: " + hostId);
        if (maxLifetime == 0) {
            SimulacraAttachment.removeSimulacrum(resolvedHostEntity, ModSpells.getId(getSpell()));
            return;
        }

        getSpell().perform(SpellEventPhase.TICK, context);

        if (lifetime == threshold) {
            lifetime = 0;
            getSpell().perform(SpellEventPhase.CAST, context);
        }

        maxLifetime--;

        System.out.println("[SimulacrumSpellSlot] TICK COMPLETE | Lifetime: " + lifetime + "/" + threshold + ", Max: " + maxLifetime);
    }

    public void exitSpellSlot() {
        if (context == null) return;
        context.simulacrtumLifetime = SimulacrumSpellData.fromSlot(this);
        getSpell().perform(SpellEventPhase.EXIT_SIMULACRUM, context);
    }

    // CODEC
    public static final Codec<UUID> UUID_CODEC =
            Codec.STRING.xmap(UUID::fromString, UUID::toString);

    public static final Codec<SimulacrumSpellSlot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUID_CODEC.fieldOf("host_id").forGetter(SimulacrumSpellSlot::getHostId),
            UUID_CODEC.fieldOf("caster_uuid").forGetter(slot -> slot.casterUUID),
            UUID_CODEC.fieldOf("target_uuid").forGetter(slot -> slot.targetUUID),
            Codec.INT.fieldOf("threshold").forGetter(SimulacrumSpellSlot::getThreshold),
            Codec.INT.fieldOf("max_lifetime").forGetter(SimulacrumSpellSlot::getMaxLifetime),
            Codec.INT.optionalFieldOf("lifetime", 0).forGetter(SimulacrumSpellSlot::getLifetime),
            Codec.INT.fieldOf("original_max_lifetime").forGetter(slot -> slot.originalMaxLifetime),
            ModSpells.SPELL_CODEC.fieldOf("spell").forGetter(SimulacrumSpellSlot::getSpell)
    ).apply(instance, (hostId, casterUUID, targetUUID, threshold, maxLifetime, lifetime, originalMaxLifetime, spell) -> {
        SimulacrumSpellSlot slot = new SimulacrumSpellSlot(hostId, targetUUID, casterUUID, threshold, maxLifetime, originalMaxLifetime, spell);
        slot.setLifetime(lifetime);
        return slot;
    }));
}