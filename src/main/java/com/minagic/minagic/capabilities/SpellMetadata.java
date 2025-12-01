package com.minagic.minagic.capabilities;

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

public class SpellMetadata {

    private final Map<Pair<ResourceLocation, String>, String> stringMap = new HashMap<>();
    private final Map<Pair<ResourceLocation, String>, Integer> intMap = new HashMap<>();
    private final Map<Pair<ResourceLocation, String>, BlockState> blockStateMap = new HashMap<>();
    private final Map<Pair<ResourceLocation, String>, BlockPos> blockPosMap = new HashMap<>();

    public static final Codec<Pair<ResourceLocation, String>> PAIR_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("namespace").forGetter(Pair::getFirst),
            Codec.STRING.fieldOf("key").forGetter(Pair::getSecond)
    ).apply(instance, Pair::of));



    public static boolean has(Entity target, Spell spell, String key) {
        var a = getAttachment(target);
        return  a.stringMap.containsKey(makeKey(spell, key)) ||
                a.intMap.containsKey(makeKey(spell, key)) ||
                a.blockStateMap.containsKey(makeKey(spell, key)) ||
                a.blockPosMap.containsKey(makeKey(spell, key));
    }

    public static String getString(Entity target, Spell spell, String key) {
        return getAttachment(target).stringMap.get(makeKey(spell, key));
    }

    public static void setString(Entity target, Spell spell, String key, String value) {
        getAttachment(target).stringMap.put(makeKey(spell, key), value);
    }

    public static void removeString(Entity target, Spell spell, String key) {
        getAttachment(target).stringMap.remove(makeKey(spell, key));
    }

    public static Integer getInt(Entity target, Spell spell, String key) {
        return getAttachment(target).intMap.get(makeKey(spell, key));
    }

    public static void setInt(Entity target, Spell spell, String key, int value) {
        getAttachment(target).intMap.put(makeKey(spell, key), value);
    }

    public static void removeInt(Entity target, Spell spell, String key) {
        getAttachment(target).intMap.remove(makeKey(spell, key));
    }

    public static BlockState getBlockState(Entity target, Spell spell, String key) {
        return getAttachment(target).blockStateMap.get(makeKey(spell, key));
    }

    public static void setBlockState(Entity target, Spell spell, String key, BlockState value) {
        getAttachment(target).blockStateMap.put(makeKey(spell, key), value);
    }

    public static void removeBlockState(Entity target, Spell spell, String key) {
        getAttachment(target).blockStateMap.remove(makeKey(spell, key));
    }

    public static BlockPos getBlockPos(Entity target, Spell spell, String key) {
        return getAttachment(target).blockPosMap.get(makeKey(spell, key));
    }

    public static void setBlockPos(Entity target, Spell spell, String key, BlockPos value) {
        getAttachment(target).blockPosMap.put(makeKey(spell, key), value);
    }

    public static void removeBlockPos(Entity target, Spell spell, String key) {
        getAttachment(target).blockPosMap.remove(makeKey(spell, key));
    }


    private static Pair<ResourceLocation, String> makeKey(Spell spell, String key) {
        return Pair.of(ModSpells.getId(spell), key);
    }

    private static SpellMetadata getAttachment(Entity entity) {
        return entity.getData(ModAttachments.SPELL_METADATA); // Adjust according to your capability system
    }

    // CODEC
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