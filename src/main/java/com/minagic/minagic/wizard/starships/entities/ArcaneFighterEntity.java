package com.minagic.minagic.wizard.starships.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class ArcaneFighterEntity extends ArcaneShipEntity{
    public ArcaneFighterEntity(EntityType<? extends ArcaneFighterEntity> entity, Level level) {
        super(entity, level);
    }


    public static AttributeSupplier.Builder createAttributes() {

        return LivingEntity.createLivingAttributes()

                .add(Attributes.MAX_HEALTH, 100.0D)

                .add(Attributes.MOVEMENT_SPEED, 0.0D)

                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);

    }


}
