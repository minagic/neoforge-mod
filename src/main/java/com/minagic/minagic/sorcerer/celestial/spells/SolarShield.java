package com.minagic.minagic.sorcerer.celestial.spells;

import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.capabilities.PlayerClassEnum;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.events.DefaultEventInterface;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGateChain;
import com.minagic.minagic.testing.spells.AutonomousChargedDevSpell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SolarShield extends AutonomousSpell implements DefaultEventInterface.ILivingDamageSpellHandler {

    public SolarShield(){
        this.cooldown = 3;
        this.simulacraMaxLifetime = 5;
        this.simulacraThreshold = 0;
        this.spellName = "Solar Shield";
    }

    @Override
    public void start (SpellCastContext context, @Nullable SimulacrumData simulacrumData){
        new SpellGateChain().addGate(new DefaultGates.ManaGate(5, this))
                .setEffect(super::start)
                .execute(context, simulacrumData);
    }

    public void onLivingDamage(LivingDamageEvent.Post event, SpellCastContext ctx, @Nullable SimulacrumData data) {
        SpellGateChain chain = new SpellGateChain()
                .addGate(new DefaultGates.SimulacrumGate())
                .addGate(new DefaultGates.ManaSustainGate(0))
                .setEffect((context, simData) ->
                {

                    ((ServerLevel) context.level()).sendParticles(
                            ParticleTypes.SOUL_FIRE_FLAME,
                            context.caster.getX(), context.caster.getY() + 1.0, context.caster.getZ(),
                            4, 0.2, 0.3, 0.2, 0.01
                    );
                });
        chain.execute(ctx,data);


    }

    @Override
    public List<DefaultGates.ClassGate.AllowedClass> getAllowedClasses(){
        return List.of(new DefaultGates.ClassGate.AllowedClass(PlayerClassEnum.SORCERER, PlayerSubClassEnum.SORCERER_CELESTIAL, 2));
    }
}
