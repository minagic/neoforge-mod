package com.minagic.minagic.capabilities;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class PlayerSpellCooldowns {
    // === CODEC ===
    public static final Codec<PlayerSpellCooldowns> CODEC =
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
                    .xmap(map -> {
                        var inst = new PlayerSpellCooldowns();
                        inst.cooldowns.putAll(map);
                        return inst;
                    }, d -> d.cooldowns);
    private final Map<ResourceLocation, Integer> cooldowns = new HashMap<>();

    public void tick() {
        cooldowns.replaceAll((id, cd) -> Math.max(0, cd - 1));
    }

    public void setCooldown(ResourceLocation id, int ticks) {
        cooldowns.put(id, ticks);
    }

    public int getCooldown(ResourceLocation id) {
        return cooldowns.getOrDefault(id, 0);
    }

    // optional helper if you want to replace whole map during sync
    public void replaceAll(Map<ResourceLocation, Integer> newMap) {
        cooldowns.clear();
        cooldowns.putAll(newMap);
    }

    public Map<ResourceLocation, Integer> view() {
        return cooldowns;
    } // read-only use only

    // === SERIALIZER ===
    public static class Serializer implements IAttachmentSerializer<PlayerSpellCooldowns> {
        @Override
        public @NotNull PlayerSpellCooldowns read(@NotNull IAttachmentHolder holder, ValueInput input) {
            PlayerSpellCooldowns data = new PlayerSpellCooldowns();
            // For each key stored, read via ValueInput
            input.read("cooldowns", Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT))
                    .ifPresent(data::replaceAll);
            return data;
        }

        @Override
        public boolean write(PlayerSpellCooldowns attachment, ValueOutput output) {
            output.store("cooldowns", Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT), attachment.view());
            return true;
        }
    }

}