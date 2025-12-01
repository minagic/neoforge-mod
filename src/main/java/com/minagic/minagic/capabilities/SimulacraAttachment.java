package com.minagic.minagic.capabilities;

import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellCasting.spellslots.SimulacrumSpellSlot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Holds all simulacrum spell data for a player.
 * - One optional activeChannelling simulacrum
 * - Any number of backgroundSimulacra keyed by spell ID
 */
import org.jetbrains.annotations.NotNull;

public class SimulacraAttachment {

    // --- Fields ---
    private ResourceLocation activeChannelingSpellID = null;
    private Map<ResourceLocation, SimulacrumSpellSlot> backgroundSimulacra = new HashMap<>();
    private Map<ResourceLocation, Float> simulacraReadiness = new HashMap<>();

    // --- Getters ---
    public SimulacrumSpellSlot getActiveChanneling() {
        return backgroundSimulacra.get(activeChannelingSpellID);
    }

    public Map<ResourceLocation, SimulacrumSpellSlot> getBackgroundSimulacra() {
        return backgroundSimulacra;
    }

    public Map<ResourceLocation, Float> getSimulacraReadiness() {
        return simulacraReadiness;
    }

    // --- Setters ---

    public static void setActiveChanneling(SpellCastContext context, Spell spell, int threshold, int maxLifetime) {
        LivingEntity entity = context.target;
        if (entity == null) return;
        SimulacraAttachment.clearChanneling(entity);
        SimulacraAttachment.addSimulacrum(context, spell, threshold, maxLifetime);
        SimulacraAttachment.setActiveChannelingID(context, spell);
    }

    public static void setActiveChannelingID(SpellCastContext context, Spell spell) {
        SimulacraAttachment attachment = context.target.getData(ModAttachments.PLAYER_SIMULACRA);
        attachment.activeChannelingSpellID = ModSpells.getId(spell);
        context.target.setData(ModAttachments.PLAYER_SIMULACRA, attachment);
    }

    public static void addSimulacrum(SpellCastContext context, Spell spell, int threshold, int maxLifetime) {
        if (context.target == null) return;

        System.out.println("[SimulacraAttachment] Adding simulacrum spell: " + spell.getString());

        SimulacraAttachment attachment = context.target.getData(ModAttachments.PLAYER_SIMULACRA);

        attachment.backgroundSimulacra.put(
                ModSpells.getId(spell),
                new SimulacrumSpellSlot(context, threshold, maxLifetime, maxLifetime, spell)
        );
        attachment.simulacraReadiness.put(ModSpells.getId(spell), 0f);
        context.target.setData(ModAttachments.PLAYER_SIMULACRA, attachment);
    }

    public static void removeSimulacrum(LivingEntity player, ResourceLocation id) {
        // cast onExit
        SimulacraAttachment attachment = player.getData(ModAttachments.PLAYER_SIMULACRA);
        Map<ResourceLocation, SimulacrumSpellSlot> backgroundSimulacra = attachment.getBackgroundSimulacra();
        Map<ResourceLocation, Float> simulacraReadiness = attachment.getSimulacraReadiness();


        SimulacrumSpellSlot slot = backgroundSimulacra.get(id);
        if (slot != null) {
            slot.exitSpellSlot();
        }

        backgroundSimulacra.remove(id);
        simulacraReadiness.remove(id);


        attachment.backgroundSimulacra = backgroundSimulacra;
        attachment.simulacraReadiness = simulacraReadiness;

        player.setData(ModAttachments.PLAYER_SIMULACRA, attachment);
    }

