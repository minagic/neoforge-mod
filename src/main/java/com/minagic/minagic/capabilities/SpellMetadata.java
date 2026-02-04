package com.minagic.minagic.capabilities;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import com.mojang.serialization.DataResult;

public final class SpellMetadata {

    // =========================
    // INTERNAL VARIABLES
    // =========================
    private final Map<Pair<ResourceLocation, String>, String> stringMap = new HashMap<>();
    private final Map<Pair<ResourceLocation, String>, Integer> intMap = new HashMap<>();
    private final Map<Pair<ResourceLocation, String>, BlockState> blockStateMap = new HashMap<>();
    private final Map<Pair<ResourceLocation, String>, BlockPos> blockPosMap = new HashMap<>();

    // =========================
    // CONSTRUCTOR
    // =========================
    public SpellMetadata() {}

    // =========================
    // INTERNAL KEY BUILDER
    // =========================
    private static Pair<ResourceLocation, String> makeKey(Spell spell, String key) {
        return Pair.of(ModSpells.getId(spell), key);
    }

    // =========================
    // INSTANCE GETTERS
    // =========================
    public boolean has(Pair<ResourceLocation, String> key) {
        Minagic.LOGGER.debug("SpellMetadats lookup: {}", key);
        Minagic.LOGGER.debug("BlockPos map: {}", blockPosMap);
        return stringMap.containsKey(key)
                || intMap.containsKey(key)
                || blockStateMap.containsKey(key)
                || blockPosMap.containsKey(key);
    }

    public String getString(Pair<ResourceLocation, String> key) {
        return stringMap.get(key);
    }

    public Integer getInt(Pair<ResourceLocation, String> key) {
        return intMap.get(key);
    }

    public BlockState getBlockState(Pair<ResourceLocation, String> key) {
        return blockStateMap.get(key);
    }

    public BlockPos getBlockPos(Pair<ResourceLocation, String> key) {
        return blockPosMap.get(key);
    }

    // =========================
    // STATIC GETTERS
    // =========================
    public static boolean has(Entity host, Spell spell, String key) {
        return getAttachment(host).has(makeKey(spell, key));
    }

    public static String getString(Entity host, Spell spell, String key) {
        return getAttachment(host).getString(makeKey(spell, key));
    }

    public static Integer getInt(Entity host, Spell spell, String key) {
        return getAttachment(host).getInt(makeKey(spell, key));
    }

    public static BlockState getBlockState(Entity host, Spell spell, String key) {
        return getAttachment(host).getBlockState(makeKey(spell, key));
    }

    public static BlockPos getBlockPos(Entity host, Spell spell, String key) {
        return getAttachment(host).getBlockPos(makeKey(spell, key));
    }

    public static Map<Pair<ResourceLocation, String>, BlockPos> getAllBlockPos(Entity host) {
        return Map.copyOf(getAttachment(host).blockPosMap);
    }

    // =========================
    // INSTANCE SETTERS
    // =========================
    public void setString(Pair<ResourceLocation, String> key, String value) {
        stringMap.put(key, value);
    }

    public void removeString(Pair<ResourceLocation, String> key) {
        stringMap.remove(key);
    }

    public void setInt(Pair<ResourceLocation, String> key, int value) {
        intMap.put(key, value);
    }

    public void removeInt(Pair<ResourceLocation, String> key) {
        intMap.remove(key);
    }

    public void setBlockState(Pair<ResourceLocation, String> key, BlockState value) {
        blockStateMap.put(key, value);
    }

    public void removeBlockState(Pair<ResourceLocation, String> key) {
        blockStateMap.remove(key);
    }

    public void setBlockPos(Pair<ResourceLocation, String> key, BlockPos value) {
        blockPosMap.put(key, value);
    }

    public void removeBlockPos(Pair<ResourceLocation, String> key) {
        blockPosMap.remove(key);
    }

    // =========================
    // STATIC SETTERS
    // =========================
    public static void setString(Entity host, Spell spell, String key, String value) {
        SpellMetadata meta = getAttachment(host);
        meta.setString(makeKey(spell, key), value);
        host.setData(ModAttachments.SPELL_METADATA, meta);
    }

    public static void removeString(Entity host, Spell spell, String key) {
        SpellMetadata meta = getAttachment(host);
        meta.removeString(makeKey(spell, key));
        host.setData(ModAttachments.SPELL_METADATA, meta);
    }

    public static void setInt(Entity host, Spell spell, String key, int value) {
        SpellMetadata meta = getAttachment(host);
        meta.setInt(makeKey(spell, key), value);
        host.setData(ModAttachments.SPELL_METADATA, meta);
    }

    public static void removeInt(Entity host, Spell spell, String key) {
        SpellMetadata meta = getAttachment(host);
        meta.removeInt(makeKey(spell, key));
        host.setData(ModAttachments.SPELL_METADATA, meta);
    }

    public static void setBlockState(Entity host, Spell spell, String key, BlockState value) {
        SpellMetadata meta = getAttachment(host);
        meta.setBlockState(makeKey(spell, key), value);
        host.setData(ModAttachments.SPELL_METADATA, meta);
    }

