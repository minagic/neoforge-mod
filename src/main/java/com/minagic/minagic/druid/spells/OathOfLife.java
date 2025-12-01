package com.minagic.minagic.druid.spells;

import com.minagic.minagic.DamageTypes;
import com.minagic.minagic.MinagicDamage;
import com.minagic.minagic.api.spells.AutonomousSpell;
import com.minagic.minagic.capabilities.Mana;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerLevel;

import java.util.Set;

public class OathOfLife extends AutonomousSpell {
    public OathOfLife() {
        this.manaCost = 8;
        this.cooldown = 0;
        this.spellName = "Oath of Life";
        this.simulacraThreshold = 5;
        this.simulacraMaxLifetime = 20; // 1 second (20 ticks)
        this.isTechnical = true;
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
