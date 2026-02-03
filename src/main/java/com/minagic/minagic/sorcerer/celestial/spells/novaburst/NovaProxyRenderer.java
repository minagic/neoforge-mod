package com.minagic.minagic.sorcerer.celestial.spells.novaburst;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.units.qual.N;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.Random;

public class NovaProxyRenderer extends EntityRenderer<NovaImpactProxyEntity, NovaProxyRenderer.State> {

    private static final ResourceLocation NOVA_PRECURSOR =
            ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "nova_pulse_precursor");

    private static final ResourceLocation NOVA_PULSE =
            ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "nova_pulse");

    public NovaProxyRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    // =========================
    // RENDER STATE
    // =========================
    public static class State extends EntityRenderState {
        public Map<ResourceLocation, Float> progress = Map.of();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(NovaImpactProxyEntity entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.progress = SimulacraAttachment.getAllProgress(entity);
        Minagic.LOGGER.debug("Extracted progress map: {}", state.progress);
    }

    // =========================
    // RENDER
    // =========================
    @Override
    public void submit(State state,
                       PoseStack poseStack,
                       SubmitNodeCollector collector,
                       CameraRenderState cameraState) {

        if (state.progress.isEmpty()) return;



        // Precursor (lines)
        if (state.progress.containsKey(NOVA_PRECURSOR) && state.progress.getOrDefault(NOVA_PRECURSOR, 0f) != 0f && state.progress.getOrDefault(NOVA_PULSE, 0f) != 1f) {
            collector.submitCustomGeometry(poseStack, RenderType.debugQuads(),
                    (pose, consumer) -> {
                        float progress = state.progress.get(NOVA_PRECURSOR);
                        renderPrecursor(pose, consumer, progress);
                    }
            );
            return;
        }
        if (state.progress.containsKey(NOVA_PULSE) && state.progress.getOrDefault(NOVA_PULSE, 0f) != 0f && state.progress.getOrDefault(NOVA_PULSE, 0f) != 1f ) {
            collector.submitCustomGeometry(poseStack, RenderType.debugQuads(),
                    (pose, consumer) -> {
                        float progress = state.progress.get(NOVA_PULSE);
                        renderPulse(pose, consumer, progress);
                    }
            );
        }



    }


    // =========================
    // PRECURSOR
    // =========================
    private void renderPrecursor(PoseStack.Pose pose, VertexConsumer vc, float progress) {
        Matrix4f mat = pose.pose();
        float r = 5.0f;
        int rings = 12;
        int segments = 24;
        int alpha = 200;

        renderOrb(pose, vc, r, alpha, rings, segments);


    }
    private Vec3 spherePoint(float r, float phi, float theta) {
        float x = (float)(r * Math.sin(phi) * Math.cos(theta));
        float y = (float)(r * Math.cos(phi));
        float z = (float)(r * Math.sin(phi) * Math.sin(theta));
        return new Vec3(x, y, z);
    }


    // =========================
    // PULSE
    // =========================
    private void renderPulse(PoseStack.Pose pose, VertexConsumer vc, float progress) {
        float radius = 5+ (1-progress)*(1-progress) * 35f;
        renderOrb(pose, vc, radius, 255, 32, 64);
    }

    // =========================
    // ORB
    // =========================
    private void renderOrb(PoseStack.Pose pose, VertexConsumer vc, float radius, int alpha, int rings, int segments) {
        Matrix4f mat = pose.pose();

        for (int i = 0; i < rings; i++) {
            float phi1 = (float) (Math.PI * i / rings);
            float phi2 = (float) (Math.PI * (i + 1) / rings);
            for (int j = 0; j < segments; j++) {
                float t1 = (float) (2.0 * Math.PI * j / segments);
                float t2 = (float) (2.0 * Math.PI * (j + 1) / segments);

                Vec3 v11 = spherePoint(radius, phi1, t1);
                Vec3 v12 = spherePoint(radius, phi1, t2);
                Vec3 v21 = spherePoint(radius, phi2, t1);
                Vec3 v22 = spherePoint(radius, phi2, t2);

                addVertexColor(vc, mat, v11, alpha);
                addVertexColor(vc, mat, v21, alpha);
                addVertexColor(vc, mat, v22, alpha);
                addVertexColor(vc, mat, v12, alpha);
            }
        }
    }

    // =========================
    // LINE
    // =========================
    private void drawLine(PoseStack stack, VertexConsumer vc, Vec3 from, Vec3 to,
                          int r, int g, int b, int a) {
        PoseStack.Pose pose = stack.last();
        Matrix4f mat = pose.pose();

        vc.addVertex(mat, (float) from.x, (float) from.y, (float) from.z).setColor(r,g,b,a).setNormal(0, 1, 0);
        vc.addVertex(mat, (float) to.x, (float) to.y, (float) to.z).setColor(r,g,b,a).setNormal(0, 1, 0);
    }

    private void addLine(VertexConsumer vc, Matrix4f mat, Vec3 from, Vec3 to, int alpha) {
        vc.addVertex(mat, (float) from.x, (float) from.y, (float) from.z)
                .setColor(255, 255, 255, alpha)
                .setNormal(0, 1, 0);
        vc.addVertex(mat, (float) to.x, (float) to.y, (float) to.z)
                .setColor(255, 255, 255, alpha)
                .setNormal(0, 1, 0);
    }

    private void drawConeLines(VertexConsumer vc, Matrix4f mat, Vec3 apex, float baseZ, float radius, int alpha) {
        Vec3 b1 = new Vec3(radius, 0.0, baseZ);
        Vec3 b2 = new Vec3(0.0, radius, baseZ);
        Vec3 b3 = new Vec3(-radius, 0.0, baseZ);
        Vec3 b4 = new Vec3(0.0, -radius, baseZ);

        // Sides
        addLine(vc, mat, apex, b1, alpha);
        addLine(vc, mat, apex, b2, alpha);
        addLine(vc, mat, apex, b3, alpha);
        addLine(vc, mat, apex, b4, alpha);

        // Base square
        addLine(vc, mat, b1, b2, alpha);
        addLine(vc, mat, b2, b3, alpha);
        addLine(vc, mat, b3, b4, alpha);
        addLine(vc, mat, b4, b1, alpha);
    }


    private void addVertexColor(VertexConsumer vc, Matrix4f mat, Vec3 v, int alpha) {
        vc.addVertex(mat, (float) v.x, (float) v.y, (float) v.z)
                .setColor(255, 255, 255, alpha);
    }

    @Override
    public boolean shouldRender(NovaImpactProxyEntity entity, Frustum frustum, double camX, double camY, double camZ) {
        return true; // always render if client knows about it
    }
}
