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


    public static float calculateDamage(MinagicDamage damage) {
        return damage.baseAmount;
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
