package com.minagic.minagic.spellCasting.spellslots;

import com.minagic.logging.ModLogger;
import com.minagic.minagic.api.spells.ISimulacrumSpell;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.SpellUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SimulacrumSpellSlot {
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
    private static final Logger LOGGER = ModLogger.SIMULACRUM;
    // COLD STATE
    private final UUID casterUUID;
    private final UUID targetUUID;
    private final UUID hostUUID;
    private final int threshold;
    private final int originalMaxLifetime;
    private final ISimulacrumSpell spell;
    // NUMERIC STATE
    private int lifetime = 0;
    private int maxLifetime; // -1 = no limit
    // HOT STATE
    private SpellCastContext context;
    private LivingEntity resolvedHostEntity; // runtime only

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
    public void resolveContext(MinecraftServer server) {
        LOGGER.debug("Attempting to resolve SimulacrumSpellSlot context...");

        resolvedHostEntity = SpellUtils.resolveLivingEntityAcrossDimensions(hostUUID, server);
        if (resolvedHostEntity == null) {
            LOGGER.warn("Could not resolve host entity for ID: {}", hostUUID);
            return;
        }
        if (context != null) return;

        LivingEntity resolvedCaster = SpellUtils.resolveLivingEntityAcrossDimensions(casterUUID, server);
        LivingEntity resolvedTarget = SpellUtils.resolveLivingEntityAcrossDimensions(targetUUID, server);

        if (resolvedCaster == null) {
            LOGGER.warn("Could not resolve caster entity for ID: {}", casterUUID);
            return;
        }

        context = (resolvedTarget != null)
                ? new SpellCastContext(resolvedCaster, resolvedTarget)
                : new SpellCastContext(resolvedCaster);

        LOGGER.debug("Context resolved: Host={}, Caster={}, Target={}", resolvedHostEntity, resolvedCaster, resolvedTarget);
    }

    public @Nullable SpellCastContext getContext() {
        return this.context;
    }

    public void tick() {
        if (context == null) {
            LOGGER.warn("SimulacrumSpellSlot tick called without resolved context.");
            return;
        }
        if (resolvedHostEntity == null) {
            LOGGER.warn("SimulacrumSpellSlot tick called without resolved host entity.");
            return;
        }

        lifetime++;
        LOGGER.debug("SimulacrumSpellSlot tick start | spell: {} | lifetime: {}/{} max: {}", this.getSpell().getString(), lifetime, threshold, maxLifetime);
        LOGGER.debug("SimulacrumSpellSlot host entity {} with id {}", resolvedHostEntity, hostUUID);
        if (maxLifetime == 0) {

            SimulacraAttachment.removeSimulacrum(resolvedHostEntity, ModSpells.getId(getSpell()));
            return;
        }

        getSpell().perform(SpellEventPhase.TICK, context, getSpellData());

        if (lifetime == threshold) {
            lifetime = 0;
            getSpell().perform(SpellEventPhase.CAST, context, getSpellData());
        }

        maxLifetime--;
        LOGGER.debug("SimulacrumSpellSlot tick complete | lifetime: {}/{} max: {}", lifetime, threshold, maxLifetime);
    }

    public SimulacrumData getSpellData() {
        return new SimulacrumData(
                ModSpells.getId(getSpell()),
                maxLifetime,
                originalMaxLifetime,
                lifetime,
                threshold,
                resolvedHostEntity
        );
    }

    public void exitSpellSlot() {
        if (context == null) return;
        getSpell().perform(SpellEventPhase.EXIT_SIMULACRUM, context, getSpellData());
    }

    public Spell getSpell() {
        return (Spell) spell;
    }
}
