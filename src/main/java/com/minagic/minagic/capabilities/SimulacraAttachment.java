package com.minagic.minagic.capabilities;

import com.minagic.minagic.api.spells.ISimulacrumSpell;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.*;

/*
 * Holds all simulacrum spell data for a player.
 * - Any number of backgroundSimulacra keyed by spell ID
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimulacraAttachment {

    // --- Fields ---
    private ResourceLocation activeChannelingSpellID = null;
    private Map<ResourceLocation, SimulacrumSpellSlot> backgroundSimulacra = new HashMap<>();
    private Map<ResourceLocation, Float> simulacraReadiness = new HashMap<>();

    // --- Getters ---

    public boolean hasActiveChanneling() {
        return activeChannelingSpellID != null && backgroundSimulacra.containsKey(activeChannelingSpellID);
    }

    public @Nullable ResourceLocation getActiveChannelingID() {
        return activeChannelingSpellID;
    }

    public boolean hasSpell(ResourceLocation id) {
        return backgroundSimulacra.containsKey(id);
    }


    // --- Setters ---

    public static void setChanneling(Entity host, SpellCastContext context, Spell spell, int threshold, int maxLifetime) {
        if (host == null) return;
        SimulacraAttachment.clearChanneling(host);
        SimulacraAttachment.addSimulacrum(host, context, spell, threshold, maxLifetime);
        SimulacraAttachment.setChannelingID(host, spell);
    }

    private static void setChannelingID(Entity host, Spell spell) {
        SimulacraAttachment attachment = host.getData(ModAttachments.PLAYER_SIMULACRA);
        attachment.activeChannelingSpellID = ModSpells.getId(spell);
        host.setData(ModAttachments.PLAYER_SIMULACRA, attachment);
    }

    public static void addSimulacrum(Entity host, SpellCastContext context, Spell spell, int threshold, int maxLifetime) {
        if (context.target == null) return;

        System.out.println("[SimulacraAttachment] Adding simulacrum spell: " + spell.getString() + " to host: " + (host) + " for target: " + (context.target));

        SimulacraAttachment attachment = host.getData(ModAttachments.PLAYER_SIMULACRA);

        if (!(spell instanceof ISimulacrumSpell simulacrumSpell)) {
            throw new IllegalArgumentException("'spell' parameter must implement ISimulacrumSpell.");
        }
        attachment.backgroundSimulacra.put(
                ModSpells.getId(spell),
                new SimulacrumSpellSlot(context, host.getUUID(), threshold, maxLifetime, maxLifetime, simulacrumSpell)
        );
        attachment.simulacraReadiness.put(ModSpells.getId(spell), 0f);
        host.setData(ModAttachments.PLAYER_SIMULACRA, attachment);
    }

    public static void removeSimulacrum(Entity host, ResourceLocation id) {
        // cast onExit
        SimulacraAttachment attachment = host.getData(ModAttachments.PLAYER_SIMULACRA);
        Map<ResourceLocation, SimulacrumSpellSlot> backgroundSimulacra = attachment.backgroundSimulacra;
        Map<ResourceLocation, Float> simulacraReadiness = attachment.simulacraReadiness;


        SimulacrumSpellSlot slot = backgroundSimulacra.get(id);
        if (slot != null) {
            slot.exitSpellSlot();
        }

        backgroundSimulacra.remove(id);
        simulacraReadiness.remove(id);


        attachment.backgroundSimulacra = backgroundSimulacra;
        attachment.simulacraReadiness = simulacraReadiness;

        host.setData(ModAttachments.PLAYER_SIMULACRA, attachment);
    }

    public static void clearSimulacra(Entity host) {
        SimulacraAttachment.clearChanneling(host);
        // cast onExit for all background simulacra
        SimulacraAttachment attachment = host.getData(ModAttachments.PLAYER_SIMULACRA);
        Map<ResourceLocation, SimulacrumSpellSlot> backgroundSimulacra = attachment.backgroundSimulacra;
        Map<ResourceLocation, Float> simulacraReadiness = attachment.simulacraReadiness;

        for (Map.Entry<ResourceLocation, SimulacrumSpellSlot> entry : backgroundSimulacra.entrySet()) {
            entry.getValue().exitSpellSlot();
        }

        backgroundSimulacra.clear();
        simulacraReadiness.clear();
        attachment.backgroundSimulacra = (backgroundSimulacra);
        attachment.simulacraReadiness = (simulacraReadiness);
        
        host.setData(ModAttachments.PLAYER_SIMULACRA, attachment);
    }

    public static void clearChanneling(Entity host) {
        SimulacraAttachment attachment = host.getData(ModAttachments.PLAYER_SIMULACRA);
        ResourceLocation id = attachment.activeChannelingSpellID;
        SimulacraAttachment.removeSimulacrum(host, id);
        attachment.activeChannelingSpellID = null;
        host.setData(ModAttachments.PLAYER_SIMULACRA, attachment);
    }

    // --- Logic ---

    public void resolveAllContexts(Level level) {
        for (SimulacrumSpellSlot slot : backgroundSimulacra.values()) {
            slot.resolveContext(level);
        }
    }

    public void tick() {
        // Tick background simulacra
        for (Map.Entry<ResourceLocation, SimulacrumSpellSlot> entry : backgroundSimulacra.entrySet()) {
            SimulacrumSpellSlot slot = entry.getValue();
            slot.tick();
            float readiness = slot.getSpellData().progress();
            simulacraReadiness.put(entry.getKey(), readiness);
        }
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
        SimulacrumSpellSlot active = att.backgroundSimulacra.get(att.activeChannelingSpellID);
        if (active != null) {
            float progress = att.simulacraReadiness.get(att.activeChannelingSpellID);
            String spellName = active.getSpell().getString();

            int filled = (int) (barWidth * progress);
            int y = yBottom;

            // Background bar
            gui.fill(xRight - barWidth, y, xRight, y + barHeight, 0x80000000);
            // Progress bar
            gui.fill(xRight - barWidth, y, xRight - barWidth + filled, y + barHeight, active.getSpellData().color(progress));

            // Label
            gui.drawString(font, "[Channeling]", xRight - barWidth, y - 10, 0xFFCCCCCC, false);
            gui.drawString(font, spellName, xRight - barWidth, y + barHeight + 2, 0xFFFFFFFF, false);

            yBottom -= (spacing + 10); // move up for next section
        }

        // --- Simulacra List ---


        if (!att.backgroundSimulacra.isEmpty() && !(att.backgroundSimulacra.size() == 1 && att.backgroundSimulacra.containsKey(att.activeChannelingSpellID))) {
            yBottom -= (spacing);

            for (Map.Entry<ResourceLocation, SimulacrumSpellSlot> entry : att.backgroundSimulacra.entrySet()) {
                ResourceLocation id = entry.getKey();
                if (id.equals(att.activeChannelingSpellID)) continue; // skip active channeling

                SimulacrumSpellSlot slot = entry.getValue();
                float readiness = att.simulacraReadiness.getOrDefault(id, 0f);

                String spellName = slot.getSpell().getString();

                int filled = (int) (barWidth * readiness);
                int y = yBottom;

                // Bar background and fill
                gui.fill(xRight - barWidth, y, xRight, y + barHeight, 0x80222222);
                gui.fill(xRight - barWidth, y, xRight - barWidth + filled, y + barHeight, slot.getSpellData().color(readiness));

                gui.drawString(font, spellName, xRight - barWidth, y + barHeight + 2, 0xFFFFFFFF, false);
                yBottom -= spacing;

                if (yBottom < 40) break; // stop if we run out of screen space
            }

            gui.drawString(font, "[Simulacra]", xRight - barWidth, yBottom, 0xFFCCCCCC, false);
        }
    }

    // WARN: DO NOT EDIT
    // --- End of Logic ---
    // --- Serializer ---
    public static class Serializer implements IAttachmentSerializer<SimulacraAttachment> {
        private static final String KEY_ACTIVE = "active_spell_id";
        private static final String KEY_SIMULACRA = "simulacra";
        private static final String KEY_SIMULACRA_READINESS = "simulacra_readiness";

        // --- Core read ---
        @Override
        public @NotNull SimulacraAttachment read(@NotNull IAttachmentHolder holder, ValueInput input) {
            SimulacraAttachment att = new SimulacraAttachment();
            input.read(KEY_ACTIVE, ResourceLocation.CODEC).ifPresent(
                    id -> att.activeChannelingSpellID = id
            );

            // Background simulacra map
            input.read(KEY_SIMULACRA, Codec.unboundedMap(ResourceLocation.CODEC, SimulacrumSpellSlot.CODEC))
                    .ifPresent(map -> att.backgroundSimulacra.putAll(map));

            input.read(KEY_SIMULACRA_READINESS,
                            Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT))
                    .ifPresent(map -> att.simulacraReadiness.putAll(map));

            return att;
        }

        // --- Core write ---
        @Override
        public boolean write(SimulacraAttachment attachment, @NotNull ValueOutput output) {
            if (attachment.activeChannelingSpellID != null) {
                output.store(KEY_ACTIVE, ResourceLocation.CODEC, attachment.activeChannelingSpellID);
            }

            if (!attachment.backgroundSimulacra.isEmpty()) {
                output.store(KEY_SIMULACRA,
                        Codec.unboundedMap(ResourceLocation.CODEC, SimulacrumSpellSlot.CODEC),
                        attachment.backgroundSimulacra);
            }

            if (!attachment.simulacraReadiness.isEmpty()) {
                output.store(KEY_SIMULACRA_READINESS,
                        Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT),
                        attachment.simulacraReadiness);
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
                            .forGetter(att -> att.backgroundSimulacra),
                    Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT)
                            .optionalFieldOf("simulacra_readiness", Map.of())
                            .forGetter(att -> att.simulacraReadiness)
            ).apply(instance, ( activeSpellID, simulacraMap, readinessMap) ->
            {
                SimulacraAttachment att = new SimulacraAttachment();
                activeSpellID.ifPresent(resourceLocation -> att.activeChannelingSpellID = resourceLocation);
                att.backgroundSimulacra.putAll(simulacraMap);
                att.simulacraReadiness.putAll(readinessMap);
                return att;
            }));
}