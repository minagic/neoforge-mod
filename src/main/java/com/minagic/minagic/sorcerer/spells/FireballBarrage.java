package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.abstractionLayer.AutonomousSpell;
import com.minagic.minagic.abstractionLayer.Spell;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spells.FireballEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * A powerful spell that rapidly fires fireballs forward.
 * Intended for Celestial / Pyromancer classes.
 */
public class FireballBarrage extends AutonomousSpell {

    @Override
    public String canCast(SpellCastContext context) {
        if (!(context.caster.getData(ModAttachments.PLAYER_CLASS.get()).getSubclassLevel(PlayerSubClassEnum.SORCERER_INFERNAL)>=10)) {
            return "You must be an Infernal Sorcerer of level 10 or higher to cast Fireball Barrage.";
        }
        return "";
    }

    @Override
    public void cast(SpellCastContext context) {
        ServerPlayer player = preCast(context);
        if (player == null) {
            return;
        }

        Level level = context.level;

        Vec3 look = player.getLookAngle();
        Vec3 spawnPos = player.getEyePosition().add(look.scale(0.5)); // spawn slightly in front of the player

        FireballEntity fireball = new FireballEntity(level, spawnPos, look);
        fireball.setOwner(player);
        level.addFreshEntity(fireball);

        // Play firing sound
        level.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

        // Apply mana cost & cooldown through your base class logic
        applyMagicCosts(context);
    }

    @Override
    public int getManaCost() {
        // Cost per shot; adjust for balance
        return 15;
    }

    @Override
    public int getCooldownTicks() {
        return 1;
    }

    @Override
    public String getString() {
        return "Fireball Barrage";
    }
}
