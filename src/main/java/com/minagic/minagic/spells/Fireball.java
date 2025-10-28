package com.minagic.minagic.spells;

import com.minagic.minagic.abstractionLayer.spells.Spell;
import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class Fireball extends Spell {
    private final int cooldownTicks = 20 * 2; // 1 second cooldown
    private final int manaCost = 30;

    @Override
    public String canCast(SpellCastContext context){
        PlayerClass playerClass = context.caster.getData(ModAttachments.PLAYER_CLASS);
        // only infernal sorcerers and elemental wizards of level 3 and above can cast fireball

        String error = "";
        if  (!(playerClass.getSubclassLevel(PlayerSubClassEnum.SORCERER_INFERNAL) >= 3 ||
                playerClass.getSubclassLevel(PlayerSubClassEnum.WIZARD_ELEMANCY) >= 3)) {
            error = "WTH is this spell? You can't cast it.";
        }
        return error;

    }

    @Override
    public int getCooldownTicks() {
        return cooldownTicks;
    }

    @Override
    public int getManaCost() {
        return manaCost;
    }

    @Override
    public String getString(){
        return "Fireball";
    }

    @Override
    public void cast(SpellCastContext context) {
        ServerPlayer player = preCast(context);
        if (player == null) {
            return; // Pre-cast checks failed
        }

        Level level = context.level;


        Vec3 look = player.getLookAngle();
        Vec3 spawnPos = player.getEyePosition().add(look.scale(0.5)); // start just in front of face

        FireballEntity fireball = new FireballEntity(level, spawnPos, look);
        fireball.setOwner(player);
        level.addFreshEntity(fireball);


        // Optional: play sound or trigger animation
        level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

        applyMagicCosts(context);
    }
}
