package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.api.spells.ChargedSpell;
import com.minagic.minagic.api.spells.SpellValidator;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.PowerCalibrator;
import com.minagic.minagic.utilities.SpellValidationResult;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TorchPlacement extends ChargedSpell {

    public TorchPlacement() {
        super();
        this.cooldown = 300;
        this.manaCost = 0;
        this.simulacraMaxLifetime = 300;
        this.spellName = "Torch Placer";
    }

    @Override
    public void cast(SpellCastContext context, SimulacrumData data) {

        float originalCharge = data.lifetime() / data.maxLifetime();

        PowerCalibrator calibrator = PowerCalibrator.of(PowerCalibrator.SinusoidalCurve.INSTANCE)
                .flatlineAbove(0.8f)
                .flatlineBelow(0.2f);

        float torchCount = (int) calibrator
                .remap(0f, 60f)
                .apply(originalCharge);

        System.out.println("Torch count: " + torchCount);

        int manaCost = (int )calibrator.remap(0f, 60).apply(originalCharge);

        SpellValidationResult manaConfirmation = SpellValidator.validateMana(this, context, manaCost);
        SpellValidator.showFailureIfNeeded(context, manaConfirmation);
        if (!manaConfirmation.success()) return;


        BlockPos origin = context.target.blockPosition();
        ServerLevel level = (ServerLevel) context.level();

        BlockPos.MutableBlockPos search = origin.mutable();
        while (search.getY() > level.getMinY() && level.isEmptyBlock(search)) {
            search.move(0, -1, 0);
        }
        BlockPos center = search.above();
        System.out.println("center: " + center);
        int placed = 0;
        int torchLimit = (int) torchCount;

        int verticalRange = 5;     // How many Y levels to search

        Set<BlockPos> placedPositions = new HashSet<>();

        float minSpacing = 4.0f;
        float maxSpacing = 8.0f;

        float baseSpacing = calibrator.remap(minSpacing, maxSpacing).apply(originalCharge);
        int radius = 0;
        while (placed < torchLimit) {
            radius++;
            for (int dx = -radius; dx <= radius && placed < torchLimit; dx++) {
                for (int dz = -radius; dz <= radius && placed < torchLimit; dz++) {
                    for (int dy = -verticalRange; dy <= verticalRange && placed < torchLimit; dy++) {


                        BlockPos floor = center.offset(dx, dy - 1, dz);
                        BlockPos air = center.offset(dx, dy, dz);

                        if (!level.getBlockState(floor).isSolid()) continue;
                        if (!level.isEmptyBlock(air)) continue;

                        if (level.getMaxLocalRawBrightness(air) > 8) continue;


                        float spacing = baseSpacing + (level.random.nextFloat() - 0.5f) * baseSpacing;

                        boolean tooClose = placedPositions.stream().anyMatch(pos -> pos.distManhattan(air) < spacing);
                        if (tooClose) continue;

                        // Place torch
                        level.setBlockAndUpdate(air, Blocks.TORCH.defaultBlockState());
                        placedPositions.add(air);
                        placed++;
                    }
                }
            }
        }


        drainMana(context, manaCost);

    }

    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses() {
        return List.of(new DefaultGates.ClassGate.AllowedClass(
                PlayerClassEnum.SORCERER,
                PlayerSubClassEnum.SORCERER_CELESTIAL,
                3
        ));
    }

}
