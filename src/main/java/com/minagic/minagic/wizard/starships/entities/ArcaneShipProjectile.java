package com.minagic.minagic.wizard.starships.entities;

import com.minagic.minagic.DamageTypes;
import com.minagic.minagic.Minagic;
import com.minagic.minagic.MinagicDamage;
import com.minagic.minagic.baseProjectiles.SpellProjectileEntity;
import com.minagic.minagic.utilities.SpellUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Set;
import java.util.UUID;

public abstract class ArcaneShipProjectile extends SpellProjectileEntity {
    public Set<ResourceKey<DamageType>> tags;
    public int baseDmg;
    public UUID sourceUUID;
    public UUID shipUUID;


    public ArcaneShipProjectile(EntityType<? extends ArcaneShipProjectile> type, Level level) {
        super(type, level);
    }

    public abstract ArcaneShipProjectile create(Level level,Vec3 pos, Vec3 dir, UUID sourceUUID, UUID shipUUID);
    @Override
    public void hitEntity(EntityHitResult hitResult){
        if (this.level().isClientSide()) return;
        MinagicDamage damage = new MinagicDamage(
                (Entity) SpellUtils.resolveLivingEntityAcrossDimensions(sourceUUID, level().getServer()),
                (LivingEntity) hitResult.getEntity(),
                SpellUtils.resolveLivingEntityAcrossDimensions(shipUUID, level().getServer()),
                baseDmg, tags);
        damage.hurt((ServerLevel) this.level());
    }
}
