package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.abstractionLayer.spells.InstanteneousSpell;
import com.minagic.minagic.abstractionLayer.spells.Spell;
import com.minagic.minagic.capabilities.PlayerSubClassEnum;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VoidBlast extends InstanteneousSpell {
    @Override
    public String canCast(SpellCastContext context){
        if (!(context.caster.getData(ModAttachments.PLAYER_CLASS)
                .getSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE) >= 3)) {
            return "WTH is this spell? You can't cast it.";
        }

        return "";
    }

    @Override
    public String getString(){
        return "Void Blast";
    }

    @Override
    public int getCooldownTicks() {
        return 20 * 3; // 3 seconds cooldown
    }

    @Override
    public int getManaCost() {
        return 30;
    }

    @Override
    public void cast(SpellCastContext context) {
        ServerPlayer player = preCast(context);
        if (player == null) {
            return;
        }

        Level level = context.level;


        Vec3 look = player.getLookAngle();
        Vec3 spawnPos = player.getEyePosition().add(look.scale(0.5)); // start just in front of face

        VoidBlastEntity voidBlast = new VoidBlastEntity(level, spawnPos, look);
        voidBlast.setOwner(player);
        level.addFreshEntity(voidBlast);
        applyMagicCosts(context);
    }
}
