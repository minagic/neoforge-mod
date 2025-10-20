package com.minagic.minagic.spells;

import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class Fireball implements ISpell{
    private final int cooldownTicks = 20 * 15; // 1 second cooldown

    public int getCooldownTicks() {
        return cooldownTicks;
    }

    @Override
    public boolean cast(SpellCastContext context) {
        Level level = context.level;
        Player player = context.caster;

        if (level.isClientSide()) return false; // Only cast on server side

        Vec3 look = player.getLookAngle();
        Vec3 spawnPos = player.getEyePosition().add(look.scale(0.5)); // start just in front of face

        FireballEntity fireball = new FireballEntity(level, spawnPos, look);
        fireball.setOwner(player);
        level.addFreshEntity(fireball);


        // Optional: play sound or trigger animation
        level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
        return true;
    }
}
