package com.minagic.minagic.capabilities;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.spellCasting.spellslots.SimulacrumSpellSlot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record SimulacrumSpellData(
        ResourceLocation spellId,
        float remainingLifetime,
        float maxLifetime,
        float lifetime,
        float threshold
) {

    // ===========
    //  CODEC
    // ===========
    public static final Codec<SimulacrumSpellData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("spell_id").forGetter(SimulacrumSpellData::spellId),
                    Codec.FLOAT.fieldOf("remaining_lifetime").forGetter(SimulacrumSpellData::remainingLifetime),
                    Codec.FLOAT.fieldOf("max_lifetime").forGetter(SimulacrumSpellData::maxLifetime),
                    Codec.FLOAT.fieldOf("lifetime").forGetter(SimulacrumSpellData::lifetime),
                    Codec.FLOAT.fieldOf("threshold").forGetter(SimulacrumSpellData::threshold)
            ).apply(instance, SimulacrumSpellData::new)
    );


    // ===========
    // Derived fields
    // ===========
    public float progress() {
        var spell = ModSpells.get(spellId); // however you get it
        if (spell == null) return 0xFFFFFF;
        return spell.progress(this);
    }

    public int color(float progress) {
        var spell = ModSpells.get(spellId); // however you get it
        if (spell == null) return 0x00000000;
        return spell.color(progress);
    }
}