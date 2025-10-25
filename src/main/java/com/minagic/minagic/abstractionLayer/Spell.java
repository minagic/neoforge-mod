package com.minagic.minagic.abstractionLayer;

import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.startup.Server;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class Spell {

    protected @Nullable ServerPlayer preCast(SpellCastContext context) {
        if (!(context.caster instanceof ServerPlayer player)) {
            return null; // NEVER EVER CAST ON THE CLIENT
        }

        if (context.level.isClientSide()){
            return null; // NEVER EVER CAST ON THE CLIENT
        }

        String error = canCast(context);
        if (!Objects.equals(error, "")) {
            player.sendSystemMessage(Component.literal(error));
            return null;
        }
        return player;
    }
    public boolean cast(SpellCastContext context) {
        ServerPlayer player = preCast(context);
        if (player == null) {
            return false; // Pre-cast checks failed
        }
        player.sendSystemMessage(Component.literal("No spell is bound to this slot."));
        return true;
    }

    public String getString() {
        return "No Spell";
    }

    public int getCooldownTicks() {
        return 0;
    }

    public int getManaCost() {
        return 0;
    }

    public String canCast(SpellCastContext context) {
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