    public static void clearSimulacra(LivingEntity player) {
        // cast onExit for all background simulacra
        SimulacraAttachment attachment = player.getData(ModAttachments.PLAYER_SIMULACRA);
        Map<ResourceLocation, SimulacrumSpellSlot> backgroundSimulacra = attachment.getBackgroundSimulacra();
        Map<ResourceLocation, Float> simulacraReadiness = attachment.getSimulacraReadiness();

        for (Map.Entry<ResourceLocation, SimulacrumSpellSlot> entry : backgroundSimulacra.entrySet()) {
            entry.getValue().exitSpellSlot();
        }

        backgroundSimulacra.clear();
        simulacraReadiness.clear();
        attachment.backgroundSimulacra = (backgroundSimulacra);
        attachment.simulacraReadiness = (simulacraReadiness);
        
        player.setData(ModAttachments.PLAYER_SIMULACRA, attachment);
    }

    public static void clearChanneling(LivingEntity target) {
        SimulacraAttachment attachment = target.getData(ModAttachments.PLAYER_SIMULACRA);
        ResourceLocation id = attachment.activeChannelingSpellID;
        SimulacraAttachment.removeSimulacrum(target, id);
        attachment.activeChannelingSpellID = null;
        target.setData(ModAttachments.PLAYER_SIMULACRA, attachment);
    }
    // --- Logic ---

    public void resolveAllContexts(Level level) {
        System.out.println("[SimulacraAttachment] Resolving all contexts");
        for (SimulacrumSpellSlot slot : backgroundSimulacra.values()) {
            slot.resolveContext(level);
        }
        System.out.println("[SimulacraAttachment] Resolved all contexts: OK");
    }

    public void tick() {

        // Tick background simulacra
        System.out.println("[SimulacraAttachment] Ticking all simulacra");
        for (Map.Entry<ResourceLocation, SimulacrumSpellSlot> entry : backgroundSimulacra.entrySet()) {
            SimulacrumSpellSlot slot = entry.getValue();
            slot.tick();
            float readiness = SimulacrumSpellData.fromSlot(slot).progress();
            simulacraReadiness.put(entry.getKey(), readiness);
        }
        System.out.println("[SimulacraAttachment] Ticking complete");
    }

    // Rendering
    public void render(GuiGraphics gui) {
        SimulacraAttachment att = this; // or however you access it

        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        Font font = Minecraft.getInstance().font;
        int xRight = screenWidth - 8;
        int yBottom = screenHeight - 60; // start above hotbar area
        int barWidth = 100;
        int barHeight = 6;
        int spacing = 14;

        // --- Active Channeling ---
        SimulacrumSpellSlot active = att.getActiveChanneling();
        if (active != null) {
            float progress = att.getSimulacraReadiness().get(att.activeChannelingSpellID);
            String spellName = active.getSpell().getString();

            int filled = (int) (barWidth * progress);
            int y = yBottom;

            // Background bar
            gui.fill(xRight - barWidth, y, xRight, y + barHeight, 0x80000000);
            // Progress bar
            gui.fill(xRight - barWidth, y, xRight - barWidth + filled, y + barHeight, SimulacrumSpellData.fromSlot(active).color(progress));

            // Label
            gui.drawString(font, "[Channeling]", xRight - barWidth, y - 10, 0xFFCCCCCC, false);
            gui.drawString(font, spellName, xRight - barWidth, y + barHeight + 2, 0xFFFFFFFF, false);

            yBottom -= (spacing + 10); // move up for next section
        }

        // --- Simulacra List ---
        if (!att.getBackgroundSimulacra().isEmpty() && !(att.backgroundSimulacra.size() == 1 && att.backgroundSimulacra.containsKey(att.activeChannelingSpellID))) {
            yBottom -= (spacing);

            for (Map.Entry<ResourceLocation, SimulacrumSpellSlot> entry : att.getBackgroundSimulacra().entrySet()) {
                if (entry.getKey().equals(att.activeChannelingSpellID)) continue; // skip active channeling
                ResourceLocation id = entry.getKey();
                SimulacrumSpellSlot slot = entry.getValue();
                float readiness = att.getSimulacraReadiness().getOrDefault(id, 0f);
                String spellName = slot.getSpell().getString();

                int filled = (int) (barWidth * readiness);
                int y = yBottom;

                // Bar background and fill
                gui.fill(xRight - barWidth, y, xRight, y + barHeight, 0x80222222);
                gui.fill(xRight - barWidth, y, xRight - barWidth + filled, y + barHeight, SimulacrumSpellData.fromSlot(slot).color(readiness));

                gui.drawString(font, spellName, xRight - barWidth, y + barHeight + 2, 0xFFFFFFFF, false);
                yBottom -= spacing;

                if (yBottom < 40) break; // stop if we run out of screen space
            }

            gui.drawString(font, "[Simulacra]", xRight - barWidth, yBottom, 0xFFCCCCCC, false);
        }
    }

