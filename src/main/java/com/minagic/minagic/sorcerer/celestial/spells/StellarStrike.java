package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.InstantaneousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.capabilities.powersource.SorceryPowerSourceAttachment;
import com.minagic.minagic.scaling.DefaultStats;
import com.minagic.minagic.scaling.SpellStatCollector;
import com.minagic.minagic.sorcerer.celestial.mechanics.StarShard;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import com.minagic.minagic.utilities.MathUtils;
import com.minagic.minagic.utilities.SpellUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static com.minagic.minagic.sorcerer.celestial.spells.CelestialBombardment.computeFiringSolution;

@AutoDetection.Spell
public class StellarStrike extends InstantaneousSpell implements SorceryPowerSourceAttachment.ISorcerySpell {

    public StellarStrike() {
        super(defaultProperties().withCooldown(10).withIdName("stellar_strike").withSpellName("Stellar Strike").withManaCost(20));

    }

    @Override
    public void cast(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
        SpellStatCollector collector = new SpellStatCollector();
        collector.injectContext(this, context, simulacrumData);
        collector.beginCollection();
        net.minecraft.world.entity.LivingEntity caster = context.caster;
        BlockPos targetedBlock = SpellUtils.getTargetBlockPos(caster, 32);
        if (targetedBlock == null) {
            return;
        }
        double baseAltitude = SpellUtils.findSurfaceY(context.level(), context.target.position().x, context.target.position().z);
        int altitude = (int) baseAltitude + context.level().random.nextInt(-3, 3) + 50;

        Vec3[] pos_dir = computeFiringSolution(context.caster.position(), MathUtils.blockPosToVec3(targetedBlock), MathUtils.blockPosToVec3(targetedBlock), altitude, 35);
        StarShard shard = new StarShard(context.level(), pos_dir[0], pos_dir[1], collector.getStat(DefaultStats.Spell.AOE_RADIUS).intValue());
        Minagic.LOGGER.debug("Celestial Bombardment spawning StarShard at {}", Arrays.toString(pos_dir));
        shard.setOwner(context.caster);
        context.level().addFreshEntity(shard);
    }

    @Override
    public String getRequiredBloodline() {
        return SorceryPowerSourceAttachment.BLOODLINE_CELESTIAL;
    }

    @Override
    public int getRequiredAffinityLevel() {
        return 5;
    }
}
