package com.minagic.minagic.capabilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class PlayerClass {

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
    private final Map<PlayerSubClassEnum, Integer> subclasses;
    private PlayerClassEnum mainClass;
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

    // RENDER
    public void render(GuiGraphics gui) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Font font = mc.font;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // --- Positioning ---
        int x = 10; // left margin
        int y = 80; // start below top HUD
        int spacing = 12;

        // --- Colors ---
        final int COLOR_MAIN = 0xFFFFFFAA;   // gold-white for main class
        final int COLOR_SUB = 0xFFCCCCCC;    // light gray for subclasses
        final int COLOR_DEITY = 0xFF88CCFF;  // pale blue for deity label

        // --- Draw Main Class ---
        String mainClassStr = "Class: " + mainClass.name();
        gui.drawString(font, mainClassStr, x, y, COLOR_MAIN, false);
        y += spacing;

        // --- Draw Subclasses ---
        if (subclasses.isEmpty()) {
            gui.drawString(font, "No subclasses", x + 10, y, COLOR_SUB, false);
            y += spacing;
        } else {
            for (Map.Entry<PlayerSubClassEnum, Integer> entry : subclasses.entrySet()) {
                PlayerSubClassEnum subclass = entry.getKey();
                int level = entry.getValue();
                String subclassText = subclass.getString() + " - Lv. " + level;
                gui.drawString(font, subclassText, x + 10, y, COLOR_SUB, false);
                y += spacing;
            }
        }

        // --- Draw Deity (if present) ---
        if (deity.isPresent() && deity.get() != Deity.UNDECLARED) {
            Deity d = deity.get();
            String deityText = "Deity: " + d;
            gui.drawString(font, deityText, x, y + spacing, COLOR_DEITY, false);
        }
    }

    // SERIALIZER
    public static class Serializer implements IAttachmentSerializer<PlayerClass> {

        @Override
        public PlayerClass read(IAttachmentHolder holder, ValueInput input) {
            PlayerClass result = new PlayerClass();
            input.read("player_class", PlayerClass.CODEC)
                    .ifPresentOrElse(
                            pc -> {
                                result.setMainClass(pc.getMainClass());
                                pc.getAllSubclasses().forEach(result::setSubclassLevel);
                                pc.getDeity().ifPresent(result::setDeity);
                            },
                            () -> {
                                // fallback or leave default UNDECLARED
                            });
            return result;
        }

        @Override
        public boolean write(PlayerClass attachment, ValueOutput output) {
            output.store("player_class", PlayerClass.CODEC, attachment);
            return true;
        }
    }
}