    public void dump(String prefix, LivingEntity owner) {
        System.out.println(prefix + "==== SimulacraAttachment ====");

        System.out.println(prefix + "Owner: " + safe(owner));
        System.out.println(prefix + "Owner UUID: " + safe(owner != null ? owner.getUUID() : null));

        System.out.println(prefix + "Active Channeling: " + safe(getActiveChanneling()));
        System.out.println(prefix + "Active Channeling Progress: " + getSimulacraReadiness().get(activeChannelingSpellID));

        System.out.println(prefix + "Background Simulacra: "
                + (backgroundSimulacra == null ? "null" : backgroundSimulacra.size()));
        if (backgroundSimulacra != null) {
            for (Map.Entry<ResourceLocation, SimulacrumSpellSlot> e : backgroundSimulacra.entrySet()) {
                System.out.println(prefix + "  Key: " + e.getKey());
                dumpSlot(e.getValue(), prefix + "    ", owner);
            }
        }

        System.out.println(prefix + "Simulacra Readiness: "
                + (simulacraReadiness == null ? "null" : simulacraReadiness.size()));
        if (simulacraReadiness != null) {
            for (var e : simulacraReadiness.entrySet()) {
                System.out.println(prefix + "  " + e.getKey() + " = " + e.getValue());
            }
        }

        System.out.println(prefix + "==== END SimulacraAttachment ====\n");
    }

    private void dumpSlot(SimulacrumSpellSlot slot, String prefix, LivingEntity owner) {
        if (slot == null) {
            System.out.println(prefix + "Slot: null");
            return;
        }

        System.out.println(prefix + "SimulacrumSpellSlot:");
        System.out.println(prefix + "  Spell: " + safe(slot.getSpell()));
        System.out.println(prefix + "  Lifetime: " + slot.getLifetime());
        System.out.println(prefix + "  Threshold: " + slot.getThreshold());
        System.out.println(prefix + "  MaxLifetime: " + slot.getMaxLifetime());

        System.out.println(prefix + "  casterUUID: " + safe(slot.casterUUID));
        System.out.println(prefix + "  targetUUID: " + safe(slot.targetUUID));

        // Check UUID resolution
        if (owner != null && owner.level() != null) {
            LivingEntity caster = tryResolve(slot.casterUUID, owner.level());
            LivingEntity target = tryResolve(slot.targetUUID, owner.level());

            System.out.println(prefix + "  Resolved caster: " + safe(caster));
            System.out.println(prefix + "  Resolved target: " + safe(target));
        }

        System.out.println(prefix + "  HOT STATE:");
        dumpContext(slot.context, prefix + "    ");
    }

    private void dumpContext(SpellCastContext ctx, String prefix) {
        if (ctx == null) {
            System.out.println(prefix + "null");
            return;
        }

        System.out.println(prefix + "SpellCastContext:");
        System.out.println(prefix + "  caster: " + safe(ctx.caster));
        System.out.println(prefix + "    caster UUID: "
                + safe(ctx.caster != null ? ctx.caster.getUUID() : null));

        System.out.println(prefix + "  target: " + safe(ctx.target));
        System.out.println(prefix + "    target UUID: "
                + safe(ctx.target != null ? ctx.target.getUUID() : null));
        System.out.println(prefix + "  simulacrumLifetime: " + ctx.simulacrtumLifetime);

        // Level safety
        System.out.println(prefix + "  level(): "
                + safe(ctx.caster != null ? ctx.caster.level() : null));

        // Validity warnings
        if (ctx.caster == null && ctx.target != null)
            System.out.println(prefix + "  !! WARNING: caster null but target present");

        if (ctx.target == null && ctx.caster != null)
            System.out.println(prefix + "  !! WARNING: target null but caster present");
    }

