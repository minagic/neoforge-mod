package com.minagic.minagic.capabilities.powersource;

import com.minagic.minagic.api.spells.Spell;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.common.events.custom.StatCollectEvent;
import com.minagic.minagic.scaling.StatCollector;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class AbstractPowerSource {

    private final ResourceLocation id;

    protected AbstractPowerSource(@NotNull ResourceLocation id) {
        this.id = id;
    }

    // =========================
    // IDENTIFIER
    // =========================

    public final ResourceLocation getId() {
        return id;
    }

    // =========================
    // PREREQUISITES
    // =========================

    public abstract boolean checkPrerequisites(@NotNull Spell spell);

    // =========================
    // MAGIC COST API (INSTANCE)
    // =========================

    public abstract boolean canConsume(
            @NotNull SpellCastContext context,
            @Nullable SimulacrumData simulacrum,
            int magicCost
    );

    public abstract void consume(
            @NotNull SpellCastContext context,
            @Nullable SimulacrumData simulacrum,
            int magicCost
    );

    public void onFail(
            @NotNull LivingEntity caster,
            int magicCost
    ) {}

    // =========================
    // STATIC DISPATCH API
    // =========================



    // =========================
    // INTERNAL RESOLUTION
    // =========================

    @Nullable
    protected static AbstractPowerSource getActivePowerSource(
            @NotNull Entity caster
    ) {
        return ActivePowerSourceAttachment.getActivePowerSource(caster);
    }

    public abstract void deactivate();

    public abstract <T extends StatCollectEvent> void contributeTo(T event);

}
