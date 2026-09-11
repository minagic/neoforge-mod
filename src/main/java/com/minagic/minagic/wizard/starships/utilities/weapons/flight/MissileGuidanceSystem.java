package com.minagic.minagic.wizard.starships.utilities.weapons.flight;

import com.minagic.minagic.baseProjectiles.SpellProjectileEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class MissileGuidanceSystem implements IGuidingSystem {

    @Override
    public Vec3 correctCourse(SpellProjectileEntity projectile, Vector3f targetPosition) {
        if (targetPosition.lengthSquared()<1e-8) return projectile.physics.direction();
        Vec3 target = new Vec3(targetPosition);
        Vec3 desiredDirection = target.subtract(projectile.position());
        Vec3 current = projectile.physics.direction();
        return current.lerp(desiredDirection.normalize().scale(current.length()), 0.7);
    }
}
