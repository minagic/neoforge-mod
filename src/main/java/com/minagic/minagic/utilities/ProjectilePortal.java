package com.minagic.minagic.utilities;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.spells.AutonomousChargedSpell;
import com.minagic.minagic.baseProjectiles.SpellProjectileEntity;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Holder;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class ProjectilePortal extends LivingEntity implements ItemSupplier {

    public @NotNull ItemStack getItem() {
        return new ItemStack(Items.GLOWSTONE);
    }

    public interface IPortalableProjectile{
        default void render(ProjectilePortal portal,
                           @NotNull PoseStack poseStack,
                           @NotNull SubmitNodeCollector collector,
                           @NotNull CameraRenderState cameraState){
            poseStack.pushPose();

            collector.submitCustomGeometry(
                    poseStack,
                    RenderType.debugQuads(),
                    (pose, consumer) -> renderEllipse(pose, consumer, 1.5f, 0.75f, 180, 8, 48)
            );
        }

        default void renderEllipse(PoseStack.Pose pose,
                              VertexConsumer vc,
                              float radiusX,
                              float radiusY,
                              int alpha,
                              int bands,
                              int segments) {
            Matrix4f mat = pose.pose();

            for (int band = 0; band < bands; band++) {
                float r1 = (float) band / bands;
                float r2 = (float) (band + 1) / bands;

                float innerX = radiusX * r1;
                float innerY = radiusY * r1;
                float outerX = radiusX * r2;
                float outerY = radiusY * r2;

                for (int i = 0; i < segments; i++) {
                    float t1 = (float) (2.0 * Math.PI * i / segments);
                    float t2 = (float) (2.0 * Math.PI * (i + 1) / segments);

                    Vec3 v11 = ellipsePoint(innerX, innerY, t1);
                    Vec3 v12 = ellipsePoint(innerX, innerY, t2);
                    Vec3 v21 = ellipsePoint(outerX, outerY, t1);
                    Vec3 v22 = ellipsePoint(outerX, outerY, t2);

                    addVertexColor(vc, mat, v11, alpha);
                    addVertexColor(vc, mat, v21, alpha);
                    addVertexColor(vc, mat, v22, alpha);
                    addVertexColor(vc, mat, v12, alpha);
                }
            }
        }
        default Vec3 ellipsePoint(float radiusX, float radiusY, float theta) {
            float x = Mth.cos(theta) * radiusX;
            float y = Mth.sin(theta) * radiusY;
            return new Vec3(x, y, 0.0);
        }

        default void addVertexColor(VertexConsumer vc, Matrix4f mat, Vec3 v, int alpha) {
            vc.addVertex(mat, (float) v.x, (float) v.y, (float) v.z)
                    .setColor(100, 100, 255, alpha);
        }


    }

    private SpellProjectileEntity projectile;

    public ProjectilePortal(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public ProjectilePortal(Level level, SpellProjectileEntity projectile, int delay){


        super(Minagic.PROJECTILE_PORTAL.get(), level);
        Minagic.LOGGER.info("Creating a new portal for {} with delay {}", projectile, delay);
        if (! (projectile instanceof IPortalableProjectile)){
            throw new IllegalArgumentException("Selected projectile was not portalable");
        }
        this.projectile = projectile;
        projectile.createPhysicsIfNull();
        SimulacraAttachment.addSimulacrum(this, new SpellCastContext(this), new SpawnPortalEntity(), delay, delay);
        faceVelocity(projectile.physics.direction());
        this.setNoGravity(true);
        this.setPos(projectile.position());
        Minagic.LOGGER.info("Portal's spatial: pos {}, heading {}", this.position(), this.projectile.physics.direction());
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
            state.portal = portal;
        }

        @Override
        public void submit(ProjectilePortal.Renderer.@NotNull State state,
                           @NotNull PoseStack poseStack,
                           @NotNull SubmitNodeCollector collector,
                           @NotNull CameraRenderState cameraState){

            ProjectilePortal portal = state.portal;
            IPortalableProjectile renderable = (IPortalableProjectile) portal.projectile;
            renderable.render(portal, poseStack, collector, cameraState);
        }


    }

}
