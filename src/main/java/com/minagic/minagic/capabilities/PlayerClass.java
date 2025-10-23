package com.minagic.minagic.capabilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class PlayerClass {

    private PlayerClassEnum mainClass;
    private final Map<PlayerSubClassEnum, Integer> subclasses;
    private Optional<Deity> deity;

    public PlayerClass() {
        this.mainClass = PlayerClassEnum.UNDECLARED;
        this.subclasses = new EnumMap<>(PlayerSubClassEnum.class);
        this.deity = Optional.empty();
    }

    // Main class
    public PlayerClassEnum getMainClass() {
        return mainClass;
    }

    public void setMainClass(PlayerClassEnum newMainClass) {
        this.mainClass = newMainClass;
        // Clear subclasses that don't match the new main class
        subclasses.entrySet().removeIf(entry -> entry.getKey().getParentClass() != newMainClass);
        // Clear deity if it's no longer valid
        if (!canHaveDeity(newMainClass)) {
            this.deity = Optional.empty();
        }
    }

    // Subclasses
    public boolean setSubclassLevel(PlayerSubClassEnum subclass, int level) {
        if (subclass.getParentClass() != this.mainClass) return false;
        if (level <= 0) {
            subclasses.remove(subclass);
        } else {
            subclasses.put(subclass, level);
        }
        return true;
    }

    public int getSubclassLevel(PlayerSubClassEnum subclass) {
        return subclasses.getOrDefault(subclass, 0);
    }

    public Map<PlayerSubClassEnum, Integer> getAllSubclasses() {
        return Collections.unmodifiableMap(subclasses);
    }

    public void clearSubclasses() {
        subclasses.clear();
    }

    // Deity
    public Optional<Deity> getDeity() {
        return deity;
    }

    public boolean setDeity(Deity deity) {
        if (!isValidDeityForClass(deity, this.mainClass)) return false;
        this.deity = Optional.of(deity);
        return true;
    }

    public void clearDeity() {
        this.deity = Optional.empty();
    }

    private boolean isValidDeityForClass(Deity deity, PlayerClassEnum clazz) {
        return switch (clazz) {
            case CLERIC -> deity.isCleric();
            case WARLOCK -> deity.isWarlock();
            default -> deity.getType() == Deity.DeityType.NONE;
        };
    }

    private boolean canHaveDeity(PlayerClassEnum clazz) {
        return clazz == PlayerClassEnum.CLERIC || clazz == PlayerClassEnum.WARLOCK;
    }

    // CODEC
    public static final Codec<PlayerClass> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            PlayerClassEnum.CODEC.fieldOf("main_class").forGetter(PlayerClass::getMainClass),
            Codec.unboundedMap(PlayerSubClassEnum.CODEC, Codec.INT)
                    .optionalFieldOf("subclasses", Map.of()).forGetter(PlayerClass::getAllSubclasses),
            Deity.CODEC.optionalFieldOf("deity").forGetter(PlayerClass::getDeity)
    ).apply(inst, (mainClass, subclassMap, deityOpt) -> {
        PlayerClass result = new PlayerClass();
        result.setMainClass(mainClass);
        subclassMap.forEach(result::setSubclassLevel);
        deityOpt.ifPresent(result::setDeity);
        return result;
    }));
}