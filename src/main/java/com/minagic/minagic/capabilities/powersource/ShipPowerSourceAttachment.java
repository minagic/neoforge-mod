package com.minagic.minagic.capabilities.powersource;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.common.events.custom.StatCollectEvent;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;


public final class ShipPowerSourceAttachment
        extends AbstractPowerSource
        implements AutoDetection.ILivingTickableAttachment {

    // =========================
    // CONSTANTS
    // =========================

    public static final int DEFAULT_MAX_FUEL = 20_000;
    public static final int FUEL_REGEN_PER_TICK = 1;

    // =========================
    // INTERNAL STATE
    // =========================

    private int fuel;
    private int maxFuel;

    // =========================
    // CONSTRUCTOR
    // =========================

    public ShipPowerSourceAttachment() {
        super(ResourceLocation.fromNamespaceAndPath(
                Minagic.MODID,
                "power_source_ship"
        ));

        this.maxFuel = DEFAULT_MAX_FUEL;
        this.fuel = DEFAULT_MAX_FUEL;
    }

    // =========================
    // PREREQUISITES
    // =========================

    /**
     * Ship power accepts any spell routed through it.
     *
     * Compatibility restrictions can instead be handled by the spell's
     * custom gate chain.
     */
    @Override
    public boolean checkPrerequisites(@NotNull Spell spell) {
        return true;
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
        return magicCost >= 0 && fuel >= magicCost;
    }

    @Override
    public void consume(
            @NotNull SpellCastContext context,
            @NotNull SimulacrumData simulacrum,
            int magicCost
    ) {
        if (magicCost <= 0) {
            return;
        }

        fuel = Math.max(0, fuel - magicCost);
    }

    @Override
    public void onFail(
            @NotNull LivingEntity caster,
            int magicCost
    ) {
        // No failure effect for now.
    }

    /**
     * Resets this attachment to its default state.
     *
     * Since newly created ship power sources are intended to start full,
     * resetting also restores full fuel.
     */
    @Override
    public void deactivate() {
        this.maxFuel = DEFAULT_MAX_FUEL;
        this.fuel = DEFAULT_MAX_FUEL;
    }

    // =========================
    // TICK
    // =========================

    @Override
    public void tick(LivingEntity host) {
        if (host.level().isClientSide()) {
            return;
        }

        if (fuel < maxFuel) {
            fuel = Math.min(
                    maxFuel,
                    fuel + FUEL_REGEN_PER_TICK
            );
        }
    }

    // =========================
    // INSTANCE GETTERS
    // =========================

    public int getFuel() {
        return fuel;
    }

    public int getMaxFuel() {
        return maxFuel;
    }

    public float getFuelRatio() {
        if (maxFuel <= 0) {
            return 0.0F;
        }

        return Mth.clamp(
                (float) fuel / (float) maxFuel,
                0.0F,
                1.0F
        );
    }

    public boolean hasFuel(int amount) {
        return amount >= 0 && fuel >= amount;
    }

    // =========================
    // INSTANCE SETTERS
    // =========================

    public void setFuel(int value) {
        this.fuel = Mth.clamp(value, 0, maxFuel);
    }

    public void setMaxFuel(int value) {
        this.maxFuel = Math.max(0, value);
        this.fuel = Math.min(fuel, maxFuel);
    }

    public void addFuel(int amount) {
        if (amount <= 0) {
            return;
        }

        setFuel(fuel + amount);
    }

    public int removeFuel(int requestedAmount) {
        if (requestedAmount <= 0) {
            return 0;
        }

        int removed = Math.min(fuel, requestedAmount);
        fuel -= removed;

        return removed;
    }

    // =========================
    // STATIC SETTERS
    // =========================

    public static void setFuel(LivingEntity host, int value) {
        ShipPowerSourceAttachment attachment = getAttachment(host);
        attachment.setFuel(value);
        writeBack(host, attachment);
    }

    public static void setMaxFuel(LivingEntity host, int value) {
        ShipPowerSourceAttachment attachment = getAttachment(host);
        attachment.setMaxFuel(value);
        writeBack(host, attachment);
    }

    public static void addFuel(LivingEntity host, int amount) {
        ShipPowerSourceAttachment attachment = getAttachment(host);
        attachment.addFuel(amount);
        writeBack(host, attachment);
    }

    public static int removeFuel(LivingEntity host, int requestedAmount) {
        ShipPowerSourceAttachment attachment = getAttachment(host);
        int removed = attachment.removeFuel(requestedAmount);
        writeBack(host, attachment);

        return removed;
    }

    // =========================
    // STATIC API
    // =========================

    public static ShipPowerSourceAttachment getAttachment(LivingEntity host) {
        return host.getData(ModAttachments.SHIP_POWER_SOURCE);
    }

    private static void writeBack(
            LivingEntity host,
            ShipPowerSourceAttachment attachment
    ) {
        host.setData(
                ModAttachments.SHIP_POWER_SOURCE,
                attachment
        );
    }

    // =========================
    // STAT CONTRIBUTIONS
    // =========================

    @Override
    public <T extends StatCollectEvent> void contributeTo(T event) {
        // Ship power currently contributes no spell statistics.
    }

    // =========================
    // CODEC
    // =========================

    public static final Codec<ShipPowerSourceAttachment> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT
                            .fieldOf("fuel")
                            .forGetter(attachment -> attachment.fuel),

                    Codec.INT
                            .fieldOf("max_fuel")
                            .forGetter(attachment -> attachment.maxFuel)
            ).apply(instance, (fuel, maxFuel) -> {
                ShipPowerSourceAttachment attachment =
                        new ShipPowerSourceAttachment();

                attachment.maxFuel = Math.max(0, maxFuel);
                attachment.fuel = Mth.clamp(
                        fuel,
                        0,
                        attachment.maxFuel
                );

                return attachment;
            }));

    // =========================
    // SERIALIZER
    // =========================

    public static final class Serializer
            implements IAttachmentSerializer<ShipPowerSourceAttachment> {

        @Override
        public @NotNull ShipPowerSourceAttachment read(
                @NotNull IAttachmentHolder holder,
                ValueInput input
        ) {
            ShipPowerSourceAttachment attachment =
                    new ShipPowerSourceAttachment();

            /*
             * Read max fuel first so that setFuel() clamps against the
             * loaded capacity rather than the default capacity.
             */
            input.read("max_fuel", Codec.INT)
                    .ifPresent(attachment::setMaxFuel);

            input.read("fuel", Codec.INT)
                    .ifPresent(attachment::setFuel);

            return attachment;
        }

        @Override
        public boolean write(
                ShipPowerSourceAttachment attachment,
                ValueOutput output
        ) {
            output.store(
                    "fuel",
                    Codec.INT,
                    attachment.fuel
            );

            output.store(
                    "max_fuel",
                    Codec.INT,
                    attachment.maxFuel
            );

            return true;
        }
    }
}