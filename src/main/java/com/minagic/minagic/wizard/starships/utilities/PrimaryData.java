package com.minagic.minagic.wizard.starships.utilities;

import com.minagic.minagic.DamageTypes;
import com.minagic.minagic.MinagicDamage;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipProjectile;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;

public record PrimaryData(List<Vec3> positions, List<Vec3> directions, int cooldown, int fuelcost, ArcaneShipProjectile projectile) {
}
