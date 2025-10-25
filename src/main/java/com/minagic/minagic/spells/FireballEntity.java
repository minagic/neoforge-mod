package com.minagic.minagic.spells;

import com.minagic.minagic.DamageTypes;
import com.minagic.minagic.Minagic;
import com.minagic.minagic.baseProjectiles.SpellProjectileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class FireballEntity extends SpellProjectileEntity implements ItemSupplier {
    private static final float SPEED = 2f;
    private static final float RADIUS = 4f;
    private static final float GRAVITY = -0.01f;

    // Generic constructor
    public FireballEntity(EntityType<? extends FireballEntity> type, Level level) {
        super(type, level, SPEED, Vec3.ZERO, GRAVITY);
    }

    // Custom constructor
    public FireballEntity(Level level, Vec3 position, Vec3 direction) {
        super( Minagic.FIREBALL.get(), level, SPEED, direction, GRAVITY);
        this.setPos(position.x, position.y, position.z);
        this.setDeltaMovement(direction.normalize().scale(SPEED));
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);

        BlockPos center = hitResult.getEntity().blockPosition();
        AOEHit.applyAOE(this.getOwner(),  this, Set.of(DamageTypes.MAGIC, DamageTypes.FIRE, DamageTypes.ELEMENTAL), 10, RADIUS, center);
        spawnAOESparkParticles(level(), hitResult.getLocation(), RADIUS);

        this.discard();
    }

    public static void spawnAOESparkParticles(Level level, Vec3 center, double radius) {
        if (level.isClientSide()) return;

        BlockPos origin = BlockPos.containing(center);
        int r = (int) Math.ceil(radius);

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    if (center.distanceTo(Vec3.atCenterOf(pos)) <= radius) {
                        spawnSparkParticleAt(level, Vec3.atCenterOf(pos));
                    }
                }
            }
        }
    }

    private static void spawnSparkParticleAt(Level level, Vec3 pos) {
        // Random small offset so sparks look natural
        RandomSource rand = level.getRandom();
        double offsetX = rand.nextGaussian() * 0.05;
        double offsetY = rand.nextGaussian() * 0.05;
        double offsetZ = rand.nextGaussian() * 0.05;

        ((ServerLevel) level).sendParticles(
                ParticleTypes.FLAME, // 🔥 You can create your own spark particle later
                pos.x, pos.y, pos.z,
                3, // Count
                offsetX, offsetY, offsetZ,
                0.01 // Speed
        );
    }


    @Override
    public net.minecraft.world.item.ItemStack getItem() {
        return new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.FIRE_CHARGE);
    }


}