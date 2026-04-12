package com.minagic.minagic.sorcerer.celestial.mechanics;

import com.minagic.minagic.DamageTypes;
import com.minagic.minagic.Minagic;
import com.minagic.minagic.baseProjectiles.SpellProjectileEntity;
import com.minagic.minagic.rendering.DefaultProjectilePortalRenderers;
import com.minagic.minagic.spells.AOEHit;
import com.minagic.minagic.utilities.ProjectilePortal;
import com.minagic.minagic.utilities.VisualUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class StarShard extends SpellProjectileEntity implements ItemSupplier, ProjectilePortal.IPortalableProjectile {
    public StarShard(EntityType<? extends StarShard> type, Level level) {
        super(type, level);
        this.speed = 0f;
    }

    public StarShard(Level level, Vec3 position, Vec3 direction) {
        super(Minagic.STAR_SHARD.get(), level);

        this.speed = 1;
        this.direction = direction;
        this.isEntityPiercing = false;
        this.setPos(position.x, position.y, position.z);

        createPhysicsIfNull();

    }

    public StarShard(Level level, PhysicsData physics) {
        super(Minagic.STAR_SHARD.get(), level);
        this.physics = physics;
    }

    @Override
    public void onHitBlock(@NotNull BlockHitResult result) {
        if (this.level().isClientSide()) return;
        VisualUtils.createParticlesInSphere((ServerLevel) this.level(), this.position(), 4, ParticleTypes.END_ROD, 40);
        AOEHit.applyAOE(
                this.getOwner(),
                this,
                Set.of(DamageTypes.RADIANT),
                12,
                4,
                result.getBlockPos()
        );
        this.discard();
    }


    @Override
    public @NotNull ItemStack getItem() {
        return new ItemStack(Items.PRISMARINE_CRYSTALS);
    }

    @Override
    public String rendererID() {
        return new DefaultProjectilePortalRenderers.StarShardPortalRenderer().getId();
    }
}