    private String safe(Object o) {
        try {
            return String.valueOf(o);
        } catch (Throwable t) {
            return "<ERR:" + t + ">";
        }
    }

    private LivingEntity tryResolve(UUID id, Level level) {
        if (id == null || level == null) return null;
        Entity e = ((ServerLevel) level).getEntity(id);
        return (e instanceof LivingEntity le) ? le : null;
    }

    // --- Serializer ---
    public static class Serializer implements IAttachmentSerializer<SimulacraAttachment> {
        private static final String KEY_ACTIVE = "active_spell_id";
        private static final String KEY_SIMULACRA = "simulacra";
        private static final String KEY_SIMULACRA_READINESS = "simulacra_readiness";

        // --- Core read ---
        @Override
        public @NotNull SimulacraAttachment read(@NotNull IAttachmentHolder holder, ValueInput input) {
            System.out.println("INITIATING PHASE: READ");
            SimulacraAttachment att = new SimulacraAttachment();
            input.read(KEY_ACTIVE, ResourceLocation.CODEC).ifPresent(
                    id -> att.activeChannelingSpellID = id
            );

            // Background simulacra map
            input.read(KEY_SIMULACRA, Codec.unboundedMap(ResourceLocation.CODEC, SimulacrumSpellSlot.CODEC))
                    .ifPresent(map -> att.backgroundSimulacra.putAll(map));

            input.read(KEY_SIMULACRA_READINESS,
                            Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT))
                    .ifPresent(map -> att.getSimulacraReadiness().putAll(map));

            return att;
        }

        // --- Core write ---
        @Override
        public boolean write(SimulacraAttachment attachment, @NotNull ValueOutput output) {

            System.out.println("INITIATING PHASE: WRITE");
            if (attachment.activeChannelingSpellID != null) {
                output.store(KEY_ACTIVE, ResourceLocation.CODEC, attachment.activeChannelingSpellID);
            }

            if (!attachment.backgroundSimulacra.isEmpty()) {
                output.store(KEY_SIMULACRA,
                        Codec.unboundedMap(ResourceLocation.CODEC, SimulacrumSpellSlot.CODEC),
                        attachment.backgroundSimulacra);
            }

            if (!attachment.getSimulacraReadiness().isEmpty()) {
                output.store(KEY_SIMULACRA_READINESS,
                        Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT),
                        attachment.getSimulacraReadiness());
            }

            return true;
        }
    }

    // --- CODEC ---
    public static final Codec<SimulacraAttachment> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.optionalFieldOf("active_spell_id").forGetter(
                            att -> Optional.ofNullable(att.activeChannelingSpellID)
                    ),
                    Codec.unboundedMap(ResourceLocation.CODEC, SimulacrumSpellSlot.CODEC)
                            .optionalFieldOf("background_simulacra", Map.of())
                            .forGetter(SimulacraAttachment::getBackgroundSimulacra),
                    Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT)
                            .optionalFieldOf("simulacra_readiness", Map.of())
                            .forGetter(SimulacraAttachment::getSimulacraReadiness)
            ).apply(instance, ( activeSpellID, simulacraMap, readinessMap) -> {
                SimulacraAttachment att = new SimulacraAttachment();
                activeSpellID.ifPresent(resourceLocation -> att.activeChannelingSpellID = resourceLocation);
                att.getBackgroundSimulacra().putAll(simulacraMap);
                att.getSimulacraReadiness().putAll(readinessMap);
                return att;
            }));
}