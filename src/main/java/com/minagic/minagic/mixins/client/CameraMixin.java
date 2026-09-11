package com.minagic.minagic.mixins.client;

import com.minagic.minagic.client.input.ClientKeybinds;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Shadow
    protected Quaternionf rotation;

    @Inject(method = "setup", at = @At("TAIL"))
    private void minagic$overrideShipCamera(BlockGetter level,

                                            Entity cameraEntity,

                                            boolean detached,

                                            boolean mirrored,

                                            float partialTick,

                                            CallbackInfo ci){


        if (ClientKeybinds.SHIP_FREELOOK.isDown()) return;
        if (cameraEntity.getVehicle() instanceof ArcaneShipEntity ship){
            Quaternionf shipRotation = new Quaternionf(ship.getState().orientation());
            if (Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_FRONT) shipRotation.rotateLocalY((float) Math.PI);

            Vector3f shipUp = new Vector3f(0.0F, 1.0F, 0.0F)
                    .rotate(shipRotation);

            Vector3f shipRight = new Vector3f(1.0F, 0.0F, 0.0F)
                    .rotate(shipRotation);

            Vector3f forward = new Vector3f(0.0F, 0.0F, 1.0F)

                    .rotate(shipRotation)

                    .normalize();

            Vector3f worldUp = new Vector3f(0.0F, 1.0F, 0.0F);

            Vector3f referenceRight = new Vector3f(worldUp)

                    .cross(forward)

                    .normalize();

            Vector3f referenceUp = new Vector3f(forward)

                    .cross(referenceRight)

                    .normalize();

            float pitch = (float) Math.toDegrees(-Math.asin(Mth.clamp(forward.y, -1, 1)));
            float yaw = (float) -Math.toDegrees(Math.atan2(forward.x, forward.z));
            float sinRoll = shipUp.dot(referenceRight);

            float cosRoll = shipUp.dot(referenceUp);

            float roll = (float) Mth.atan2(sinRoll, cosRoll);
            this.setRotation(yaw, pitch);
            this.rotation.rotateZ(roll);
        }
    }

    @Inject(method = "setup", at = @At("TAIL"))

    private void minagic$placeCameraInCockpit(

            BlockGetter level,

            Entity cameraEntity,

            boolean detached,

            boolean mirrored,

            float partialTick,

            CallbackInfo ci

    ) {

        if (!(cameraEntity.getVehicle() instanceof ArcaneShipEntity ship)) {

            return;
        }
        Vector3f localCockpitOffset = ship.getCockpitOffset();
        if (Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON){
            localCockpitOffset = ship.getCockpitOffset().add(new Vector3f(0, 2, -4));
        }



        Vector3f worldOffset = new Quaternionf(ship.getState().orientation())

                .transform(localCockpitOffset);

        Vec3 desiredPosition = ship.getPosition(partialTick).add(

                worldOffset.x,

                worldOffset.y,

                worldOffset.z

        );

        this.setPosition(desiredPosition.x, desiredPosition.y, desiredPosition.z);

    }

    @Shadow
    protected abstract void setPosition(double x, double y, double z);

}
