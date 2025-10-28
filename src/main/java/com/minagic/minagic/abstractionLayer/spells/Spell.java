package com.minagic.minagic.abstractionLayer.spells;

import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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

        if (!Objects.equals(magicPrerequisitesHelper(context), "")) {
            player.sendSystemMessage(Component.literal(magicPrerequisitesHelper(context)));
            return null;
        }

        return player;
    }

    public ServerPlayer preCast(SpellCastContext context, boolean checkPrerequisites) {
        // Bypass prerequisite check
        if (checkPrerequisites) {
            return preCast(context);
        } else {
            Player player = context.caster;
            if (!(player instanceof ServerPlayer serverPlayer)){
                return null;
            }
            if (!context.level.isClientSide()) {
                String error = canCast(context);
                if(Objects.equals(error, "")) {
                    return serverPlayer;
                } else {
                    serverPlayer.sendSystemMessage(Component.literal(error));
                    return null;
                }
            } else {
                return null;
            }
        }
    }
    // CASTING
    public void onStart(SpellCastContext context) {
        cast(context);
    }

    public void onStop(SpellCastContext context) {
        // No-op by default
    }

    public void cast(SpellCastContext context) {
        ServerPlayer player = preCast(context);
        if (player == null) {
            return; // Pre-cast checks failed
        }

        player.sendSystemMessage(Component.literal("No spell is bound to this slot."));

        applyMagicCosts(context);
    }
    public String magicPrerequisitesHelper(SpellCastContext context) {

        // set cooldown
        var cooldowns = context.caster.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get());
        var mana = context.caster.getData(ModAttachments.MANA.get());

        if (mana.getMana() < getManaCost()) {
            return "Not enough mana to cast " + getString() + ".";
        }

        if (cooldowns.getCooldown(ModSpells.getId(this)) > 0) {
            return "Spell " + getString() + " is on cooldown.";
        }

        return "";

    }


    public void applyMagicCosts(SpellCastContext context) {
        // set cooldown
        var cooldowns = context.caster.getData(ModAttachments.PLAYER_SPELL_COOLDOWNS.get());
        var mana = context.caster.getData(ModAttachments.MANA.get());

        mana.drainMana(getManaCost());
        cooldowns.setCooldown(ModSpells.getId(this), getCooldownTicks());
    }

    public void spellChargeTick(SpellCastContext context) {
        // No-op by default
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

    public int getSimulacrumThreshold(){
        return getCooldownTicks();
    }

    public int getSimulacrumManaCost(){
        return getManaCost();
    }

    public int getMaxLifetime(){
        return -1; // Infinite lifetime by default
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
