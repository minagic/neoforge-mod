package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.api.spells.InstanteneousSpell;
import com.minagic.minagic.api.spells.SpellEventPhase;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGatePolicyGenerator;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class VoidBlast extends InstanteneousSpell {
    public VoidBlast() {
        super();

        this.spellName = "VoidBlast";
        this.cooldown = 20 * 3; // 60 ticks
        this.manaCost = 30;
        // simulacrum values untouched
    }

    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses() {
        return List.of(new DefaultGates.ClassGate.AllowedClass(
                PlayerClassEnum.SORCERER,
                PlayerSubClassEnum.SORCERER_VOIDBOURNE,
                3
        ));
    }

    @Override
    public void cast(SpellCastContext ctx, SimulacrumData simData) {
        SpellGatePolicyGenerator.build(SpellEventPhase.CAST, this.getAllowedClasses(), null, manaCost, null, false, this)
                .setEffect((context, simulacrumData) -> {
                    LivingEntity player = context.caster;
                    Level level = context.level();

                    Vec3 look = player.getLookAngle();
                    Vec3 spawnPos = player.getEyePosition().add(look.scale(0.5)); // start just in front of face

                    VoidBlastEntity voidBlast = new VoidBlastEntity(level, spawnPos, look);
                    voidBlast.setOwner(player);
                    level.addFreshEntity(voidBlast);
                })
                .execute(ctx, simData);

    }
}
