package com.minagic.minagic.sorcerer.spells;

import com.minagic.minagic.DamageTypes;
import com.minagic.minagic.Minagic;
import com.minagic.minagic.MinagicDamage;
import com.minagic.minagic.baseProjectiles.SpellProjectileEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.Set;

public class VoidBlastEntity extends SpellProjectileEntity implements ItemSupplier {
    public VoidBlastEntity(EntityType<? extends SpellProjectileEntity> type, Level level) {
        super(type, level);
    }

    public VoidBlastEntity(Level level, Vec3 Pos, Vec3 direction) {
        super(Minagic.VOID_BLAST_ENTITY.get(), level);
        this.speed = 0.5;
        this.gravity = 0.0;
        this.isBlockPiercing = false;
        this.isEntityPiercing = false;
        this.setPos(Pos.x, Pos.y, Pos.z);
        this.setDeltaMovement(direction.normalize().scale(this.speed));
    }

    @Override
    protected void onHitBlock(net.minecraft.world.phys.BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        this.discard();
    }

    @Override
    protected void onHitEntity(net.minecraft.world.phys.EntityHitResult hitResult) {
        super.onHitEntity(hitResult);

        Entity entity = hitResult.getEntity();

        if (!(hitResult.getEntity() instanceof net.minecraft.world.entity.LivingEntity livingEntity)) {
            this.discard();
            return;
        }

        Level level = Objects.requireNonNull(this.getOwner()).level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        // DAMAGE
        MinagicDamage damage = new MinagicDamage(this.getOwner(), livingEntity, this, 12, Set.of(DamageTypes.MAGIC, DamageTypes.VOID));
        livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 1));
        livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));
        damage.hurt(serverLevel);

    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(net.minecraft.world.item.Items.ENDER_PEARL);
    }
}
