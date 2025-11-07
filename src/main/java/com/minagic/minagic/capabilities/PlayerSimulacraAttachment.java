package com.minagic.minagic.capabilities;

import com.minagic.minagic.abstractionLayer.spells.Spell;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellCasting.spellslots.ChannelingSpellslot;
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
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerSimulacraAttachment {

    // --- Fields ---
    private ChannelingSpellslot activeChanneling = null;
    private float activeChannelingProgress = 0f;
    private Map<ResourceLocation, SimulacrumSpellSlot> backgroundSimulacra = new HashMap<>();
    private Map<ResourceLocation, Float> simulacraReadiness = new HashMap<>();

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

    public static void setActiveChanneling(SpellCastContext context, Spell spell, int threshold, int maxLifetime) {
        LivingEntity entity = context.target;
        if (entity == null) return;

        PlayerSimulacraAttachment attachment = entity.getData(ModAttachments.PLAYER_SIMULACRA);
        // Remove existing channeling if any
        if (attachment.getActiveChanneling() != null) {
            PlayerSimulacraAttachment.clearChanneling(entity);
        }
        attachment = entity.getData(ModAttachments.PLAYER_SIMULACRA); // Refresh after clearing
        attachment.activeChanneling = new ChannelingSpellslot(context, threshold, maxLifetime, maxLifetime, spell);
        entity.setData(ModAttachments.PLAYER_SIMULACRA, attachment);
    }

    public static void addSimulacrum(SpellCastContext context, Spell spell, int threshold, int maxLifetime) {
        if (context.target == null) return;

        System.out.println("[PlayerSimulacraAttachment] Adding simulacrum spell: " + spell.getString());
        System.out.println("[PlayerSimulacraAttachment] Current itemStack source: " + context.stack.getItem());

        PlayerSimulacraAttachment attachment = context.target.getData(ModAttachments.PLAYER_SIMULACRA);

        attachment.backgroundSimulacra.put(
                ModSpells.getId(spell),
                new SimulacrumSpellSlot(context, threshold, maxLifetime, maxLifetime, spell)
        );
        attachment.simulacraReadiness.put(ModSpells.getId(spell), 0f);
        context.target.setData(ModAttachments.PLAYER_SIMULACRA, attachment);
    }

    public static void removeSimulacrum(LivingEntity player, ResourceLocation id) {
        // cast onExit
        PlayerSimulacraAttachment attachment = player.getData(ModAttachments.PLAYER_SIMULACRA);
        Map<ResourceLocation, SimulacrumSpellSlot> backgroundSimulacra = attachment.getBackgroundSimulacra();
        Map<ResourceLocation, Float> simulacraReadiness = attachment.getSimulacraReadiness();


        SimulacrumSpellSlot slot = backgroundSimulacra.get(id);
        if (slot != null) {
            slot.exitSpellSlot();
        }

        backgroundSimulacra.remove(id);
        simulacraReadiness.remove(id);


        attachment.backgroundSimulacra = (backgroundSimulacra);
        attachment.simulacraReadiness = (simulacraReadiness);

        player.setData(ModAttachments.PLAYER_SIMULACRA, attachment);
    }

    public static void clearSimulacra(LivingEntity player) {
        // cast onExit for all background simulacra
        PlayerSimulacraAttachment attachment = player.getData(ModAttachments.PLAYER_SIMULACRA);
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
        PlayerSimulacraAttachment attachment = target.getData(ModAttachments.PLAYER_SIMULACRA);
        ChannelingSpellslot channeling = attachment.getActiveChanneling();
        if (channeling != null) {
            channeling.exitSpellSlot();
        }

        attachment.activeChanneling = null;
        attachment.activeChannelingProgress = 0f;
        target.setData(ModAttachments.PLAYER_SIMULACRA, attachment);
        
    }

    private void setActiveChannelingProgress(Float progress) {
        this.activeChannelingProgress = progress;
    }

    // --- Logic ---

    public void resolveAllContexts(Level level) {
        if (activeChanneling != null) {
            activeChanneling.resolveContext(level);
        }
        for (SimulacrumSpellSlot slot : backgroundSimulacra.values()) {
            slot.resolveContext(level);
        }
    }

    public void tick() {
        // Tick channeling first
        if (activeChanneling != null) {
            activeChanneling.tick();
            this.activeChannelingProgress = SimulacrumSpellData.fromSlot(activeChanneling).progress();
        }

        //System.out.println("[PlayerSimulacraAttachment] Tick of active channeling complete. Active channeling progress: " + this.activeChannelingProgress);

        // Tick background simulacra

        for (Map.Entry<ResourceLocation, SimulacrumSpellSlot> entry : backgroundSimulacra.entrySet()) {
            SimulacrumSpellSlot slot = entry.getValue();
            slot.tick();
            float readiness = SimulacrumSpellData.fromSlot(slot).progress();
            simulacraReadiness.put(entry.getKey(), readiness);
        }
    }

    private void onChannelExpire(LivingEntity player, ResourceLocation spellId) {
        if (player.level().isClientSide()) {return;}
        clearChanneling(player);
    }

    private void onBackgroundExpire(LivingEntity player, ResourceLocation spellId) {
        if (player.level().isClientSide()) {return;}
        System.out.println("[PlayerSimulacraAttachment] Background simulacrum expired: " + spellId);
        removeSimulacrum(player, spellId);
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
            gui.fill(xRight - barWidth, y, xRight - barWidth + filled, y + barHeight, SimulacrumSpellData.fromSlot(active).color(activeChannelingProgress));

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
                gui.fill(xRight - barWidth, y, xRight - barWidth + filled, y + barHeight, SimulacrumSpellData.fromSlot(slot).color(readiness));

                gui.drawString(font, spellName, xRight - barWidth, y + barHeight + 2, 0xFFFFFFFF, false);
                yBottom -= spacing;

                if (yBottom < 40) break; // stop if we run out of screen space
            }

            gui.drawString(font, "[Simulacra]", xRight - barWidth, yBottom, 0xFFCCCCCC, false);
        }
    }

    public void dump(String prefix, LivingEntity owner) {
        System.out.println(prefix + "==== PlayerSimulacraAttachment ====");

        System.out.println(prefix + "Owner: " + safe(owner));
        System.out.println(prefix + "Owner UUID: " + safe(owner != null ? owner.getUUID() : null));

        System.out.println(prefix + "Active Channeling: " + safe(activeChanneling));
        System.out.println(prefix + "Active Channeling Progress: " + activeChannelingProgress);

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

        System.out.println(prefix + "==== END PlayerSimulacraAttachment ====\n");
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

        System.out.println(prefix + "  stack: " + safe(slot.getStack()));

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

        System.out.println(prefix + "  stack: " + safe(ctx.stack));
        System.out.println(prefix + "  simulacrumLifetime: " + ctx.simulacrtumLifetime);

        // Level safety
        System.out.println(prefix + "  level(): "
                + safe(ctx.caster != null ? ctx.caster.level() : null));

        // Validity warnings
        if (ctx.caster == null && ctx.target != null)
            System.out.println(prefix + "  !! WARNING: caster null but target present");

        if (ctx.target == null && ctx.caster != null)
            System.out.println(prefix + "  !! WARNING: target null but caster present");

        if (ctx.stack == null)
            System.out.println(prefix + "  !! WARNING: stack is NULL");
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
                            slot.casterUUID,
                            slot.targetUUID,
                            slot.getThreshold(),
                            slot.getMaxLifetime(),
                            slot.originalMaxLifetime,
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
                    att.activeChanneling = new ChannelingSpellslot(slot.getStack(),
                            slot.casterUUID,
                            slot.targetUUID,
                            slot.getThreshold(),
                            slot.getMaxLifetime(),
                            slot.originalMaxLifetime,
                            slot.getSpell());
                    att.getActiveChanneling().setLifetime(slot.getLifetime());
                });
                att.setActiveChannelingProgress(progress);
                att.getBackgroundSimulacra().putAll(simulacraMap);
                att.getSimulacraReadiness().putAll(readinessMap);
                return att;
            }));
}