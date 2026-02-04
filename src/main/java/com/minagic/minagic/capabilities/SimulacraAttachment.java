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
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SimulacraAttachment implements AutodetectionInterfaces.ILivingTickableAttachment, AutodetectionInterfaces.IRenderableAttachment {

    // =========================
    // INTERNAL VARIABLES
    // =========================
    private ResourceLocation activeChannelingSpellID = null;
    private final Map<ResourceLocation, SimulacrumSpellSlot> backgroundSimulacra = new HashMap<>();
    private final Map<ResourceLocation, Float> simulacraReadiness = new HashMap<>();

    // =========================
    // CONSTRUCTOR
    // =========================
    public SimulacraAttachment() {}

    // =========================
    // INSTANCE GETTERS
    // =========================
    public boolean hasSpell(ResourceLocation id) {
        return backgroundSimulacra.containsKey(id);
    }

    public boolean hasActiveChanneling() {
        return activeChannelingSpellID != null && backgroundSimulacra.containsKey(activeChannelingSpellID);
    }

    public @Nullable ResourceLocation getActiveChannelingID() {
        return activeChannelingSpellID;
    }

    public Map<ResourceLocation, Float> getAllProgress() {
        return Map.copyOf(simulacraReadiness);
    }

    public List<SimulacrumSpellSlot> getAllSpellSlots() {
        return List.copyOf(backgroundSimulacra.values());
    }

    // =========================
    // STATIC GETTERS
    // =========================

    public static boolean hasSpell(Entity host, ResourceLocation id) {
        return getAttachment(host).hasSpell(id);
    }

    public static boolean hasActiveChanneling(Entity host){
        return getAttachment(host).hasActiveChanneling();
    }

    public static @Nullable ResourceLocation getActiveChannelingID(Entity host) {
        return getAttachment(host).getActiveChannelingID();
    }
    public static Map<ResourceLocation, Float> getAllProgress(Entity host) {
        return getAttachment(host).getAllProgress();
    }

    public static List<SimulacrumSpellSlot> getAllSpellSlots(Entity host) {
        return getAttachment(host).getAllSpellSlots();
    }

    // =========================
    // INSTANCE SETTERS
    // =========================
    private void setActiveChannelingID(ResourceLocation id) {
        this.activeChannelingSpellID = id;
    }

    // =========================
    // STATIC SETTERS
    // =========================
    public static void setChanneling(Entity host, SpellCastContext context, Spell spell, int threshold, int maxLifetime) {
        clearChanneling(host);
        addSimulacrum(host, context, spell, threshold, maxLifetime);

        SimulacraAttachment att = getAttachment(host);
        att.setActiveChannelingID(ModSpells.getId(spell));
        host.setData(ModAttachments.PLAYER_SIMULACRA, att);
    }

    public static void addSimulacrum(Entity host, SpellCastContext context, Spell spell, int threshold, int maxLifetime) {
        if (context.target == null) return;

        if (!(spell instanceof ISimulacrumSpell simulacrumSpell)) {
            throw new IllegalArgumentException("'spell' must implement ISimulacrumSpell.");
        }

        SimulacraAttachment att = getAttachment(host);

        ResourceLocation id = ModSpells.getId(spell);

        att.backgroundSimulacra.put(
                id,
                new SimulacrumSpellSlot(context, host.getUUID(), threshold, maxLifetime, maxLifetime, simulacrumSpell)
        );

        att.simulacraReadiness.put(id, 0f);

        host.setData(ModAttachments.PLAYER_SIMULACRA, att);
    }

    public static void removeSimulacrum(Entity host, ResourceLocation id) {
        SimulacraAttachment att = getAttachment(host);

        SimulacrumSpellSlot slot = att.backgroundSimulacra.get(id);
        if (slot != null) {
            slot.exitSpellSlot();
        }

        att.backgroundSimulacra.remove(id);
        att.simulacraReadiness.remove(id);

        host.setData(ModAttachments.PLAYER_SIMULACRA, att);
    }

    public static void clearChanneling(Entity host) {
        SimulacraAttachment att = getAttachment(host);

        if (att.activeChannelingSpellID != null) {
            removeSimulacrum(host, att.activeChannelingSpellID);
            att.activeChannelingSpellID = null;
        }

        host.setData(ModAttachments.PLAYER_SIMULACRA, att);
    }

    public static void clearSimulacra(Entity host) {
        SimulacraAttachment att = getAttachment(host);

        for (SimulacrumSpellSlot slot : att.backgroundSimulacra.values()) {
            slot.exitSpellSlot();
        }

        att.backgroundSimulacra.clear();
        att.simulacraReadiness.clear();
        att.activeChannelingSpellID = null;

        host.setData(ModAttachments.PLAYER_SIMULACRA, att);
    }

    // =========================
    // INTERNAL LOGIC
    // =========================
    private void resolveAllContexts(MinecraftServer server) {
        for (SimulacrumSpellSlot slot : backgroundSimulacra.values()) {
            slot.resolveContext(server);
        }
    }

    public void tick(LivingEntity host) {
        if (host.level().isClientSide()) return;
        MinecraftServer server = host.level().getServer();
        resolveAllContexts(server);

        Map<ResourceLocation, SimulacrumSpellSlot> copy = Map.copyOf(backgroundSimulacra);

        for (var entry : copy.entrySet()) {
            SimulacrumSpellSlot slot = entry.getValue();
            slot.tick();
            simulacraReadiness.put(entry.getKey(), slot.getSpellData().progress());
        }
    }

    // =========================
    // RENDER
    // =========================
    public void render(LivingEntity host, GuiGraphics gui) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int xRight = screenWidth - 8;
        int yBottom = screenHeight - 60;
        int barWidth = 100;
        int barHeight = 6;
        int spacing = 14;

        // Active channeling
        SimulacrumSpellSlot active = backgroundSimulacra.get(activeChannelingSpellID);
        if (active != null) {
            float progress = simulacraReadiness.get(activeChannelingSpellID);
            int filled = (int) (barWidth * progress);

            gui.fill(xRight - barWidth, yBottom, xRight, yBottom + barHeight, 0x80000000);
            gui.fill(xRight - barWidth, yBottom,
                    xRight - barWidth + filled, yBottom + barHeight,
                    active.getSpellData().color(progress));

            gui.drawString(font, "[Channeling]", xRight - barWidth, yBottom - 10, 0xFFCCCCCC, false);
            gui.drawString(font, active.getSpell().getString(),
                    xRight - barWidth, yBottom + barHeight + 2, 0xFFFFFFFF, false);

            yBottom -= spacing + 10;
        }

        // Background simulacra
        for (var entry : backgroundSimulacra.entrySet()) {
            if (entry.getKey().equals(activeChannelingSpellID)) continue;

            float progress = simulacraReadiness.getOrDefault(entry.getKey(), 0f);
            int filled = (int) (barWidth * progress);

            gui.fill(xRight - barWidth, yBottom, xRight, yBottom + barHeight, 0x80222222);
            gui.fill(xRight - barWidth, yBottom,
                    xRight - barWidth + filled, yBottom + barHeight,
                    entry.getValue().getSpellData().color(progress));

            gui.drawString(font, entry.getValue().getSpell().getString(),
                    xRight - barWidth, yBottom + barHeight + 2, 0xFFFFFFFF, false);

            yBottom -= spacing;
            if (yBottom < 40) break;
        }
    }

    public boolean shouldRender(LivingEntity host){
        return !this.getAllSpellSlots().isEmpty();
    }

    // =========================
    // INTERNAL HELPERS
    // =========================
    private static SimulacraAttachment getAttachment(Entity entity) {
        return entity.getData(ModAttachments.PLAYER_SIMULACRA);
    }

    // =========================
    // DANGER ZONE: DO NOT EDIT
    // =========================

    // CODEC
    public static final Codec<SimulacraAttachment> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.optionalFieldOf("active_spell_id")
                            .forGetter(att -> Optional.ofNullable(att.activeChannelingSpellID)),
                    Codec.unboundedMap(ResourceLocation.CODEC, SimulacrumSpellSlot.CODEC)
                            .optionalFieldOf("background_simulacra", Map.of())
                            .forGetter(att -> att.backgroundSimulacra),
                    Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT)
                            .optionalFieldOf("simulacra_readiness", Map.of())
                            .forGetter(att -> att.simulacraReadiness)
            ).apply(instance, (activeSpellID, simulacraMap, readinessMap) -> {
                SimulacraAttachment att = new SimulacraAttachment();
                activeSpellID.ifPresent(id -> att.activeChannelingSpellID = id);
                att.backgroundSimulacra.putAll(simulacraMap);
                att.simulacraReadiness.putAll(readinessMap);
                return att;
            }));

    // SERIALIZER
    public static class Serializer implements IAttachmentSerializer<SimulacraAttachment> {

        private static final String KEY_ACTIVE = "active_spell_id";
        private static final String KEY_SIMULACRA = "simulacra";
        private static final String KEY_SIMULACRA_READINESS = "simulacra_readiness";

        @Override
        public @NotNull SimulacraAttachment read(@NotNull IAttachmentHolder holder, ValueInput input) {
            SimulacraAttachment att = new SimulacraAttachment();

            input.read(KEY_ACTIVE, ResourceLocation.CODEC)
                    .ifPresent(id -> att.activeChannelingSpellID = id);

            input.read(KEY_SIMULACRA,
                            Codec.unboundedMap(ResourceLocation.CODEC, SimulacrumSpellSlot.CODEC))
                    .ifPresent(att.backgroundSimulacra::putAll);

            input.read(KEY_SIMULACRA_READINESS,
                            Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT))
                    .ifPresent(att.simulacraReadiness::putAll);

            return att;
        }

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
}
