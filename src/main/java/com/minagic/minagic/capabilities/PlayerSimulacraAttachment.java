package com.minagic.minagic.capabilities;

import com.minagic.minagic.abstractionLayer.ChanneledSpell;
import com.minagic.minagic.abstractionLayer.Spell;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.ChannelingSpellslot;
import com.minagic.minagic.spellCasting.SimulacrumSpellSlot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all simulacrum spell data for a player.
 * - One optional activeChannelling simulacrum
 * - Any number of backgroundSimulacra keyed by spell ID
 */
import net.minecraft.world.item.ItemStack;

public class PlayerSimulacraAttachment {

    // --- Fields ---
    private ChannelingSpellslot activeChanneling = null;
    private float activeChannelingProgress = 0f;
    private final Map<ResourceLocation, SimulacrumSpellSlot> backgroundSimulacra = new HashMap<>();

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
    }

    public void clearChanneling() {
        this.activeChanneling = null;
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
                this.activeChannelingProgress = (float) this.activeChanneling.getLifetime() / this.activeChanneling.getThreshold();
            }
        }

        System.out.println("[PlayerSimulacraAttachment] Tick of active channeling complete. Active channeling progress: " + this.activeChannelingProgress);

        // Tick background simulacra
        backgroundSimulacra.entrySet().removeIf(entry -> {
            SimulacrumSpellSlot slot = entry.getValue();
            slot.tick(player, level, this::onBackgroundExpire);
            return slot.getMaxLifetime() == 0; // Remove expired
        });
    }

    private void onChannelExpire(ResourceLocation spellId) {
        System.out.println("[PlayerSimulacraAttachment] Channeling spell expired: " + spellId);
        this.activeChanneling = null;
    }

    private void onBackgroundExpire(ResourceLocation spellId) {
        System.out.println("[PlayerSimulacraAttachment] Background simulacrum expired: " + spellId);
        backgroundSimulacra.remove(spellId);
    }

    // --- Serializer ---
    public static class Serializer implements IAttachmentSerializer<PlayerSimulacraAttachment> {
        private static final String KEY_ACTIVE = "active";
        private static final String KEY_SIMULACRA = "simulacra";
        private static final String KEY_ACTIVE_PROGRESS = "progress";

        // --- Core read ---
        @Override
        public PlayerSimulacraAttachment read(IAttachmentHolder holder, ValueInput input) {
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

            return att;
        }

        // --- Core write ---
        @Override
        public boolean write(PlayerSimulacraAttachment attachment, ValueOutput output) {
            if (attachment.activeChanneling != null) {
                output.store(KEY_ACTIVE, SimulacrumSpellSlot.CODEC, attachment.activeChanneling);
            }

            output.store(KEY_ACTIVE_PROGRESS, Codec.FLOAT, attachment.activeChannelingProgress);

            if (!attachment.backgroundSimulacra.isEmpty()) {
                output.store(KEY_SIMULACRA,
                        Codec.unboundedMap(ResourceLocation.CODEC, SimulacrumSpellSlot.CODEC),
                        attachment.backgroundSimulacra);
            }

            return true;
        }
    }

    // --- CODEC ---
    public static final Codec<PlayerSimulacraAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            // Channeling spell slot may be null, so make it optional
            ChannelingSpellslot.CODEC.optionalFieldOf("active_channeling").forGetter(
                    att -> java.util.Optional.ofNullable(att.getActiveChanneling())
            ),
            Codec.FLOAT.fieldOf("active_channeling_progress").forGetter(PlayerSimulacraAttachment::getActiveChannelingProgress ),
            // Background simulacra map
            Codec.unboundedMap(ResourceLocation.CODEC, SimulacrumSpellSlot.CODEC)
                    .optionalFieldOf("background_simulacra", Map.of())
                    .forGetter(PlayerSimulacraAttachment::getBackgroundSimulacra)
    ).apply(instance, (maybeActive, progress, simulacraMap) -> {
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
        return att;
    }));

    private void setActiveChannelingProgress(Float progress) {
        this.activeChannelingProgress = progress;
    }

    public PlayerSimulacraAttachment copy() {
        PlayerSimulacraAttachment clone = new PlayerSimulacraAttachment();

        // --- Deep copy of active channeling ---
        if (this.activeChanneling != null) {
            ChannelingSpellslot src = this.activeChanneling;
            ChannelingSpellslot channelingCopy = new ChannelingSpellslot(
                    src.getStack().copy(),
                    src.getThreshold(),
                    src.getMaxLifetime(),
                    src.getSpell()
            );
            channelingCopy.setLifetime(src.getLifetime());
            clone.activeChanneling = channelingCopy;
        }

        // --- Deep copy of background simulacra ---
        for (Map.Entry<ResourceLocation, SimulacrumSpellSlot> entry : this.backgroundSimulacra.entrySet()) {
            SimulacrumSpellSlot src = entry.getValue();
            SimulacrumSpellSlot slotCopy = new SimulacrumSpellSlot(
                    src.getStack().copy(),
                    src.getThreshold(),
                    src.getMaxLifetime(),
                    src.getSpell()
            );
            slotCopy.setLifetime(src.getLifetime());
            clone.backgroundSimulacra.put(entry.getKey(), slotCopy);
        }

        return clone;
    }
}