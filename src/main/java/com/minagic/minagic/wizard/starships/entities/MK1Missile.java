package com.minagic.minagic.wizard.starships.entities;

import com.minagic.minagic.DamageTypes;
import com.minagic.minagic.Minagic;
import com.minagic.minagic.common.registry.ModEntityTypes;
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
        implements ItemSupplier {

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
            UUID shipUUID
    ) {
        MK1Missile missile = new MK1Missile(
                ModEntityTypes.MK1_MISSILE.get(),
                level
        );

        missile.baseDmg = 20;
        missile.shipUUID = shipUUID;
        missile.sourceUUID = sourceUUID;
        missile.direction = dir.normalize();
        missile.speed = 2.5;
        missile.gravity = 0.0;
        missile.setPos(pos);
        missile.computer = new ArcaneShipMissileComputer();
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
        if (this.level().isClientSide()) return;
        Minagic.LOGGER.info("Logging computer: {}", this.computer);
        if (!(this.computer instanceof ArcaneShipMissileComputer missileComputer)) {

            return;

        }
        if (!computer.stillLockedOn(this) &&
                (missileComputer.getShip() != null
                        && this.distanceTo(missileComputer.getShip())>5
                )
        || missileComputer.getShip() == null )
        {

            createMissileExplosion();
            this.discard();
        }
    }


    @Override
    public void hitEntity(EntityHitResult hitResult) {
        createMissileExplosion();

        super.hitEntity(hitResult);
    }

    @Override
    protected void hitBlock(BlockHitResult hitResult) {
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