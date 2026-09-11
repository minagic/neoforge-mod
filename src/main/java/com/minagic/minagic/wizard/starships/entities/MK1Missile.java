package com.minagic.minagic.wizard.starships.entities;

import com.minagic.minagic.DamageTypes;
import com.minagic.minagic.common.registry.ModEntityTypes;
import com.minagic.minagic.utilities.SpellUtils;
import com.minagic.minagic.wizard.starships.utilities.weapons.flight.MissileGuidanceSystem;
import com.minagic.minagic.wizard.starships.utilities.weapons.targeting.TargetingComputer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Set;
import java.util.UUID;

public class MK1Missile
        extends ArcaneShipProjectile
        implements ItemSupplier
{

    ArcaneShipEntity cachedOwner = null;

    private static final float EXPLOSION_POWER = 3.0F;

    public MK1Missile(
            EntityType<? extends ArcaneShipProjectile> type,
            Level level
    ) {
        super(type, level);
    }

    @Override
    public ArcaneShipProjectile create(
            Level level,
            Vec3 pos,
            Vec3 dir,
            UUID sourceUUID,
            UUID shipUUID,
            TargetingComputer computer
    ) {
        MK1Missile missile = new MK1Missile(
                ModEntityTypes.MK1_MISSILE.get(),
                level
        );

        missile.baseDmg = 20;
        missile.shipUUID = shipUUID;
        missile.sourceUUID = sourceUUID;
        missile.direction = dir.normalize();
        missile.speed = 2.5+level.getEntityInAnyDimension(shipUUID).getDeltaMovement().length();
        missile.gravity = 0.0;
        missile.setPos(pos);
        missile.computer = computer;
        missile.guidingSystem = new MissileGuidanceSystem();
        missile.tags = Set.of(
                com.minagic.minagic.DamageTypes.MAGIC,
                DamageTypes.INJURY,
                com.minagic.minagic.DamageTypes.ETHEREAL
        );

        level.addFreshEntity(missile);

        return missile;
    }

    @Override
    public void tick(){

        super.tick();

    }

    public void attemptResolve(){
        if(level().isClientSide())return;
        if (cachedOwner != null){
            if (cachedOwner.isAlive()) return;
            cachedOwner = null;
            return;
        }
        Entity candidate = SpellUtils.resolveLivingEntityAcrossDimensions(shipUUID, this.level().getServer());
        if (candidate instanceof ArcaneShipEntity ship && ship.isAlive()){
            cachedOwner = ship;
        }
    }


    @Override
    public void hitEntity(EntityHitResult hitResult) {
        if (
                hitResult.getEntity().getUUID() == this.shipUUID ||
                hitResult.getEntity().getUUID() == this.sourceUUID ||
                (hitResult.getEntity() instanceof ArcaneShipProjectile projectile && projectile.shipUUID == this.shipUUID)) return;
        attemptResolve();
        if (cachedOwner != null && cachedOwner.isAlive() && this.distanceTo(cachedOwner) < 6) return; // arming distance
        createMissileExplosion();

        super.hitEntity(hitResult);
    }

    @Override
    protected void hitBlock(BlockHitResult hitResult) {
        if (cachedOwner != null && cachedOwner.isAlive() && this.distanceTo(cachedOwner) < 6) return; // arming distance
        createMissileExplosion();

        super.hitBlock(hitResult);
    }

    private void createMissileExplosion() {
        if (level().isClientSide()) {
            return;
        }

        level().explode(
                this,
                getX(),
                getY(),
                getZ(),
                EXPLOSION_POWER,
                Level.ExplosionInteraction.TNT
        );
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.WITHER_SKELETON_SKULL);
    }
}