package com.minagic.minagic.testing.spells;


import com.minagic.minagic.Config;
import com.minagic.minagic.abstractionLayer.spells.ChanneledAutonomousSpell;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class ChanneledAutonomousDevSpell extends ChanneledAutonomousSpell {

    @Override
    public void cast(SpellCastContext context) {

        System.out.println("[ChanneledAutonomousDevSpell] 🔫 Pew Pew Pew! lifetime="
                + context.simulacrtumLifetime + " Player=" + context.caster.getName().getString());

    }

    public ChanneledAutonomousDevSpell() {
        super();

        this.spellName = "ChanneledAutonomousDevSpell";
        this.manaCost = 2;

        this.simulacraThreshold = 5;       // once every 0.5 seconds
        this.simulacraMaxLifetime = -1;    // channeled = infinite lifetime
    }
    @Override
    public CastFailureReason canCast(SpellCastContext context) {
        return Config.ENABLE_DEV_SPELLS.get() ? CastFailureReason.OK : CastFailureReason.CASTER_CLASS_MISMATCH;
    }
}
