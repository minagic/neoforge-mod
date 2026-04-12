package com.minagic.minagic.utilities;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.AutonomousChargedSpell;
import com.minagic.minagic.baseProjectiles.SpellProjectileEntity;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.registries.ProjectilePortalRendererRegistry;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class ProjectilePortal extends LivingEntity implements ItemSupplier {

    public @NotNull ItemStack getItem() {
        return new ItemStack(Items.GLOWSTONE);
    }

    public static abstract class ProjectilePortalRenderer {
        protected String id;
        public final String getId(){
            return Minagic.MODID+ ":projectile_portal_renderer/"+id;
        }

        public abstract void render(
                ProjectilePortal portal,
                PoseStack poseStack,
                SubmitNodeCollector collector,
                CameraRenderState cameraState
        );

        @Override
        public boolean equals(Object obj) {
            return obj != null && this.getClass() == obj.getClass();
        }

        @Override
        public int hashCode() {
            return this.getClass().hashCode();
        }

    }

    public interface IPortalableProjectile{
        String rendererID();
    }

    private static final EntityDataAccessor<String> PORTAL_RENDERER_ID =
            SynchedEntityData.defineId(ProjectilePortal.class, EntityDataSerializers.STRING);

    private SpellProjectileEntity projectile;
    private String rendererID;

    public ProjectilePortal(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public ProjectilePortal(Level level, SpellProjectileEntity projectile, int delay){


        super(Minagic.PROJECTILE_PORTAL.get(), level);
        Minagic.LOGGER.info("Creating a new portal for {} with delay {}", projectile, delay);
        if (! (projectile instanceof IPortalableProjectile portalableProjectile)){
            throw new IllegalArgumentException("Selected projectile was not portalable");
        }
        else{
            this.entityData.set(PORTAL_RENDERER_ID, portalableProjectile.rendererID());
        }
        this.projectile = projectile;
        projectile.createPhysicsIfNull();
        int actualDelay = delay + this.random.nextInt(40) - 20;
        SimulacraAttachment.addSimulacrum(this, new SpellCastContext(this), new SpawnPortalEntity(), actualDelay, actualDelay);
        faceVelocity(projectile.physics.direction());
        this.setNoGravity(true);
        this.setPos(projectile.position());
        Minagic.LOGGER.info("Portal's spatial: pos {}, heading {}", this.position(), this.projectile.physics.direction());
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PORTAL_RENDERER_ID, "");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 0.0D)
                .add(Attributes.ARMOR, 0.0D);
    }


    public void spawn(Level level){
        Minagic.LOGGER.info("Portal executes: Spawning {} at {}", projectile, this.position());
        level.addFreshEntity(projectile);
    }

    private void faceVelocity(Vec3 direction) {
        if (direction.lengthSqr() < 1e-6) return;

        float yaw = (float)(Mth.atan2(direction.z, direction.x) * (180F / Math.PI)) - 90F;
        float pitch = (float)(-(Mth.atan2(direction.y,
                Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * (180F / Math.PI)));

        this.setYRot(yaw);
        this.setXRot(pitch);
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }


    @AutoDetection.Spell
    public static class SpawnPortalEntity extends AutonomousChargedSpell {

        public SpawnPortalEntity() {
            this.spellName = "Spawn ProjectilePortal Entity";
            this.idName = "spawn_portal_entity";
            this.cooldown = 0;
            this.isTechnical = true;
        }

        @Override
        public void cast(SpellCastContext context, @Nullable SimulacrumData simulacrumData) {
            if (context.target instanceof ProjectilePortal portal){

                portal.spawn(context.level());
                portal.discard();
            }
        }


    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        String id = this.entityData.get(PORTAL_RENDERER_ID);
        if (id != null && !id.isBlank()) {
            output.putString("portal_renderer", id);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        input.read("portal_renderer", Codec.STRING).ifPresent(id -> {
            Minagic.LOGGER.debug("Loaded portal renderer from NBT: {}", id);
            this.entityData.set(PORTAL_RENDERER_ID, id);
        });
    }

    // === RENDERER ===

    public static class Renderer extends EntityRenderer<ProjectilePortal, ProjectilePortal.Renderer.State> {

        public Renderer(EntityRendererProvider.Context context) {
            super(context);
            Minagic.LOGGER.debug("ProjectilePortal.Renderer constructed");
        }

        public static class State extends EntityRenderState{
            ProjectilePortal portal;
        }

        @Override
        public @NotNull State createRenderState() {
            return new State();
        }

        @Override
        public void extractRenderState(@NotNull ProjectilePortal portal, @NotNull State state, float partialTick){
            super.extractRenderState(portal, state, partialTick);
            state.portal = portal;
        }

        @Override
        public void submit(ProjectilePortal.Renderer.@NotNull State state,
                           @NotNull PoseStack poseStack,
                           @NotNull SubmitNodeCollector collector,
                           @NotNull CameraRenderState cameraState){

            ProjectilePortal portal = state.portal;
            String id = portal.entityData.get(PORTAL_RENDERER_ID);

            if (id == null || id.isBlank()) {
                Minagic.LOGGER.debug("Renderer id not yet synced, skipping render");
                return;
            }

            ProjectilePortalRenderer renderer =
                    ProjectilePortalRendererRegistry.getProjectilePortalRenderer(id);

            if (renderer == null) {
                Minagic.LOGGER.warn("No renderer found for id {}", id);
                Minagic.LOGGER.debug("Found the following ids: {}", ProjectilePortalRendererRegistry.REGISTRY);
                return;
            }

            renderer.render(portal, poseStack, collector, cameraState);

        }



    }

}
