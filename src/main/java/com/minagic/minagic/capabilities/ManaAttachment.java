package com.minagic.minagic.capabilities;

import com.minagic.minagic.api.SpellcastingItem;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerSubClassEnum;
import com.minagic.minagic.registries.ModAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;


public final class ManaAttachment implements AutodetectionInterfaces.IRenderableAttachment, AutodetectionInterfaces.ILivingTickableAttachment {

    // =========================
    // INTERNAL VARIABLES
    // =========================
    private float mana;
    private int maxMana;

    // =========================
    // CONSTRUCTOR
    // =========================
    public ManaAttachment() {
        this.maxMana = 0;
        this.mana = 0;
    }

    // =========================
    // INSTANCE GETTERS
    // =========================
    public float getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    // =========================
    // STATIC GETTERS
    // =========================
    public static float getMana(Entity host) {
        return getAttachment(host).getMana();
    }

    public static int getMaxMana(Entity host) {
        return getAttachment(host).getMaxMana();
    }

    // =========================
    // INSTANCE SETTERS
    // =========================
    public void drainMana(float amount) {
        mana = Math.max(0f, mana - amount);
    }

    public void restoreMana(float amount) {
        mana = Math.min(maxMana, mana + amount);
    }


    public void setMaxMana(int maxMana) {
        this.maxMana = Math.max(0, maxMana);
        this.mana = Math.min(this.mana, this.maxMana);
    }

    // =========================
    // STATIC SETTERS
    // =========================
    public static void drainMana(Entity host, float amount) {
        ManaAttachment manaAttachment = getAttachment(host);
        manaAttachment.drainMana(amount);
        host.setData(ModAttachments.MANA, manaAttachment);
    }

    public static void restoreMana(Entity host, float amount) {
        ManaAttachment manaAttachment = getAttachment(host);
        manaAttachment.restoreMana(amount);
        host.setData(ModAttachments.MANA, manaAttachment);
    }

    public static void setMaxMana(Entity host, int maxMana) {
        ManaAttachment manaAttachment = getAttachment(host);
        manaAttachment.setMaxMana(maxMana);
        host.setData(ModAttachments.MANA, manaAttachment);
    }

    // =========================
    // INTERNAL METHODS
    // =========================
    public void tick(LivingEntity host) {
        PlayerClass pc = host.getData(ModAttachments.PLAYER_CLASS);

        int computedMax = computeMaxMana(host, pc);

        // Adjust current mana if needed
        if (mana > computedMax) {
            mana = computedMax;
        }

        this.maxMana = computedMax;

        restoreMana(1);


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

    // =========================
    // RENDER
    // =========================
    public void render(LivingEntity host, GuiGraphics gui) {
        final int BAR_WIDTH = 100;
        final int BAR_HEIGHT = 8;
        final int PADDING = 8;
        final int COLOR_BACKGROUND = 0xFF222222;
        final int COLOR_FILL = 0xFF3399FF; // mana blue
        final int COLOR_TEXT = 0xFF99CCFF;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Font font = mc.font;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        float ratio = Mth.clamp(mana / maxMana, 0f, 1f);

        int y = screenHeight - 10; // anchored above hotbar

        // Background
        gui.fill(PADDING, y, PADDING + BAR_WIDTH, y + BAR_HEIGHT, COLOR_BACKGROUND);
        // Fill
        gui.fill(PADDING, y, PADDING + (int) (BAR_WIDTH * ratio), y + BAR_HEIGHT, COLOR_FILL);

        // Text
        String text = String.format("Mana: %.0f / %.0f", mana, (float) maxMana);
        gui.drawString(font, text, PADDING, y - 10, COLOR_TEXT, false);
    }

    @Override
    public boolean shouldRender(LivingEntity host) {
        return host.getMainHandItem().getItem() instanceof SpellcastingItem<?>;
    }

    // =========================
    // INTERNAL HELPERS
    // =========================
    private static ManaAttachment getAttachment(Entity entity) {
        return entity.getData(ModAttachments.MANA);
    }

    // =========================
    // DANGER ZONE: DO NOT EDIT
    // =========================

    // CODEC
    public static final Codec<ManaAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("mana").forGetter(ManaAttachment::getMana),
            Codec.INT.fieldOf("maxMana").forGetter(ManaAttachment::getMaxMana)
    ).apply(instance, (manaValue, maxManaValue) -> {
        ManaAttachment m = new ManaAttachment();
        m.setMaxMana(maxManaValue);
        m.restoreMana(manaValue - m.getMana());
        return m;
    }));

    // SERIALIZER

    public static class Serializer implements IAttachmentSerializer<ManaAttachment> {
        private static final String KEY_MANA = "mana";
        private static final String KEY_MAX_MANA = "maxMana";

        @Override
        public @NotNull ManaAttachment read(@NotNull IAttachmentHolder holder, ValueInput input) {
            ManaAttachment manaAttachment = new ManaAttachment();
            input.read(KEY_MAX_MANA, Codec.INT).ifPresent(manaAttachment::setMaxMana);
            // Read both manaAttachment and maxMana if present
            input.read(KEY_MANA, Codec.INT).ifPresent(value -> {
                // Clamp to ensure no invalid data
                manaAttachment.restoreMana(value - manaAttachment.getMana());
            });

            return manaAttachment;
        }

        @Override
        public boolean write(ManaAttachment attachment, ValueOutput output) {
            output.store(KEY_MANA, Codec.FLOAT, attachment.getMana());
            output.store(KEY_MAX_MANA, Codec.INT, attachment.getMaxMana());
            return true;
        }
    }


}
