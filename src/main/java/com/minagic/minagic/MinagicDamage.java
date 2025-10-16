package com.minagic.minagic;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.Set;

public record MinagicDamage(
        Entity sourceEntity, // Caster or attacker
        LivingEntity targetEntity, // Optional, useful for effects
        float baseAmount, // Before modifiers
        Set<ResourceKey<DamageType>> tags // e.g. FIRE, MAGIC, DOT
) {
    private static final Map<ResourceKey<DamageType>, Float> DAMAGE_MODIFIERS = Map.ofEntries(
            Map.entry(DamageTypes.MAGIC, 1.0f),
            Map.entry(DamageTypes.ELEMENTAL, 2.0f),
            Map.entry(DamageTypes.DOT, 3.0f),
            Map.entry(DamageTypes.FIRE, 4.0f),
            Map.entry(DamageTypes.NATURAL, 5.0f),
            Map.entry(DamageTypes.POISON, 6.0f),
            Map.entry(DamageTypes.LIGHTNING, 7.0f),
            Map.entry(DamageTypes.PHYSICAL, 8.0f),
            Map.entry(DamageTypes.ARMOR_PIERCING, 9.0f),
            Map.entry(DamageTypes.INJURY, 10.0f),
            Map.entry(DamageTypes.PSYCHIC, 11.0f),
            Map.entry(DamageTypes.ETHEREAL, 12.0f),
            Map.entry(DamageTypes.EXECUTION, 13.0f),
            Map.entry(DamageTypes.UNBLOCKABLE, 14.0f)
    );

    public static float calculateDamage(MinagicDamage damage) {
        float total = damage.baseAmount;

        for (ResourceKey<DamageType> tag : damage.tags) {
            // HERE WE DEFINE MODIFIERS, e.g. if damage.sourceEntity has some trinket for fire we increase damage if current tag contains fire.
            total += DAMAGE_MODIFIERS.getOrDefault(tag, 0.0f);
        }

        return total;
    }

    public void hurt(ServerLevel level) {
        // 1. Calculate final damage amount
        float finalDamage = calculateDamage(this);

        // 2. Resolve primary damage type (optional tiered priority)
        ResourceKey<DamageType> primaryType = DamageTypes.MAGIC;

        // 3. Get the Holder<DamageType> from registry
        Holder<DamageType> typeHolder = level.registryAccess()
                .holderOrThrow(primaryType);


        // 4. Construct the DamageSource — includes source entity if available
        DamageSource source = new DamageSource(typeHolder);

        // 5. Apply the damage
        this.targetEntity.hurt(source, finalDamage);
    }
}
