package com.minagic.minagic.baseProjectiles;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class ArcSpellProjectileEntity extends SpellProjectileEntity {
    protected double gravity;

    public ArcSpellProjectileEntity(EntityType<? extends ArcSpellProjectileEntity> type, Level level, double speed, Vec3 direction, double gravity) {
        super(type, level, speed, direction);
        this.setNoGravity(false); // Enable gravity
        this.gravity = gravity;
    }
    @Override
    public void tick() {
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.add(0, -gravity, 0)); // Gravity

        super.tick();
    }
}
