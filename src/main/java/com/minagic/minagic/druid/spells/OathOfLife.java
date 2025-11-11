package com.minagic.minagic.druid.spells;

import com.minagic.minagic.DamageTypes;
import com.minagic.minagic.MinagicDamage;
import com.minagic.minagic.abstractionLayer.spells.AutonomousSpell;
import com.minagic.minagic.abstractionLayer.spells.Spell;
import com.minagic.minagic.capabilities.Mana;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Animal;

import java.util.Set;

public class OathOfLife extends AutonomousSpell {
    @Override
    public int getMaxLifetime() {
        return 20; // lasts for 1 second (20 ticks)
    }

    @Override
    public int getManaCost() {
        return 8;
    }

    @Override
    public int getCooldownTicks() {
        return 0; // no cooldown
    }

    @Override
    public int getSimulacrumThreshold() {
        return 5; // can be cast by simulacra each 15 ticks
    }

    @Override
    public String getString() {
        return "Oath of Life";
    }

    @Override
    public void cast(SpellCastContext context) {
        // identify caster's mana percentage
        Mana mana = context.caster.getData(ModAttachments.MANA);
        float manaPercentage = mana.getMana() / (float) mana.getMaxMana();
        if (manaPercentage < 0.2f) {
            // low mana, DAMAGE instead
            MinagicDamage damage = new MinagicDamage(context.target, context.caster, context.target, 6.0f, Set.of(
                    DamageTypes.MAGIC,
                    DamageTypes.NATURAL
            ));
            damage.hurt((ServerLevel)context.level());
        }
        else {
            // heal target
            context.target.heal(6.0f);
        }
    }

}
