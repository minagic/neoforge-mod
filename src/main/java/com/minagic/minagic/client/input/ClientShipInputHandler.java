package com.minagic.minagic.client.input;

import com.minagic.minagic.common.network.packets.ClientShipInputPacket;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import org.checkerframework.checker.units.qual.C;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ClientShipInputHandler {
    public static class ShipInput{
        public float vertical;
        public float forward;
        public float strafe;

        public float pitch;
        public float yaw;
        public float roll;

        public ShipInput(float vertical, float forward, float strafe, float pitch, float yaw, float roll) {
            this.vertical = vertical;
            this.forward = forward;
            this.strafe = strafe;
            this.pitch = pitch;
            this.yaw = yaw;
            this.roll = roll;
        }

        public ClientShipInputPacket createPacket(){
            return new ClientShipInputPacket(vertical, forward, strafe, pitch, yaw, roll);
        }

        public static ShipInput fromPacket(ClientShipInputPacket pkt){
            return new ShipInput(pkt.vertical(), pkt.forward(), pkt.strafe(), pkt.pitch(), pkt.yaw(), pkt.roll());

        }

        public static ShipInput NONE = new ShipInput(0,0,0,0,0,0);

        public ShipInput sanitized() {

            return new ShipInput(

                    sanitizeAxis(forward),

                    sanitizeAxis(strafe),

                    sanitizeAxis(vertical),

                    sanitizeAxis(yaw),

                    sanitizeAxis(pitch),

                    sanitizeAxis(roll)


            );

        }

        private static float sanitizeAxis(float value) {

            if (!Float.isFinite(value)) {

                return 0.0F;

            }

            return Mth.clamp(value, -1.0F, 1.0F);

        }

        public String toString(){
            return "Ship input: Vertical: %f, Forward: %f, Strafe: %f, Pitch: %f, Yaw: %f, Roll: %f ".formatted(vertical, forward, strafe, pitch, yaw, roll);
        }
    }


    public final class ShipInputReader {

        // =========================
        // MOUSE CONFIGURATION
        // =========================

        /*
         * Number of camera degrees moved in one client tick
         * that produces full steering input.
         */
        private static final float YAW_DEGREES_FOR_FULL_INPUT = 8.0F;
        private static final float PITCH_DEGREES_FOR_FULL_INPUT = 8.0F;

        // =========================
        // MOUSE STATE
        // =========================

        private Vec3 previousLook;
        private boolean mouseStateInitialized;

        // =========================
        // INPUT READING
        // =========================

        public ShipInput readInputs(
                Minecraft minecraft,
                KeyMapping descendKey,
                KeyMapping rollLeftKey,
                KeyMapping rollRightKey,
                KeyMapping freelook,
                ArcaneShipEntity ship
        ) {
            LocalPlayer player = minecraft.player;

            if (player == null || minecraft.screen != null) {
                resetMouseState();
                return ShipInput.NONE;
            }

            Options options = minecraft.options;

            // =========================
            // TRANSLATIONAL INPUT
            // =========================

            float forward = axis(
                    options.keyUp.isDown(),
                    options.keyDown.isDown()
            );

            float strafe = axis(
                    options.keyLeft.isDown(),
                    options.keyRight.isDown()
            );

            float vertical = axis(
                    options.keyJump.isDown(),
                    descendKey.isDown()
            );

            // =========================
            // ROLL INPUT
            // =========================

            float roll = axis(
                    rollRightKey.isDown(),
                    rollLeftKey.isDown()
            );

            // =========================
            // MOUSE INPUT
            // =========================

            float yaw = 0.0F;
            float pitch = freelook.isDown() ? 0 : 1;
//            if (!freelook.isDown()) {
//
//                Vec3 desired = player.getLookAngle();
//                Vector3f f = ship.getOrientation().transform(new Vector3f(0,0,1));
//                Vec3 fwd = new Vec3(f.x, f.y, f.z);
//                Vec3 error = fwd.cross(desired);
//
//                Vector3f right = ship.getOrientation().transform(new Vector3f(1,0,0));
//                Vector3f up    = ship.getOrientation().transform(new Vector3f(0,1,0));
//
//                yaw =
//                        (float)error.dot(new Vec3(up.x, up.y, up.z));
//
//                pitch = (float)-error.dot(new Vec3(right.x, right.y, right.z));
////                if (previousLook != null) {
////
////                    // Small-angle rotation between the two look vectors.
////
////                    Vec3 delta = previousLook.cross(currentLook);
////
////                    Quaternionf q = new Quaternionf(ship.getOrientation()).normalize();
////
////                    Vector3f right = q.transform(new Vector3f(1, 0, 0));
////
////                    Vector3f up    = q.transform(new Vector3f(0, 1, 0));
////
////                    // Project the world-space rotation onto the ship's local axes.
////
////                    yaw = (float) delta.dot(new Vec3(up.x, up.y, up.z));
////
////                    pitch = (float) delta.dot(new Vec3(right.x, right.y, right.z));
////
////                    // Tune these!
////
////                    yaw *= 2.0F;
////
////                    pitch *= 2.0F;
////
////                    yaw = Mth.clamp(yaw, -1.0F, 1.0F);
////
////                    pitch = Mth.clamp(pitch, -1.0F, 1.0F);
////
////                }
////
////                previousLook = currentLook;
//
//            }
//
//            else {
//
//                // Avoid a huge jump when leaving freelook.
//
//                previousLook = player.getLookAngle();
//
//            }



            return new ShipInput(
                    vertical,
                    forward,
                    strafe,
                    pitch,
                    yaw,
                    roll
            );
        }

        // =========================
        // STATE MANAGEMENT
        // =========================

        public void resetMouseState() {
            mouseStateInitialized = false;
        }

        // =========================
        // AXIS UTILITIES
        // =========================

        private static float axis(
                boolean positive,
                boolean negative
        ) {
            return (positive ? 1.0F : 0.0F)
                    - (negative ? 1.0F : 0.0F);
        }
    }

    private ShipInputReader reader = new ShipInputReader();

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event){
        Minecraft minecraft = Minecraft.getInstance();

        LocalPlayer player = minecraft.player;

        if (player == null) {

            return;

        }

        if (!(player.getVehicle() instanceof ArcaneShipEntity ship)) {

            return;

        }

        ShipInput input = reader.readInputs(minecraft, ClientKeybinds.DESCEND, ClientKeybinds.ROLL_RIGHT, ClientKeybinds.ROLL_LEFT, ClientKeybinds.SHIP_FREELOOK, ship);


        ClientPacketDistributor.sendToServer(

                input.createPacket());
    }


}
















