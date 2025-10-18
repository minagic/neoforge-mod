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
        LivingEntity targetEntity, // Optional, useful for effects, resistances
        Entity directEntity,
        float baseAmount, // Before modifiers, use for "base spell damage"
        Set<ResourceKey<DamageType>> tags // our custom resource tags

) {
    // BOGUS
    private static final Map<ResourceKey<DamageType>, Float> DAMAGE_MODIFIERS = Map.ofEntries(
            Map.entry(DamageTypes.MAGIC, 1.0f),
            Map.entry(DamageTypes.ELEMENTAL, 2.0f),
            Map.entry(DamageTypes.DOT, 3.0f),
            Map.entry(DamageTypes.FIRE, 4.0f),
            Map.entry(DamageTypes.NATURAL, 5.0f),
            Map.entry(DamageTypes.POISON, 6.0f),
            Map.entry(DamageTypes.LIGHTNING, 7.0f),
            Map.entry(DamageTypes.PHYSICAL, 8.0f),
            Map.entry(DamageTypes.ARMOR_PIERCING, 0.0f),
            Map.entry(DamageTypes.INJURY, 10.0f),
            Map.entry(DamageTypes.PSYCHIC, 11.0f),
            Map.entry(DamageTypes.ETHEREAL, 12.0f),
            Map.entry(DamageTypes.EXECUTION, 13.0f),
            Map.entry(DamageTypes.UNBLOCKABLE, 14.0f)
    );

    public static float calculateDamage(MinagicDamage damage) {
        float total = damage.baseAmount;

        for (ResourceKey<DamageType> tag : damage.tags) {
            // HERE WE DEFINE MODIFIERS,
            // e.g. if damage.sourceEntity has some trinket for fire we increase damage
            // if current tag contains fire.
            total += DAMAGE_MODIFIERS.getOrDefault(tag, 0.0f);
        }

        return total;
    }

    public void hurt(ServerLevel level) {
        float finalDamage = calculateDamage(this);

        // THIS SHOULD BE REPLACED BY PRIORITIZATION
        ResourceKey<DamageType> primaryType = DamageTypes.MAGIC;

        Holder<DamageType> typeHolder = level.registryAccess()
                .holderOrThrow(primaryType);

        // 0.5f is base exhaustion,
        // in this example all NATURAL damage deals double the exhaustion
        // ARMOR_PIERCING is now embedded into DynamicDamageSource
        DamageSource source = new DynamicDamageSource(typeHolder, this.tags, 0.5f, directEntity, sourceEntity);

        this.targetEntity.hurt(source, finalDamage);
    }
}