    public static void removeBlockState(Entity host, Spell spell, String key) {
        SpellMetadata meta = getAttachment(host);
        meta.removeBlockState(makeKey(spell, key));
        host.setData(ModAttachments.SPELL_METADATA, meta);
    }

    public static void setBlockPos(Entity host, Spell spell, String key, BlockPos value) {
        Minagic.LOGGER.debug("Attempting to set block pos to {} for spell {} at {}", host, spell, value);
        SpellMetadata meta = getAttachment(host);
        Minagic.LOGGER.debug("Successfully retrieved metadata, block map: {}", meta.blockPosMap);
        meta.setBlockPos(makeKey(spell, key), value);
        Minagic.LOGGER.debug("Successfully set block pos, new map: {}", meta.blockPosMap);
        host.setData(ModAttachments.SPELL_METADATA, meta);
        Minagic.LOGGER.debug("Successfully saved new metadata: {}", meta.blockPosMap);
    }

    public static void removeBlockPos(Entity host, Spell spell, String key) {
        SpellMetadata meta = getAttachment(host);
        meta.removeBlockPos(makeKey(spell, key));
        host.setData(ModAttachments.SPELL_METADATA, meta);
    }

    // =========================
    // INTERNAL HELPERS
    // =========================
    private static SpellMetadata getAttachment(Entity entity) {
        return entity.getData(ModAttachments.SPELL_METADATA);
    }

    // =========================
    // DANGER ZONE: DO NOT EDIT
    // =========================

    public static final Codec<Pair<ResourceLocation, String>> PAIR_CODEC = Codec.STRING.comapFlatMap(s -> {
        int i = s.indexOf('|');
        if (i <= 0) return null;
        try {
            ResourceLocation rl = ResourceLocation.parse(s.substring(0, i));
            String key = s.substring(i + 1);
            return DataResult.success(Pair.of(rl, key));
        } catch (Exception e) {
            return null;
        }
    }, pair -> pair.getFirst().toString() + "|" + pair.getSecond());

    public static final Codec<SpellMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(PAIR_CODEC, Codec.STRING).fieldOf("stringMap").forGetter(s -> s.stringMap),
            Codec.unboundedMap(PAIR_CODEC, Codec.INT).fieldOf("intMap").forGetter(s -> s.intMap),
            Codec.unboundedMap(PAIR_CODEC, BlockState.CODEC).fieldOf("blockStateMap").forGetter(s -> s.blockStateMap),
            Codec.unboundedMap(PAIR_CODEC, BlockPos.CODEC).fieldOf("blockPosMap").forGetter(s -> s.blockPosMap)
    ).apply(instance, (stringMap, intMap, blockStateMap, blockPosMap) -> {
        SpellMetadata meta = new SpellMetadata();
        meta.stringMap.putAll(stringMap);
        meta.intMap.putAll(intMap);
        meta.blockStateMap.putAll(blockStateMap);
        meta.blockPosMap.putAll(blockPosMap);
        return meta;
    }));
    // =========================
    // SERIALIZER
    // =========================
    public static class Serializer implements IAttachmentSerializer<SpellMetadata> {
        private static final String KEY_STRING_MAP = "stringMap";
        private static final String KEY_INT_MAP = "intMap";
        private static final String KEY_BLOCKSTATE_MAP = "blockStateMap";
        private static final String KEY_BLOCKPOS_MAP = "blockPosMap";

        @Override
        public @NotNull SpellMetadata read(@NotNull IAttachmentHolder holder, ValueInput input) {
            SpellMetadata metadata = new SpellMetadata();
            input.read(KEY_STRING_MAP, Codec.unboundedMap(PAIR_CODEC, Codec.STRING)).ifPresent(metadata.stringMap::putAll);
            input.read(KEY_INT_MAP, Codec.unboundedMap(PAIR_CODEC, Codec.INT)).ifPresent(metadata.intMap::putAll);
            input.read(KEY_BLOCKSTATE_MAP, Codec.unboundedMap(PAIR_CODEC, BlockState.CODEC)).ifPresent(metadata.blockStateMap::putAll);
            input.read(KEY_BLOCKPOS_MAP, Codec.unboundedMap(PAIR_CODEC, BlockPos.CODEC)).ifPresent(metadata.blockPosMap::putAll);
            return metadata;
        }

        @Override
        public boolean write(SpellMetadata attachment, ValueOutput output) {
            output.store(KEY_STRING_MAP, Codec.unboundedMap(PAIR_CODEC, Codec.STRING), attachment.stringMap);
            output.store(KEY_INT_MAP, Codec.unboundedMap(PAIR_CODEC, Codec.INT), attachment.intMap);
            output.store(KEY_BLOCKSTATE_MAP, Codec.unboundedMap(PAIR_CODEC, BlockState.CODEC), attachment.blockStateMap);
            output.store(KEY_BLOCKPOS_MAP, Codec.unboundedMap(PAIR_CODEC, BlockPos.CODEC), attachment.blockPosMap);
            return true;
        }
    }
}