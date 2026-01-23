package com.minagic.minagic.spells;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.MinagicDamage;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.ObjectUtils.Null;

public class AOEHit {

    /**
     * Deals area-of-effect damage and triggers additional logic for each affected entity.
     *
     * @param source     The entity that caused the hit (e.g. projectile owner)
     * @param tags       A set of DamageType keys to apply
     * @param baseDamage The base damage per target
     * @param radius     The radius around the source position
     */
    public static void applyAOE(Entity source,
                                Entity directSource,
                                Set<ResourceKey<DamageType>> tags,
                                float baseDamage,
                                float radius, BlockPos center) {
        if (source == null || source.level().isClientSide()) return;

        Level level = source.level();
        if (!(level instanceof ServerLevel serverLevel)) return;


        // Define the affected area
        AABB area = new AABB(
                center.getX() - radius, center.getY() - radius, center.getZ() - radius,
                center.getX() + radius, center.getY() + radius, center.getZ() + radius
        );


        // For each entity in range
        List<Entity> targets = serverLevel.getEntities(source, area, e -> e instanceof LivingEntity && e.isAlive());
        if (area.contains(source.position())) {
            targets.add(source);
        }
        for (Entity target : targets) {
            MinagicDamage damage = new MinagicDamage(source, target.asLivingEntity(), directSource, baseDamage, tags);
            damage.hurt(serverLevel);
        }
    }
}