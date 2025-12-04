package com.minagic.minagic.spellCasting.spellslots;

import com.minagic.minagic.api.spells.ISimulacrumSpell;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumSpellData;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class SimulacrumSpellSlot {
    // COLD STATE
    private final UUID casterUUID;
    private final UUID targetUUID;
    private final UUID hostUUID ; // NEW — the actual entity hosting the simulacrum

    // NUMERIC STATE
    private int lifetime = 0;
    private final int threshold;
    private int maxLifetime; // -1 = no limit
    private final int originalMaxLifetime;

    // HOT STATE
    private SpellCastContext context;
    private LivingEntity resolvedHostEntity; // runtime only
    private final ISimulacrumSpell spell;

    // CONSTRUCTOR for deserialization
    public SimulacrumSpellSlot(
            UUID hostUUID,
            UUID targetUUID,
            UUID casterUUID,
            int threshold,
            int maxLifetime,
            int originalMaxLifetime,
            ISimulacrumSpell spell
    ) {
        if (!(spell instanceof Spell)) {
            throw new IllegalArgumentException("'spell' parameter must extend Spell class.");
        }
        this.spell = spell;
        this.hostUUID = hostUUID;
        this.targetUUID = targetUUID;
        this.casterUUID = casterUUID;
        this.threshold = threshold;
        this.maxLifetime = maxLifetime;
        this.originalMaxLifetime = originalMaxLifetime;
    }

    // CONSTRUCTOR from SpellCastContext
    public SimulacrumSpellSlot(
            SpellCastContext context,
            UUID hostUUID,
            int threshold,
            int maxLifetime,
            int originalMaxLifetime,
            ISimulacrumSpell spell
    ) {
        if (!(spell instanceof Spell)) {
            throw new IllegalArgumentException("'spell' parameter must extend Spell class.");
        }

        this.spell = spell;
        this.context = context;
        this.casterUUID = context.caster.getUUID();
        this.targetUUID = context.target != null ? context.target.getUUID() : context.caster.getUUID();
        this.hostUUID = hostUUID;
        this.threshold = threshold;
        this.maxLifetime = maxLifetime;
        this.originalMaxLifetime = originalMaxLifetime;
    }

    // CONTEXT RESOLUTION
    public void resolveContext(Level level) {
        System.out.println("Attempting to resolve SimulacrumSpellSlot context...");
        if (level.isClientSide()) return;
        Entity host = level.getEntity(hostUUID);

        if (host instanceof LivingEntity livingHost) {
            resolvedHostEntity = livingHost;
        }
        else{
            System.out.println("Warning: SimulacrumSpellSlot could not resolve host entity for ID: " + hostUUID);
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
        context.simulacrtumLifetime = getSpellData();
        System.out.println("[SimulacrumSpellSlot] TICK START | Lifetime: " + lifetime + "/" + threshold + ", Max: " + maxLifetime);
        System.out.println("[SimulacrumSpellSlot] Resolved Host Entity: " + resolvedHostEntity + " | Host ID: " + hostUUID);
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

    public SimulacrumSpellData getSpellData() {
        return new SimulacrumSpellData(
                ModSpells.getId(getSpell()),
                maxLifetime,
                originalMaxLifetime,
                lifetime,
                threshold

        );
    }

    public void exitSpellSlot() {
        if (context == null) return;
        context.simulacrtumLifetime = getSpellData();
        getSpell().perform(SpellEventPhase.EXIT_SIMULACRUM, context);
    }

    public Spell getSpell() {
        return (Spell) spell;
    }

    // CODEC
    public static final Codec<UUID> UUID_CODEC =
            Codec.STRING.xmap(UUID::fromString, UUID::toString);


    public static final Codec<SimulacrumSpellSlot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUID_CODEC.fieldOf("host_id").forGetter(slot -> slot.hostUUID),
            UUID_CODEC.fieldOf("caster_uuid").forGetter(slot -> slot.casterUUID),
            UUID_CODEC.fieldOf("target_uuid").forGetter(slot -> slot.targetUUID),
            Codec.INT.fieldOf("threshold").forGetter(slot -> slot.threshold),
            Codec.INT.fieldOf("max_lifetime").forGetter(slot -> slot.maxLifetime),
            Codec.INT.optionalFieldOf("lifetime", 0).forGetter(slot -> slot.lifetime),
            Codec.INT.fieldOf("original_max_lifetime").forGetter(slot -> slot.originalMaxLifetime),
            ModSpells.SPELL_CODEC.fieldOf("spell").forGetter(SimulacrumSpellSlot::getSpell)
    ).apply(instance, (hostId, casterUUID, targetUUID, threshold, maxLifetime, lifetime, originalMaxLifetime, spell) -> {
        if (!(spell instanceof ISimulacrumSpell simulacrumSpell)) {
            throw new IllegalArgumentException("Spell is not an ISimulacrumSpell: " + spell);
        }
        SimulacrumSpellSlot slot = new SimulacrumSpellSlot(hostId, targetUUID, casterUUID, threshold, maxLifetime, originalMaxLifetime, simulacrumSpell);
        slot.lifetime = lifetime;
        return slot;
    }));
}