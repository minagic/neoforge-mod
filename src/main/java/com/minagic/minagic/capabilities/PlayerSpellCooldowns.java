package com.minagic.minagic.capabilities;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;


import java.util.HashMap;

public final class PlayerSpellCooldowns {
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

    public boolean isReady(ResourceLocation id) {
        return getCooldown(id) <= 0;
    }

    // NBT <-> object: simple and explicit
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        for (var e : cooldowns.entrySet()) {
            tag.putInt(e.getKey().toString(), e.getValue());
        }
        return tag;
    }

    public static PlayerSpellCooldowns fromTag(CompoundTag tag) {
        PlayerSpellCooldowns data = new PlayerSpellCooldowns();
        for (String k : tag.keySet()) {
            data.cooldowns.put(ResourceLocation.fromNamespaceAndPath("minagic", k), tag.getInt(k).orElse(0));
        }
        return data;
    }

    // optional helper if you want to replace whole map during sync
    public void replaceAll(Map<ResourceLocation, Integer> newMap) {
        cooldowns.clear();
        cooldowns.putAll(newMap);
    }

    public Map<ResourceLocation, Integer> view() { return cooldowns; } // read-only use only


    // === Serialization ===
    public static final Codec<PlayerSpellCooldowns> CODEC =
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
                    .xmap(map -> {
                        var inst = new PlayerSpellCooldowns();
                        inst.cooldowns.putAll(map);
                        return inst;
                    }, d -> d.cooldowns);

}