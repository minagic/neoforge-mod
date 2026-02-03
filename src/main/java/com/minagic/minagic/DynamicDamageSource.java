package com.minagic.minagic;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;

public class DynamicDamageSource extends DamageSource {
    private final Set<ResourceKey<DamageType>> tags;
    private final float baseExhaustion;

    public DynamicDamageSource(Holder<DamageType> dummyType,
                               Set<ResourceKey<DamageType>> tags,
                               float exhaustion,
                               Entity causing,
                               @Nullable Entity directSource
    ) {
        super(dummyType, directSource, causing);
        this.tags = tags;
        this.baseExhaustion = exhaustion;
    }


    @Override
    public float getFoodExhaustion() {
        if (tags.contains(DamageTypes.NATURAL)) {
            return baseExhaustion * 2.0f;
        }
        return baseExhaustion;
    }

    @Override
    public boolean is(@NotNull TagKey<DamageType> tag) {
        if (tag == DamageTypeTags.BYPASSES_ARMOR) {
            // custom logic, e.g. check your own tag set
            return this.tags.contains(DamageTypes.ARMOR_PIERCING);
        }

        // fallback to default logic
        return super.is(tag);
    }

    @Override
    public boolean is(@NotNull ResourceKey<DamageType> key) {
        return this.tags.contains(key);
    }


}