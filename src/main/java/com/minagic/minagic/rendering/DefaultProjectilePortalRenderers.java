package com.minagic.minagic.rendering;
    
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.utilities.ProjectilePortal;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
public class DefaultProjectilePortalRenderers {

    @AutoDetection.ProjectilePortalRenderer
    public static class StarShardPortalRenderer extends ProjectilePortal.ProjectilePortalRenderer {

        public StarShardPortalRenderer(){
            this.id = "star_shard";
        }

        @Override
        public void render(ProjectilePortal portal,
                           @NotNull PoseStack poseStack,
                           @NotNull SubmitNodeCollector collector,
                           @NotNull CameraRenderState cameraState) {
            poseStack.pushPose();

            Vec3 dir = portal.getLookAngle(); // or whatever field/source you use
            rotateToDirection(poseStack, dir);

            collector.submitCustomGeometry(
                    poseStack,
                    RenderType.debugQuads(),
                    (pose, consumer) -> renderEllipse(pose, consumer, 1.5f, 0.75f, 180, 8, 48)
            );

            poseStack.popPose();
        }

        protected void renderEllipse(PoseStack.Pose pose,
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

        protected Vec3 ellipsePoint(float radiusX, float radiusY, float theta) {
            float x = Mth.cos(theta) * radiusX;
            float y = Mth.sin(theta) * radiusY;
            return new Vec3(x, y, 0.0);
        }

        protected void addVertexColor(VertexConsumer vc, Matrix4f mat, Vec3 v, int alpha) {
            vc.addVertex(mat, (float) v.x, (float) v.y, (float) v.z)
                    .setColor(100, 100, 255, alpha);
        }

        protected void rotateToDirection(PoseStack poseStack, Vec3 dir) {
            if (dir.lengthSqr() < 1e-6) {
                return;
            }

            dir = dir.normalize();

            float yaw = (float) (Mth.atan2(dir.z, dir.x) * (180F / Math.PI)) - 90F;
            float pitch = (float) (-(Mth.atan2(dir.y, Math.sqrt(dir.x * dir.x + dir.z * dir.z)) * (180F / Math.PI)));

            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-yaw));
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
        }


    }
}
