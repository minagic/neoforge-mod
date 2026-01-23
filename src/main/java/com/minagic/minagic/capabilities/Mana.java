package com.minagic.minagic.capabilities;

import com.minagic.minagic.registries.ModAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.HashMap;
import java.util.Map;



public final class Mana {
    private float mana;
    private int maxMana;

    public Mana() {}

    public void tick(LivingEntity player) {
        // --- Retrieve Player Class Info ---
        PlayerClass pc = player.getData(ModAttachments.PLAYER_CLASS);

        int computedMax = computeMaxMana(player, pc);

        // Adjust current mana if needed
        if (mana > computedMax) {
            mana = computedMax;
        }

        this.maxMana = computedMax;



        // Sorcerer passive regen
        restoreMana(1);
        // Optional: log or effect if mana restored


    }

    private int computeMaxMana(LivingEntity player, PlayerClass pc) {
        if (!(player instanceof Player)) {
            return 1000; // Non-player entities have a lot of mana
        }
        int base = switch (pc.getMainClass()) {
            case SORCERER -> 120;
            case CLERIC -> 100;
            case DRUID -> 110;
            case WIZARD -> 90;
            case BARD -> 95;
            default -> 80;
        };

        // Subclass bonuses
        base += pc.getSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE) * 5;
        base += pc.getSubclassLevel(PlayerSubClassEnum.CLERIC_ORACLE) * 3;
        base += pc.getSubclassLevel(PlayerSubClassEnum.DRUID_SPIRITS) * 2;

        return base;
    }

    public float getMana() {return mana;}

    public boolean drainMana(float amount) {
        boolean success = mana - amount >= 0;
        if (!success) return false;
        mana = Math.max(0f, mana - amount);
        return true;
    }

    public boolean restoreMana(float amount) {
        boolean success = mana + amount <= maxMana;
        mana = Math.min(maxMana, mana + amount);
        return success;
    }

    public int getMaxMana() {return maxMana;}

    public void setMaxMana(int maxMana) {this.maxMana = maxMana;}


    // rendering
    public void render(GuiGraphics gui) {
        final int BAR_WIDTH = 100;
        final int BAR_HEIGHT = 8;
        final int PADDING = 8;
        final int COLOR_BACKGROUND = 0xFF222222;
        final int COLOR_FILL = 0xFF3399FF; // mana blue
        final int COLOR_TEXT = 0xFF99CCFF;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;

        Font font = mc.font;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        float ratio = Mth.clamp(mana / maxMana, 0f, 1f);

        int x = PADDING;
        int y = screenHeight - 10; // anchored above hotbar

        // Background
        gui.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, COLOR_BACKGROUND);
        // Fill
        gui.fill(x, y, x + (int) (BAR_WIDTH * ratio), y + BAR_HEIGHT, COLOR_FILL);

        // Text
        String text = String.format("Mana: %.0f / %.0f", mana, (float) maxMana);
        gui.drawString(font, text, x, y - 10, COLOR_TEXT, false);
    }

    // CODEC
    public static final Codec<Mana> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("mana").forGetter(Mana::getMana),
            Codec.INT.fieldOf("maxMana").forGetter(Mana::getMaxMana)
    ).apply(instance, (manaValue, maxManaValue) -> {
        Mana m = new Mana();
        m.setMaxMana(maxManaValue);
        m.restoreMana(manaValue-m.getMana());
        return m;
    }));

    // SERIALIZER

    public static class Serializer implements IAttachmentSerializer<Mana> {
        private static final String KEY_MANA = "mana";
        private static final String KEY_MAX_MANA = "maxMana";

        @Override
        public Mana read(IAttachmentHolder holder, ValueInput input) {
            Mana mana = new Mana();
            input.read(KEY_MAX_MANA, Codec.INT).ifPresent(mana::setMaxMana);
            // Read both mana and maxMana if present
            input.read(KEY_MANA, Codec.INT).ifPresent(value -> {
                // Clamp to ensure no invalid data
                mana.restoreMana(value - mana.getMana());
            });

            return mana;
        }

        @Override
        public boolean write(Mana attachment, ValueOutput output) {
            output.store(KEY_MANA, Codec.FLOAT, attachment.getMana());
            output.store(KEY_MAX_MANA, Codec.INT, attachment.getMaxMana());
            return true;
        }
    }


}