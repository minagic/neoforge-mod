package com.minagic.minagic.capabilities;
import com.minagic.minagic.api.spells.ISimulacrumSpell;
import com.minagic.minagic.registries.ModSpells;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public record SimulacrumSpellData(
        ResourceLocation spellId,
        float remainingLifetime,
        float maxLifetime,
        float lifetime,
        float threshold,
        LivingEntity host
) {



    // ===========
    // Derived fields
    // ===========
    public float progress() {
        var spell = ModSpells.get(spellId); // however you get it
        if (spell == null) return 0f;
        if (!(spell instanceof ISimulacrumSpell simulacrumSpell)) return 0f;
        return simulacrumSpell.progress(this);
    }

    public int color(float progress) {
        var spell = ModSpells.get(spellId); // however you get it
        if (spell == null) return 0x00000000;
        return spell.color(progress);
    }

    public void dump(){
        System.out.println("Spell:  " + spellId);
        System.out.println("Remaining Lifetime: " +  remainingLifetime);
        System.out.println("Max Lifetime: " + maxLifetime);
        System.out.println("Lifetime: " + lifetime);
        System.out.println("Threshold: " + threshold);
    }

    public boolean validate(){
        if (host == null) return false;
        if (!host.isAlive()) return false;
        return !host.level().isClientSide();

    }
}