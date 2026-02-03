package com.minagic.minagic.sorcerer.celestial.spells.novaburst;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.utilities.MathUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class NovaImpactProxyEntity extends Monster implements ItemSupplier {

    // =========================
    // Synched data
    // =========================
    private static final EntityDataAccessor<Integer> LIFETIME =
            SynchedEntityData.defineId(NovaImpactProxyEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> RADIUS =
            SynchedEntityData.defineId(NovaImpactProxyEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<String> CASTER_UUID =
            SynchedEntityData.defineId(NovaImpactProxyEntity.class, EntityDataSerializers.STRING);

    // =========================
    // Constructors
    // =========================
    public NovaImpactProxyEntity(EntityType<? extends NovaImpactProxyEntity> type, Level level) {
        super(type, level);
        this.setNoAi(true);

    }

    public NovaImpactProxyEntity(Level level, BlockPos position, int lifetime, UUID casterUUID, float radius) {
        this(Minagic.NOVA_PROXY.get(), level);
        this.setPos(MathUtils.blockPosToVec3(position));
        setLifetime(lifetime);
        setCasterUUID(casterUUID);
        setRadius(radius);
    }

    // =========================
    // Synched data definition
    // =========================
    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LIFETIME, 0);
        builder.define(RADIUS, 0f);
        builder.define(CASTER_UUID, "");
    }

    // =========================
    // Attributes (required!)
    // =========================
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 0.0D)
                .add(Attributes.ARMOR, 0.0D);
    }

    // =========================
    // Tick logic
    // =========================
    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) return;

        int life = getLifetime();
        if (life <= 0) {
            discard();
            return;
        }

        setLifetime(life - 1);
    }

    // =========================
    // Getters / setters
    // =========================
    public int getLifetime() {
        return this.entityData.get(LIFETIME);
    }

    public void setLifetime(int value) {
        this.entityData.set(LIFETIME, value);
    }

    public float getRadius() {
        return this.entityData.get(RADIUS);
    }

    public void setRadius(float value) {
        this.entityData.set(RADIUS, value);
    }

    public UUID getCasterUUID() {
        String s = this.entityData.get(CASTER_UUID);
        return s.isEmpty() ? null : UUID.fromString(s);
    }

    public void setCasterUUID(UUID uuid) {
        this.entityData.set(CASTER_UUID, uuid == null ? "" : uuid.toString());
    }

    // =========================
    // Entity behavior
    // =========================
    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource source, float amount) {
        return false;
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public @NotNull ItemStack getItem() {
        return ItemStack.EMPTY;
    }

    // =========================
    // Save / Load
    // =========================
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("lifetime", getLifetime());
        output.putFloat("radius", getRadius());

        UUID caster = getCasterUUID();
        if (caster != null) {
            output.putString("caster", caster.toString());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        setLifetime(input.read("lifetime", Codec.INT).orElse(0));
        setRadius(input.read("radius", Codec.FLOAT).orElse(0f));

        input.read("caster", Codec.STRING).ifPresent(s -> {
            try {
                setCasterUUID(UUID.fromString(s));
            } catch (IllegalArgumentException ignored) {
                setCasterUUID(null);
            }
        });
    }


}