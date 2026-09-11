package com.minagic.minagic.wizard.starships.utilities.weapons.flight;

import com.minagic.minagic.baseProjectiles.SpellProjectileEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class NoGuidanceSystem implements IGuidingSystem {

    @Override
    public Vec3 correctCourse(SpellProjectileEntity projectile, Vector3f targetPosition) {
        return projectile.physics.direction();
    }
}
