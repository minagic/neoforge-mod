package com.minagic.minagic.wizard.starships.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.phys.Vec3;

public interface OrdnanceRenderer {
    enum MountRenderState {
        READY,
        BUILDING,
        EMPTY
    }
    void render(

            PoseStack poseStack,

            SubmitNodeCollector collector,

            Vec3 localPosition,

            Vec3 localDirection,

            MountRenderState state

    );

}