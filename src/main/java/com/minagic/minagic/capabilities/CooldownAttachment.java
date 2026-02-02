package com.minagic.minagic.capabilities;

import com.minagic.minagic.registries.ModAttachments;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class CooldownAttachment {

    // =========================
    // INTERNAL VARIABLES
    // =========================
    private final Map<ResourceLocation, Integer> cooldowns = new HashMap<>();

    // =========================
    // CONSTRUCTOR
    // =========================
    public CooldownAttachment() {}

    // =========================
    // INSTANCE GETTERS
    // =========================
    public int getCooldown(ResourceLocation id) {
        return cooldowns.getOrDefault(id, 0);
    }

    public Map<ResourceLocation, Integer> getAllCooldowns() {
        return Map.copyOf(cooldowns); // NEVER leak originals
    }

    public boolean isOnCooldown(ResourceLocation id) {
        return getCooldown(id) > 0;
    }

    // =========================
    // STATIC GETTERS
    // =========================
    public static int getCooldown(Entity host, ResourceLocation id) {
        return getAttachment(host).getCooldown(id);
    }

    public static Map<ResourceLocation, Integer> getAllCooldowns(Entity host) {
        return getAttachment(host).getAllCooldowns();
    }

    public static boolean isOnCooldown(Entity host, ResourceLocation id) {
        return getCooldown(host, id) > 0;
    }

    // =========================
    // INSTANCE SETTERS
    // =========================
    public void applyCooldown(ResourceLocation id, int ticks) {
        cooldowns.put(id, Math.max(0, ticks));
    }

    public void clearAll() {
        cooldowns.clear();
    }

    // =========================
    // STATIC SETTERS
    // =========================
    public static void applyCooldown(Entity host, ResourceLocation id, int ticks) {
        CooldownAttachment cooldownAttachment = getAttachment(host);
        cooldownAttachment.applyCooldown(id, ticks);
        host.setData(ModAttachments.PLAYER_SPELL_COOLDOWNS, cooldownAttachment);
    }

    public static void clearAll(Entity host) {
        CooldownAttachment cooldownAttachment = getAttachment(host);
        cooldownAttachment.clearAll();
        host.setData(ModAttachments.PLAYER_SPELL_COOLDOWNS, cooldownAttachment);
    }

    // =========================
    // INTERNAL METHODS
    // =========================
    private void replaceAll(Map<ResourceLocation, Integer> newMap) {
        cooldowns.clear();
        newMap.forEach((k, v) -> cooldowns.put(k, Math.max(0, v)));
    }

    public void tick() {
        cooldowns.replaceAll((id, cd) -> cd - 1);
        cooldowns.entrySet().removeIf(e -> e.getValue() <= 0);
    }

    public static void tick(Entity host) {
        CooldownAttachment cd = getAttachment(host);
        cd.tick();
        host.setData(ModAttachments.PLAYER_SPELL_COOLDOWNS, cd);
    }

    // =========================
    // INTERNAL HELPERS
    // =========================
    private static CooldownAttachment getAttachment(Entity entity) {
        return entity.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS);
    }

    // =========================
    // DANGER ZONE: DO NOT EDIT
    // =========================

    // CODEC
    public static final Codec<CooldownAttachment> CODEC =
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
                    .xmap(map -> {
                        var inst = new CooldownAttachment();
                        inst.cooldowns.putAll(map);
                        return inst;
                    }, d -> d.cooldowns);

    // SERIALIZER
    public static class Serializer implements IAttachmentSerializer<CooldownAttachment> {
        @Override
        public @NotNull CooldownAttachment read(@NotNull IAttachmentHolder holder, ValueInput input) {
            CooldownAttachment data = new CooldownAttachment();
            // For each key stored, read via ValueInput
            input.read("cooldowns", Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT))
                    .ifPresent(data::replaceAll);
            return data;
        }

        @Override
        public boolean write(CooldownAttachment attachment, ValueOutput output) {
            output.store("cooldowns", Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT), attachment.getAllCooldowns());
            return true;
        }
    }

}
