package com.minagic.minagic.spells;

import com.minagic.minagic.api.spells.InstantaneousSpell;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@AutoDetection.Spell
public class Fireball extends InstantaneousSpell {

    public Fireball() {
        super(InstantaneousSpell.defaultProperties()
                .withSpellName("Fireball")
                .withIdName("fireball")
                .withCooldown(40)
                .withManaCost(30));
    }

    @Override
    public void cast(SpellCastContext ctx, SimulacrumData simData) {
        Level level = ctx.level();
        LivingEntity player = ctx.caster;


        Vec3 look = player.getLookAngle();
        Vec3 spawnPos = player.getEyePosition().add(look.scale(0.5)); // start just in front of face

        FireballEntity fireball = new FireballEntity(level, spawnPos, look);
        fireball.setOwner(player);
        level.addFreshEntity(fireball);


        // Optional: play sound or trigger animation
        level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
