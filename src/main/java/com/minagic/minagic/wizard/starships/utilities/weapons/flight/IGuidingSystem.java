package com.minagic.minagic.wizard.starships.utilities.weapons.flight;

import com.minagic.minagic.baseProjectiles.SpellProjectileEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public interface IGuidingSystem {
    Vec3 correctCourse(SpellProjectileEntity projectile, Vector3f targetPosition);
}
