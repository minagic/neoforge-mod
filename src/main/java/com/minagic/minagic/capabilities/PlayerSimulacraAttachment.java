package com.minagic.minagic.capabilities;

import com.minagic.minagic.abstractionLayer.spells.Spell;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.spellslots.ChannelingSpellslot;
import com.minagic.minagic.spellCasting.spellslots.SimulacrumSpellSlot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Holds all simulacrum spell data for a player.
 * - One optional activeChannelling simulacrum
 * - Any number of backgroundSimulacra keyed by spell ID
 */
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerSimulacraAttachment {

    // --- Fields ---
    private ChannelingSpellslot activeChanneling = null;
    private float activeChannelingProgress = 0f;
    private final Map<ResourceLocation, SimulacrumSpellSlot> backgroundSimulacra = new HashMap<>();
    private final Map<ResourceLocation, Float> simulacraReadiness = new HashMap<>();

    // --- Getters ---
    public ChannelingSpellslot getActiveChanneling() {
        return activeChanneling;
    }

    public float getActiveChannelingProgress() {
        return activeChannelingProgress;
    }

    public Map<ResourceLocation, SimulacrumSpellSlot> getBackgroundSimulacra() {
        return backgroundSimulacra;
    }

    public Map<ResourceLocation, Float> getSimulacraReadiness() {
        return simulacraReadiness;
    }
    // --- Setters ---

    public void setActiveChanneling(Spell spell, int threshold, int maxLifetime, ItemStack stack) {
        this.activeChanneling = new ChannelingSpellslot(stack.copy(), threshold, maxLifetime, spell);
        this.activeChannelingProgress = 0f;
    }

    public void addSimulacrum(Spell spell, int threshold, int maxLifetime, ItemStack stack) {
        ResourceLocation id = ModSpells.getId(spell);
        backgroundSimulacra.put(id, new SimulacrumSpellSlot(stack.copy(), threshold, maxLifetime, spell));
    }

    public void removeSimulacrum(ResourceLocation id) {
        backgroundSimulacra.remove(id);
    }

    public void clearSimulacra() {
        backgroundSimulacra.clear();
        simulacraReadiness.clear();
    }

    public void clearChanneling() {
        this.activeChanneling = null;
    }

    private void setActiveChannelingProgress(Float progress) {
        this.activeChannelingProgress = progress;
    }

    public void setSimulacraReadiness(ResourceLocation id, float readiness) {
        simulacraReadiness.put(id, Mth.clamp(readiness, 0f, 1f));
    }

    // --- Logic ---

    public void tick(ServerPlayer player, Level level) {
        // Tick channeling first
        if (activeChanneling != null) {
            activeChanneling.tick(player, level, this::onChannelExpire);
            if (activeChanneling == null) { // in case it expired
                this.activeChannelingProgress = 0f; // Reset progress on expire
            }
            else {
                if (this.activeChanneling.getThreshold() == 0 && this.activeChanneling.getMaxLifetime() == 0) {
                    this.activeChannelingProgress = 0f;
                }
                else if (this.activeChanneling.getThreshold() == 0) {
                    this.activeChannelingProgress = (float) this.activeChanneling.getLifetime() / this.activeChanneling.getSpell().getMaxLifetime();
                }
                else{
                    this.activeChannelingProgress = (float) this.activeChanneling.getLifetime() / this.activeChanneling.getThreshold();
                }
            }
        }

        //System.out.println("[PlayerSimulacraAttachment] Tick of active channeling complete. Active channeling progress: " + this.activeChannelingProgress);

        // Tick background simulacra
        backgroundSimulacra.entrySet().removeIf(entry -> {
            SimulacrumSpellSlot slot = entry.getValue();
            slot.tick(player, level, this::onBackgroundExpire);
            return slot.getMaxLifetime() == 0; // Remove expired
        });

        for (Map.Entry<ResourceLocation, SimulacrumSpellSlot> entry : backgroundSimulacra.entrySet()) {
            SimulacrumSpellSlot slot = entry.getValue();
            float readiness = slot.getMaxLifetime() == 0
                    ? 0f
                    :
                    slot.getLifetime() < 0
                            ? 1f
                            : (float) slot.getMaxLifetime() / slot.getSpell().getMaxLifetime();
            simulacraReadiness.put(entry.getKey(), Mth.clamp(readiness, 0f, 1f));
        }
    }

    private void onChannelExpire(ResourceLocation spellId) {
        System.out.println("[PlayerSimulacraAttachment] Channeling spell expired: " + spellId);
        this.activeChanneling = null;
    }

    private void onBackgroundExpire(ResourceLocation spellId) {
        System.out.println("[PlayerSimulacraAttachment] Background simulacrum expired: " + spellId);
        backgroundSimulacra.remove(spellId);
    }

    // Rendering
    public void render(GuiGraphics gui) {
        PlayerSimulacraAttachment att = this; // or however you access it

        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        Font font = Minecraft.getInstance().font;
        int xRight = screenWidth - 8;
        int yBottom = screenHeight - 60; // start above hotbar area
        int barWidth = 100;
        int barHeight = 6;
        int spacing = 14;

        // --- Active Channeling ---
        ChannelingSpellslot active = att.getActiveChanneling();
        if (active != null) {
            float progress = att.getActiveChannelingProgress();
            String spellName = active.getSpell().getString();

            int filled = (int) (barWidth * progress);
            int y = yBottom;

            // Background bar
            gui.fill(xRight - barWidth, y, xRight, y + barHeight, 0x80000000);
            // Progress bar
            gui.fill(xRight - barWidth, y, xRight - barWidth + filled, y + barHeight, 0xFF3399FF);

            // Label
            gui.drawString(font, "[Channeling]", xRight - barWidth, y - 10, 0xFFCCCCCC, false);
            gui.drawString(font, spellName, xRight - barWidth, y + barHeight + 2, 0xFFFFFFFF, false);

            yBottom -= (spacing + 10); // move up for next section
        }

        // --- Simulacra List ---
        if (!att.getBackgroundSimulacra().isEmpty()) {
            yBottom -= (spacing);

            for (Map.Entry<ResourceLocation, SimulacrumSpellSlot> entry : att.getBackgroundSimulacra().entrySet()) {
                ResourceLocation id = entry.getKey();
                SimulacrumSpellSlot slot = entry.getValue();
                float readiness = att.getSimulacraReadiness().getOrDefault(id, 0f);
                String spellName = slot.getSpell().getString();

                int filled = (int) (barWidth * readiness);
                int y = yBottom;

                // Bar background and fill
                gui.fill(xRight - barWidth, y, xRight, y + barHeight, 0x80222222);
                gui.fill(xRight - barWidth, y, xRight - barWidth + filled, y + barHeight, 0xFFFFAA33);

                gui.drawString(font, spellName, xRight - barWidth, y + barHeight + 2, 0xFFFFFFFF, false);
                yBottom -= spacing;

                if (yBottom < 40) break; // stop if we run out of screen space
            }

            gui.drawString(font, "[Simulacra]", xRight - barWidth, yBottom, 0xFFCCCCCC, false);
        }
    }

    // --- Serializer ---
    public static class Serializer implements IAttachmentSerializer<PlayerSimulacraAttachment> {
        private static final String KEY_ACTIVE = "active";
        private static final String KEY_SIMULACRA = "simulacra";
        private static final String KEY_ACTIVE_PROGRESS = "progress";
        private static final String KEY_SIMULACRA_READINESS = "simulacra_readiness";

        // --- Core read ---
        @Override
        public @NotNull PlayerSimulacraAttachment read(@NotNull IAttachmentHolder holder, ValueInput input) {
            PlayerSimulacraAttachment att = new PlayerSimulacraAttachment();

            // Active channeling
            input.read(KEY_ACTIVE, SimulacrumSpellSlot.CODEC).ifPresent(slot -> {
                if (slot instanceof ChannelingSpellslot channeling) {
                    att.activeChanneling = channeling;
                } else {
                    att.activeChanneling = new ChannelingSpellslot(
                            slot.getStack(),
                            slot.getThreshold(),
                            slot.getMaxLifetime(),
                            slot.getSpell()
                    );
                }
            });

            // Background simulacra map
            input.read(KEY_SIMULACRA, Codec.unboundedMap(ResourceLocation.CODEC, SimulacrumSpellSlot.CODEC))
                    .ifPresent(map -> att.backgroundSimulacra.putAll(map));

            input.read(KEY_ACTIVE_PROGRESS, Codec.FLOAT).ifPresent(progress -> att.activeChannelingProgress = progress);
            input.read(KEY_SIMULACRA_READINESS,
                            Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT))
                    .ifPresent(map -> att.getSimulacraReadiness().putAll(map));

            return att;
        }

        // --- Core write ---
        @Override
        public boolean write(PlayerSimulacraAttachment attachment, @NotNull ValueOutput output) {
            if (attachment.activeChanneling != null) {
                output.store(KEY_ACTIVE, SimulacrumSpellSlot.CODEC, attachment.activeChanneling);
            }

            output.store(KEY_ACTIVE_PROGRESS, Codec.FLOAT, attachment.activeChannelingProgress);

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
    public static final Codec<PlayerSimulacraAttachment> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ChannelingSpellslot.CODEC.optionalFieldOf("active_channeling")
                            .forGetter(att -> Optional.ofNullable(att.getActiveChanneling())),
                    Codec.FLOAT.fieldOf("active_channeling_progress")
                            .forGetter(PlayerSimulacraAttachment::getActiveChannelingProgress),
                    Codec.unboundedMap(ResourceLocation.CODEC, SimulacrumSpellSlot.CODEC)
                            .optionalFieldOf("background_simulacra", Map.of())
                            .forGetter(PlayerSimulacraAttachment::getBackgroundSimulacra),
                    Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT)
                            .optionalFieldOf("simulacra_readiness", Map.of())
                            .forGetter(PlayerSimulacraAttachment::getSimulacraReadiness)
            ).apply(instance, (maybeActive, progress, simulacraMap, readinessMap) -> {
                PlayerSimulacraAttachment att = new PlayerSimulacraAttachment();
                maybeActive.ifPresent(slot -> {
                    att.setActiveChanneling(slot.getSpell(),
                            slot.getThreshold(),
                            slot.getMaxLifetime(),
                            slot.getStack());
                    att.getActiveChanneling().setLifetime(slot.getLifetime());
                });
                att.setActiveChannelingProgress(progress);
                att.getBackgroundSimulacra().putAll(simulacraMap);
                att.getSimulacraReadiness().putAll(readinessMap);
                return att;
            }));
}