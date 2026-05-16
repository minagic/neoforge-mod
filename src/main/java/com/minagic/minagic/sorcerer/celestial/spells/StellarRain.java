package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.InstantaneousSpell;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.powersource.SorceryPowerSourceAttachment;
import com.minagic.minagic.sorcerer.celestial.mechanics.StarShard;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.utilities.ProjectilePortal;
import com.minagic.minagic.utilities.SpellUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

@AutoDetection.Spell
public class StellarRain extends InstantaneousSpell implements SorceryPowerSourceAttachment.ISorcerySpell {

    public StellarRain() {
        super(InstantaneousSpell.defaultProperties()
                .withSpellName("Stellar Rain")
                .withIdName("stellar_rain")
                .withCooldown(0)
                .withManaCost(40));
    }

    @Override
    public void cast(SpellCastContext context, @org.jetbrains.annotations.Nullable SimulacrumData simulacrumData) {
        Minagic.LOGGER.info("Initating Stellar Rain");
        LivingEntity caster = context.caster;
        BlockPos targetedBlock = SpellUtils.getTargetBlockPos(caster, 32);
        if (targetedBlock == null) {
            return;
        }
        ArrayList<BlockPos> targets = new ArrayList<>();
        RandomSource random = context.level().random;
        int targetCount = 10;
        int XZRange = 5;
        for (int i = 0; i < targetCount; i++) {
            int XOffset = random.nextInt(-XZRange, XZRange);
            int ZOffset = random.nextInt(-XZRange, XZRange);

            BlockPos currentBlockPos = new BlockPos(targetedBlock.getX() + XOffset, 0, targetedBlock.getZ() + ZOffset);
            currentBlockPos = new BlockPos(currentBlockPos.getX(),
                    (int) SpellUtils.findSurfaceY(context.level(), currentBlockPos.getX(), currentBlockPos.getZ()),
                    currentBlockPos.getZ());
            targets.add(currentBlockPos);
            Minagic.LOGGER.debug("Stellar Rain locked target {}", currentBlockPos);

        }

        for (BlockPos target : targets){
            Vec3 pos = new Vec3(target.getX(), target.getY()+100, target.getZ());
            StarShard shard = new StarShard(context.level(), pos, new Vec3(0, -1, 0), 4);
            ProjectilePortal portal = new ProjectilePortal(context.level(), shard, 120);
            context.level().addFreshEntity(portal);

        }

    }

    @Override
    public String getRequiredBloodline() {
        return SorceryPowerSourceAttachment.BLOODLINE_CELESTIAL;
    }

    @Override
    public int getRequiredAffinityLevel() {
        return 17;
    }

}
