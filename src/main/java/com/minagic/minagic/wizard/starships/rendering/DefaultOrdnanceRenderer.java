package com.minagic.minagic.wizard.starships.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class DefaultOrdnanceRenderer
        implements OrdnanceRenderer {

    private static final float PYLON_WIDTH  = 0.18F;
    private static final float PYLON_HEIGHT = 0.35F;
    private static final float PYLON_LENGTH = 0.50F;

    private static final float MISSILE_WIDTH  = 0.18F;
    private static final float MISSILE_HEIGHT = 0.18F;
    private static final float MISSILE_LENGTH = 1.20F;

    private static final int READY_ALPHA = 255;
    private static final int BUILDING_ALPHA = 80;
    private static final int FULL_BRIGHT = 0x00F000F0;
    @Override
    public void render(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            Vec3 localPosition,
            Vec3 localDirection,
            MountRenderState state
    ) {
        if (localDirection.lengthSqr() < 1.0E-8) {
            return;
        }

        poseStack.pushPose();

        poseStack.translate(
                localPosition.x,
                localPosition.y,
                localPosition.z
        );

        /*
         * Rotate the model's +Z axis toward the mount direction.
         */
        Vector3f direction =
                localDirection.toVector3f().normalize();

        Quaternionf mountRotation =
                new Quaternionf().rotationTo(
                        new Vector3f(0.0F, 0.0F, 1.0F),
                        direction
                );

        poseStack.mulPose(mountRotation);

        /*
         * The pylon is always visible.
         */
        collector.submitCustomGeometry(
                poseStack,
                RenderType.debugQuads(),
                this::renderPylon
        );

        switch (state) {
            case READY -> collector.submitCustomGeometry(
                    poseStack,
                    RenderType.debugQuads(),
                    (pose, consumer) ->
                            renderMissile(
                                    pose,
                                    consumer,
                                    READY_ALPHA,
                                    0x00F000F0
                            )
            );

            case BUILDING -> collector.submitCustomGeometry(
                    poseStack,

                    /*
                     * Use the translucent quad render type available in
                     * your mappings. In 1.21.x this is commonly present.
                     */
                    RenderType.translucentMovingBlock(),

                    (pose, consumer) ->
                            renderMissile(
                                    pose,
                                    consumer,
                                    BUILDING_ALPHA,
                                    0x00F000F0
                            )
            );

            case EMPTY -> {
                // Pylon only.
            }
        }

        poseStack.popPose();
    }

    private void renderPylon(
            PoseStack.Pose pose,
            VertexConsumer consumer
    ) {
        /*
         * localPosition is the missile center / fly-off point.
         *
         * Therefore the pylon extends upward from the missile toward the hull,
         * rather than pushing the missile farther downward.
         */
        addBox(
                pose,
                consumer,

                -PYLON_WIDTH * 0.5F,
                MISSILE_HEIGHT * 0.5F,
                -PYLON_LENGTH * 0.5F,

                PYLON_WIDTH * 0.5F,
                MISSILE_HEIGHT * 0.5F + PYLON_HEIGHT,
                PYLON_LENGTH * 0.5F,

                85,
                90,
                105,
                255,

                0x00F000F0
        );
    }

    private void renderMissile(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            int alpha,
            int packedLight
    ) {
        /*
         * Place the missile below the pylon.
         */
        float centerY = 0.0F;

        float halfWidth = MISSILE_WIDTH * 0.5F;

        float halfHeight = MISSILE_HEIGHT * 0.5F;

        float halfLength = MISSILE_LENGTH * 0.5F;

        float minX = -halfWidth;

        float maxX = halfWidth;

        float minY = centerY - halfHeight;

        float maxY = centerY + halfHeight;

        float minZ = -halfLength;

        float maxZ = halfLength;

        /*
         * Main missile body.
         */
        addBox(
                pose,
                consumer,

                minX,
                minY,
                minZ,

                maxX,
                maxY,
                maxZ,

                180,
                190,
                205,
                alpha,
                packedLight

        );

        /*
         * Nose cone pointing along +Z.
         */
        float noseTipZ =
                maxZ + 0.35F;

        addPyramidNose(
                pose,
                consumer,

                minX,
                minY,
                maxX,
                maxY,
                maxZ,
                noseTipZ,

                210,
                220,
                235,
                alpha,
                packedLight
        );

        /*
         * Rear fins.
         */
        float finZMin = minZ;
        float finZMax = minZ + 0.25F;

        float finExtent = 0.12F;

        addBox(
                pose,
                consumer,

                -halfWidth - finExtent,
                centerY - 0.025F,
                finZMin,

                halfWidth + finExtent,
                centerY + 0.025F,
                finZMax,

                120,
                130,
                150,
                alpha,
                packedLight
        );

        addBox(
                pose,
                consumer,

                -0.025F,
                minY - finExtent,
                finZMin,

                0.025F,
                maxY + finExtent,
                finZMax,

                120,
                130,
                150,
                alpha,
                packedLight
        );
    }
    private static void addPyramidNose(
            PoseStack.Pose pose,
            VertexConsumer consumer,

            float minX,
            float minY,
            float maxX,
            float maxY,

            float baseZ,
            float tipZ,

            int red,
            int green,
            int blue,
            int alpha,

            int packedLight
    ) {
        Matrix4f matrix = pose.pose();

        float centerX = (minX + maxX) * 0.5F;
        float centerY = (minY + maxY) * 0.5F;

        /*
         * Triangles are emitted as degenerate quads because this render path
         * consumes four vertices per primitive.
         */

        // Top face
        addQuad(
                consumer,
                matrix,

                minX, maxY, baseZ,
                maxX, maxY, baseZ,
                centerX, centerY, tipZ,
                centerX, centerY, tipZ,

                0.0F, 1.0F, 0.4F,

                red, green, blue, alpha,
                packedLight
        );

        // Bottom face
        addQuad(
                consumer,
                matrix,

                maxX, minY, baseZ,
                minX, minY, baseZ,
                centerX, centerY, tipZ,
                centerX, centerY, tipZ,

                0.0F, -1.0F, 0.4F,

                red, green, blue, alpha,
                packedLight
        );

        // Left face
        addQuad(
                consumer,
                matrix,

                minX, minY, baseZ,
                minX, maxY, baseZ,
                centerX, centerY, tipZ,
                centerX, centerY, tipZ,

                -1.0F, 0.0F, 0.4F,

                red, green, blue, alpha,
                packedLight
        );

        // Right face
        addQuad(
                consumer,
                matrix,

                maxX, maxY, baseZ,
                maxX, minY, baseZ,
                centerX, centerY, tipZ,
                centerX, centerY, tipZ,

                1.0F, 0.0F, 0.4F,

                red, green, blue, alpha,
                packedLight
        );
    }

    private static void addBox(
            PoseStack.Pose pose,
            VertexConsumer consumer,

            float minX,
            float minY,
            float minZ,

            float maxX,
            float maxY,
            float maxZ,

            int red,
            int green,
            int blue,
            int alpha,
            int packedLight
    ) {
        Matrix4f matrix = pose.pose();

        // Front: -Z
        addQuad(
                consumer,
                matrix,

                minX, minY, minZ,
                maxX, minY, minZ,
                maxX, maxY, minZ,
                minX, maxY, minZ,

                0.0F, 0.0F, -1.0F,

                red, green, blue, alpha,
                packedLight
        );

        // Back: +Z
        addQuad(
                consumer,
                matrix,

                maxX, minY, maxZ,
                minX, minY, maxZ,
                minX, maxY, maxZ,
                maxX, maxY, maxZ,

                0.0F, 0.0F, 1.0F,

                red, green, blue, alpha,
                packedLight
        );

        // Left: -X
        addQuad(
                consumer,
                matrix,

                minX, minY, maxZ,
                minX, minY, minZ,
                minX, maxY, minZ,
                minX, maxY, maxZ,

                -1.0F, 0.0F, 0.0F,

                red, green, blue, alpha
                ,packedLight

        );

        // Right: +X
        addQuad(
                consumer,
                matrix,

                maxX, minY, minZ,
                maxX, minY, maxZ,
                maxX, maxY, maxZ,
                maxX, maxY, minZ,

                1.0F, 0.0F, 0.0F,

                red, green, blue, alpha
                ,packedLight
        );

        // Top: +Y
        addQuad(
                consumer,
                matrix,

                minX, maxY, minZ,
                maxX, maxY, minZ,
                maxX, maxY, maxZ,
                minX, maxY, maxZ,

                0.0F, 1.0F, 0.0F,

                red, green, blue, alpha,
                packedLight
        );

        // Bottom: -Y
        addQuad(
                consumer,
                matrix,

                minX, minY, maxZ,
                maxX, minY, maxZ,
                maxX, minY, minZ,
                minX, minY, minZ,

                0.0F, -1.0F, 0.0F,

                red, green, blue, alpha,
                packedLight
        );
    }

    private static void addQuad(
            VertexConsumer consumer,
            Matrix4f matrix,

            float x1,
            float y1,
            float z1,

            float x2,
            float y2,
            float z2,

            float x3,
            float y3,
            float z3,

            float x4,
            float y4,
            float z4,

            float normalX,
            float normalY,
            float normalZ,

            int red,
            int green,
            int blue,
            int alpha,

            int packedLight
    ) {
        addVertex(
                consumer,
                matrix,
                x1, y1, z1,
                normalX, normalY, normalZ,
                red, green, blue, alpha,
                0.0F, 0.0F,
                packedLight
        );

        addVertex(
                consumer,
                matrix,
                x2, y2, z2,
                normalX, normalY, normalZ,
                red, green, blue, alpha,
                1.0F, 0.0F,
                packedLight
        );

        addVertex(
                consumer,
                matrix,
                x3, y3, z3,
                normalX, normalY, normalZ,
                red, green, blue, alpha,
                1.0F, 1.0F,
                packedLight
        );

        addVertex(
                consumer,
                matrix,
                x4, y4, z4,
                normalX, normalY, normalZ,
                red, green, blue, alpha,
                0.0F, 1.0F,
                packedLight
        );
    }

    private static void addVertex(
            VertexConsumer consumer,
            Matrix4f matrix,

            float x,
            float y,
            float z,

            float normalX,
            float normalY,
            float normalZ,

            int red,
            int green,
            int blue,
            int alpha,

            float u,
            float v,

            int packedLight
    ) {
        consumer.addVertex(matrix, x, y, z)
                .setColor(red, green, blue, alpha)
                .setUv(u, v)
                .setLight(packedLight)
                .setNormal(
                        normalX,
                        normalY,
                        normalZ
                );
    }
}
