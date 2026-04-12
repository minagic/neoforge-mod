package com.minagic.minagic.capabilities.powersource;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SorceryPowerSourceAttachment
        extends AbstractPowerSource
        implements AutoDetection.ILivingTickableAttachment,
        AutoDetection.IRenderableAttachment {

    // =========================
    // CONSTANTS
    // =========================

    public static final String BLOODLINE_CELESTIAL   = "celestial";
    public static final String BLOODLINE_SPIRITUAL  = "spiritual";
    public static final String BLOODLINE_DRACONIC = "draconic";
    public static final String BLOODLINE_VOIDBOURNE  = "voidbourne";
    public static final String BLOODLINE_INFERNAL  = "infernal";

    private static final int DEFAULT_MAX_MANA = 100;
    private static final float DEFAULT_FLUX_STABILITY = 1.0f;

    // =========================
    // SPELL INTERFACE
    // =========================

    public interface ISorcerySpell {
        String getRequiredBloodline();
        int getRequiredAffinityLevel();
    }

    // =========================
    // INTERNAL STATE
    // =========================

    private int mana;
    private int maxMana;

    // internal power numbers
    private final Map<String, Integer> bloodlineAffinity = new HashMap<>();

    // player-facing ranks / tiers
    private final Map<String, Integer> bloodlineRank = new HashMap<>();

    private float fluxStability;

    // =========================
    // CONSTRUCTOR
    // =========================

    public SorceryPowerSourceAttachment() {
        super(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "power_source_sourcery"));
        deactivate();
    }

    // =========================
    // PREREQUISITES
    // =========================

    @Override
    public boolean checkPrerequisites(@NotNull Spell spell) {
        if (!(spell instanceof ISorcerySpell sorcerySpell)) {
            Minagic.LOGGER.debug("Error: Magic of invalid domain, spell unbound");
            return false;
        }

        String requiredBloodline = sorcerySpell.getRequiredBloodline();
        int requiredAffinity = sorcerySpell.getRequiredAffinityLevel();
        Minagic.LOGGER.debug("Requirements: {} : {}, have: {}", requiredBloodline, requiredAffinity, bloodlineRank);
        return getRank(requiredBloodline) >= requiredAffinity;
    }


    // =========================
    // MAGIC COST
    // =========================

    @Override
    public boolean canConsume(
            @NotNull SpellCastContext context,
            @NotNull SimulacrumData simulacrum,
            int magicCost
    ) {
        return mana >= magicCost;
    }

    @Override
    public void consume(
            @NotNull SpellCastContext context,
            @NotNull SimulacrumData simulacrum,
            int magicCost
    ) {
        this.mana = Math.max(0, mana - magicCost);
    }

    @Override
    public void onFail(
            @NotNull LivingEntity caster,
            int magicCost
    ) {
    }

    @Override
    public void deactivate() {
        // Power shutdown
        this.mana = 0;
        this.fluxStability = DEFAULT_FLUX_STABILITY;
        this.maxMana = DEFAULT_MAX_MANA;

        // Explicitly invalidate all known bloodlines
        bloodlineAffinity.put(BLOODLINE_CELESTIAL,   -1);
        bloodlineAffinity.put(BLOODLINE_SPIRITUAL,   -1);
        bloodlineAffinity.put(BLOODLINE_DRACONIC,    -1);
        bloodlineAffinity.put(BLOODLINE_VOIDBOURNE,  -1);
        bloodlineAffinity.put(BLOODLINE_INFERNAL,    -1);

        bloodlineRank.put(BLOODLINE_CELESTIAL,   -1);
        bloodlineRank.put(BLOODLINE_SPIRITUAL,   -1);
        bloodlineRank.put(BLOODLINE_DRACONIC,    -1);
        bloodlineRank.put(BLOODLINE_VOIDBOURNE,  -1);
        bloodlineRank.put(BLOODLINE_INFERNAL,    -1);
    }

    // =========================
    // TICK
    // =========================

    @Override
    public void tick(LivingEntity host) {
        if (host.level().isClientSide()) return;

        if (mana < maxMana) {
            mana += 1;
        }
    }

    // =========================
    // RENDER
    // =========================


    @Override
    public boolean shouldRender(LivingEntity host) {
        //Minagic.LOGGER.debug("Attempting rendering for host: {}, found attachment: {}, it is {}instance of SorceryPowerAttachment", host, getActivePowerSource(host), getActivePowerSource(host) instanceof SorceryPowerSourceAttachment ? "" : "not an ");
        return getActivePowerSource(host) instanceof SorceryPowerSourceAttachment;
    }

    @Override
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

        float ratio = Mth.clamp((float) mana / maxMana, 0f, 1f);

        int y = screenHeight - 10; // anchored above hotbar

        // Background
        gui.fill(PADDING, y, PADDING + BAR_WIDTH, y + BAR_HEIGHT, COLOR_BACKGROUND);
        // Fill
        gui.fill(PADDING, y, PADDING + (int) (BAR_WIDTH * ratio), y + BAR_HEIGHT, COLOR_FILL);

        // Text
        String text = String.format("Mana: %.0f / %.0f", (float) mana, (float) maxMana);
        int textY = y - 10;
        gui.drawString(font, text, PADDING, textY, COLOR_TEXT, false);

        List<String> bloodlineLines = new ArrayList<>();
        addBloodlineLine(bloodlineLines, "Celestial", getAffinity(BLOODLINE_CELESTIAL), getRank(BLOODLINE_CELESTIAL));
        addBloodlineLine(bloodlineLines, "Spiritual", getAffinity(BLOODLINE_SPIRITUAL), getRank(BLOODLINE_SPIRITUAL));
        addBloodlineLine(bloodlineLines, "Draconic", getAffinity(BLOODLINE_DRACONIC), getRank(BLOODLINE_DRACONIC));
        addBloodlineLine(bloodlineLines, "Voidbourne", getAffinity(BLOODLINE_VOIDBOURNE), getRank(BLOODLINE_VOIDBOURNE));
        addBloodlineLine(bloodlineLines, "Infernal", getAffinity(BLOODLINE_INFERNAL), getRank(BLOODLINE_INFERNAL));

        int lineY = textY - 10;
        for (String line : bloodlineLines) {
            gui.drawString(font, line, PADDING, lineY, COLOR_TEXT, false);
            lineY -= 10;
        }

        // =========================
        // AFFINITY DISPLAY
        // =========================

        int textY2 = y - 22; // start above mana text
        int lineSpacing = 9;

        for (String bloodline : new String[] {
                BLOODLINE_CELESTIAL,
                BLOODLINE_SPIRITUAL,
                BLOODLINE_DRACONIC,
                BLOODLINE_VOIDBOURNE,
                BLOODLINE_INFERNAL
        }) {
            int rank = getRank(bloodline);
            if (rank < 0) continue; // inactive / sealed

            String stars = "★".repeat(Math.max(0, rank));
            String label = capitalize(bloodline) + "  " + stars;

            gui.drawString(font, label, PADDING, textY2, 0xFFE6CCFF, false);
            textY2 -= lineSpacing;
        }
    }

    private static String capitalize(String s) {
        return s.isEmpty()
                ? s
                : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static void addBloodlineLine(
            List<String> lines,
            String name,
            int affinity,
            int rank
    ) {
        if (affinity == -1 && rank == -1) {
            return;
        }
        StringBuilder line = new StringBuilder(name).append(": ");
        boolean wrote = false;
        if (rank != -1) {
            line.append("Rank ").append(rank);
            wrote = true;
        }
        if (affinity != -1) {
            if (wrote) {
                line.append(" / ");
            }
            line.append("Affinity ").append(affinity);
        }
        lines.add(line.toString());
    }

    // =========================
    // INSTANCE GETTERS
    // =========================

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public float getFluxStability() {
        return fluxStability;
    }

    public int getAffinity(String bloodline) {
        return bloodlineAffinity.getOrDefault(bloodline, 0);
    }

    public int getRank(String bloodline) {
        return bloodlineRank.getOrDefault(bloodline, 0);
    }

    // =========================
    // INSTANCE SETTERS
    // =========================

    public void setMana(int value) {
        this.mana = Mth.clamp(value, 0, maxMana);
    }

    public void setMaxMana(int value) {
        this.maxMana = Math.max(0, value);
        this.mana = Math.min(mana, maxMana);
    }

    public void setAffinity(String bloodline, int value) {
        bloodlineAffinity.put(bloodline, value);
    }

    public void setRank(String bloodline, int value) {
        bloodlineRank.put(bloodline, value);
    }

    public void setFluxStability(float value) {
        this.fluxStability = value;
    }

    // =========================
    // STATIC SETTERS
    // =========================

    public static void setMana(Entity host, int value) {
        SorceryPowerSourceAttachment att = getAttachment(host);
        att.setMana(value);
        writeBack(host, att);
    }

    public static void setMaxMana(Entity host, int value) {
        SorceryPowerSourceAttachment att = getAttachment(host);
        att.setMaxMana(value);
        writeBack(host, att);
    }

    public static void setAffinity(Entity host, String bloodline, int value) {
        SorceryPowerSourceAttachment att = getAttachment(host);
        att.setAffinity(bloodline, value);
        writeBack(host, att);
    }

    public static void setRank(Entity host, String bloodline, int value) {
        SorceryPowerSourceAttachment att = getAttachment(host);
        att.setRank(bloodline, value);
        writeBack(host, att);
    }

    public static void setFluxStability(Entity host, float value) {
        SorceryPowerSourceAttachment att = getAttachment(host);
        att.setFluxStability(value);
        writeBack(host, att);
    }

    // =========================
    // STATIC API
    // =========================

    public static SorceryPowerSourceAttachment getAttachment(Entity host) {
        return (SorceryPowerSourceAttachment)
                host.getData(ModAttachments.SORCERY_POWER_SOURCE);
    }

    private static void writeBack(Entity host, SorceryPowerSourceAttachment att) {
        host.setData(ModAttachments.SORCERY_POWER_SOURCE, att);
    }



    // =========================
    // CODEC
    // =========================

    public static final Codec<SorceryPowerSourceAttachment> CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Codec.INT.fieldOf("mana").forGetter(a -> a.mana),
                    Codec.INT.fieldOf("max_mana").forGetter(a -> a.maxMana),
                    Codec.unboundedMap(Codec.STRING, Codec.INT)
                            .optionalFieldOf("affinity", Map.of())
                            .forGetter(a -> a.bloodlineAffinity),
                    Codec.unboundedMap(Codec.STRING, Codec.INT)
                            .optionalFieldOf("rank", Map.of())
                            .forGetter(a -> a.bloodlineRank),
                    Codec.FLOAT.fieldOf("flux")
                            .forGetter(a -> a.fluxStability)
            ).apply(inst, (mana, maxMana, affinity, rank, flux) -> {
                SorceryPowerSourceAttachment att = new SorceryPowerSourceAttachment();
                att.mana = mana;
                att.maxMana = maxMana;
                att.bloodlineAffinity.putAll(affinity);
                att.bloodlineRank.putAll(rank);
                att.fluxStability = flux;
                return att;
            }));

    // =========================
    // SERIALIZER
    // =========================

    public static class Serializer
            implements IAttachmentSerializer<SorceryPowerSourceAttachment> {

        @Override
        public @NotNull SorceryPowerSourceAttachment read(
                @NotNull IAttachmentHolder holder,
                ValueInput input
        ) {
            SorceryPowerSourceAttachment att = new SorceryPowerSourceAttachment();

            input.read("mana", Codec.INT).ifPresent(att::setMana);
            input.read("max_mana", Codec.INT).ifPresent(att::setMaxMana);
            input.read("affinity",
                            Codec.unboundedMap(Codec.STRING, Codec.INT))
                    .ifPresent(att.bloodlineAffinity::putAll);
            input.read("rank",
                            Codec.unboundedMap(Codec.STRING, Codec.INT))
                    .ifPresent(att.bloodlineRank::putAll);
            input.read("flux", Codec.FLOAT).ifPresent(att::setFluxStability);

            return att;
        }

        @Override
        public boolean write(
                SorceryPowerSourceAttachment att,
                ValueOutput output
        ) {
            output.store("mana", Codec.INT, att.mana);
            output.store("max_mana", Codec.INT, att.maxMana);

            if (!att.bloodlineAffinity.isEmpty()) {
                output.store("affinity",
                        Codec.unboundedMap(Codec.STRING, Codec.INT),
                        att.bloodlineAffinity);
            }

            if (!att.bloodlineRank.isEmpty()) {
                output.store("rank",
                        Codec.unboundedMap(Codec.STRING, Codec.INT),
                        att.bloodlineRank);
            }

            output.store("flux", Codec.FLOAT, att.fluxStability);
            return true;
        }
    }
}
