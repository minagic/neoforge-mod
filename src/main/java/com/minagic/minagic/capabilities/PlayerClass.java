package com.minagic.minagic.capabilities;

import com.minagic.minagic.capabilities.MagicClassEnums.DeityEnum;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerClassEnum;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerSubClassEnum;
import com.minagic.minagic.registries.ModAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class PlayerClass {

    // =========================
    // INTERNAL VARIABLES
    // =========================
    private final Map<PlayerSubClassEnum, Integer> subclasses;
    private PlayerClassEnum mainClass;
    private DeityEnum deity;

    // =========================
    // CONSTRUCTOR
    // =========================
    public PlayerClass() {
        this.mainClass = PlayerClassEnum.UNDECLARED;
        this.subclasses = new EnumMap<>(PlayerSubClassEnum.class);
        this.deity = DeityEnum.UNDECLARED;
    }

    // =========================
    // INSTANCE GETTERS
    // =========================
    public PlayerClassEnum getMainClass() {
        return mainClass;
    }

    public int getSubclassLevel(PlayerSubClassEnum subclass) {
        return subclasses.getOrDefault(subclass, 0);
    }

    public Map<PlayerSubClassEnum, Integer> getAllSubclasses() {
        return Collections.unmodifiableMap(subclasses);
    }

    public DeityEnum getDeity() {
        return deity;
    }

    // =========================
    // STATIC GETTERS
    // =========================
    public static PlayerClassEnum getMainClass(Entity host) {
        return host.getData(ModAttachments.PLAYER_CLASS).getMainClass();
    }

    public static int getSubclassLevel(Entity host, PlayerSubClassEnum subclass) {
        return host.getData(ModAttachments.PLAYER_CLASS).getSubclassLevel(subclass);
    }

    public static Map<PlayerSubClassEnum, Integer> getAllSubclasses(Entity host) {
        return host.getData(ModAttachments.PLAYER_CLASS).getAllSubclasses();
    }

    public static DeityEnum getDeity(Entity host) {
        return host.getData(ModAttachments.PLAYER_CLASS).getDeity();
    }

    // =========================
    // INSTANCE SETTERS
    // =========================
    public void setMainClass(PlayerClassEnum newMainClass) {
        this.mainClass = newMainClass;

        // Clear subclasses that don't match the new main class
        subclasses.entrySet().removeIf(entry ->
                entry.getKey().getParentClass() != newMainClass
        );

        // Clear deity if invalid
        if (!canHaveDeity(newMainClass)) {
            this.deity =  DeityEnum.UNDECLARED;
        }
    }

    public boolean setSubclassLevel(PlayerSubClassEnum subclass, int level) {
        if (subclass.getParentClass() != this.mainClass) return false;

        if (level <= 0) {
            subclasses.remove(subclass);
        } else {
            subclasses.put(subclass, level);
        }
        return true;
    }

    public boolean setDeity(DeityEnum deity) {
        if (!isValidDeityForClass(deity, this.mainClass)) return false;
        this.deity = deity;
        return true;
    }

    public void clearSubclasses() {
        subclasses.clear();
    }

    public void clearDeity() {
        this.deity = DeityEnum.UNDECLARED;
    }

    // =========================
    // STATIC SETTERS
    // =========================
    public static void setMainClass(Entity host, PlayerClassEnum clazz) {
        PlayerClass pc = host.getData(ModAttachments.PLAYER_CLASS);
        pc.setMainClass(clazz);
        host.setData(ModAttachments.PLAYER_CLASS, pc);
    }

    public static void setSubclassLevel(Entity host, PlayerSubClassEnum subclass, int level) {
        PlayerClass pc = host.getData(ModAttachments.PLAYER_CLASS);
        pc.setSubclassLevel(subclass, level);
        host.setData(ModAttachments.PLAYER_CLASS, pc);
    }

    public static void setDeity(Entity host, DeityEnum deity) {
        PlayerClass pc = host.getData(ModAttachments.PLAYER_CLASS);
        pc.setDeity(deity);
        host.setData(ModAttachments.PLAYER_CLASS, pc);
    }

    public static void clearSubclasses(Entity host) {
        PlayerClass pc = host.getData(ModAttachments.PLAYER_CLASS);
        pc.clearSubclasses();
        host.setData(ModAttachments.PLAYER_CLASS, pc);
    }

    public static void clearDeity(Entity host) {
        PlayerClass pc = host.getData(ModAttachments.PLAYER_CLASS);
        pc.clearDeity();
        host.setData(ModAttachments.PLAYER_CLASS, pc);
    }

    // =========================
    // INTERNAL METHODS
    // =========================
    private boolean isValidDeityForClass(DeityEnum deity, PlayerClassEnum clazz) {
        return switch (clazz) {
            case CLERIC -> deity.isCleric();
            case WARLOCK -> deity.isWarlock();
            default -> deity.getType() == DeityEnum.DeityType.NONE;
        };
    }

    private boolean canHaveDeity(PlayerClassEnum clazz) {
        return clazz == PlayerClassEnum.CLERIC || clazz == PlayerClassEnum.WARLOCK;
    }

    // =========================
    // RENDER
    // =========================
    public void render(GuiGraphics gui) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Font font = mc.font;
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int x = 10;
        int y = 80;
        int spacing = 12;

        final int COLOR_MAIN = 0xFFFFFFAA;
        final int COLOR_SUB = 0xFFCCCCCC;
        final int COLOR_DEITY = 0xFF88CCFF;

        gui.drawString(font, "Class: " + mainClass.name(), x, y, COLOR_MAIN, false);
        y += spacing;

        if (subclasses.isEmpty()) {
            gui.drawString(font, "No subclasses", x + 10, y, COLOR_SUB, false);
            y += spacing;
        } else {
            for (var entry : subclasses.entrySet()) {
                String text = entry.getKey().getString() + " - Lv. " + entry.getValue();
                gui.drawString(font, text, x + 10, y, COLOR_SUB, false);
                y += spacing;
            }
        }


        gui.drawString(font, "Deity: " + deity, x, (y + spacing), COLOR_DEITY, false);
    }

    // =========================
    // DANGER ZONE: DO NOT EDIT
    // =========================

    // CODEC
    public static final Codec<PlayerClass> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            PlayerClassEnum.CODEC.fieldOf("main_class").forGetter(PlayerClass::getMainClass),
            Codec.unboundedMap(PlayerSubClassEnum.CODEC, Codec.INT)
                    .optionalFieldOf("subclasses", Map.of()).forGetter(PlayerClass::getAllSubclasses),
            DeityEnum.CODEC.fieldOf("deity").forGetter(PlayerClass::getDeity)
    ).apply(inst, (mainClass, subclassMap, deity) -> {
        PlayerClass result = new PlayerClass();
        result.setMainClass(mainClass);
        subclassMap.forEach(result::setSubclassLevel);
        result.setDeity(deity);
        return result;
    }));

    // SERIALIZER
    public static class Serializer implements IAttachmentSerializer<PlayerClass> {

        @Override
        public @NotNull PlayerClass read(@NotNull IAttachmentHolder holder, ValueInput input) {
            PlayerClass result = new PlayerClass();
            input.read("player_class", PlayerClass.CODEC)
                    .ifPresent(pc -> {
                        result.setMainClass(pc.getMainClass());
                        pc.getAllSubclasses().forEach(result::setSubclassLevel);
                        result.setDeity(pc.getDeity());
                    });
            return result;
        }

        @Override
        public boolean write(@NotNull PlayerClass attachment, ValueOutput output) {
            output.store("player_class", PlayerClass.CODEC, attachment);
            return true;
        }
    }
}