package com.minagic.minagic.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.abstractionLayer.Spell;
import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class Fireball extends Spell {
    private final int cooldownTicks = 20 * 2; // 1 second cooldown
    private final int manaCost = 30;

    @Override
    public boolean canPlayerClassCastSpell(PlayerClass playerClass){
        // only infernal sorcerers and elemental wizards of level 3 and above can cast fireball
        return playerClass.getSubclassLevel(PlayerSubClassEnum.SORCERER_INFERNAL) >= 3 ||
               playerClass.getSubclassLevel(PlayerSubClassEnum.WIZARD_ELEMANCY) >= 3;
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
    public boolean cast(SpellCastContext context) {
        Level level = context.level;
        ServerPlayer player = context.caster;

        if (level.isClientSide()) return false; // Only cast on server side

        // player class verification
        PlayerClass pc = player.getData(ModAttachments.PLAYER_CLASS);
        if (!canPlayerClassCastSpell(pc)) {
            player.sendSystemMessage(Component.literal("WTF is this spell? You can't cast it."));
            return false;
        }


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
