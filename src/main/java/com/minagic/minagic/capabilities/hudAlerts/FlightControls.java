package com.minagic.minagic.capabilities.hudAlerts;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;


public class FlightControls implements AutoDetection.IRenderableAttachment {
    private Quaternionf craftOrientation;

    private FlightControls(float x, float y, float z, float w) {
        this.craftOrientation = new Quaternionf(x, y, z, w);
    }

    public FlightControls() {
        this.craftOrientation = new Quaternionf();
    }

    private Quaternionf getCraftOrientation() {
        return craftOrientation;
    }

    private void setCraftOrientation(Quaternionf craftOrientation) {
        this.craftOrientation = craftOrientation;
    }

    private static FlightControls getAttachment(Entity host) {
        return host.getData(ModAttachments.FLIGHT_CONTROLS);
    }

    public static Quaternionf getCraftOrientation(Entity host) {
        return getAttachment(host).getCraftOrientation();
    }

    private static void writeAttachment(Entity host, FlightControls attachment) {
        host.setData(ModAttachments.FLIGHT_CONTROLS, attachment);
    }

    public static void setCraftOrientation(Entity host, Quaternionf craftOrientation) {
        FlightControls attachment = getAttachment(host);
        attachment.setCraftOrientation(craftOrientation);
        writeAttachment(host, attachment);

    }

    @Override
    public void render(LivingEntity host, GuiGraphics gui) {
        Font font = Minecraft.getInstance().font;

        // Player sight direction in world space.
        org.joml.Vector3f sight = host.getLookAngle()
                .toVector3f()
                .normalize();

        Quaternionf q = new Quaternionf(craftOrientation).normalize();



        // Test both common model-forward conventions.
        Vector3f forwardPositiveZ = q.transform(
                new Vector3f(0.0F, 0.0F, 1.0F)
        ).normalize();

        Vector3f forwardNegativeZ = q.transform(
                new Vector3f(0.0F, 0.0F, -1.0F)
        ).normalize();

        float dotPositiveZ = Mth.clamp(
                forwardPositiveZ.dot(sight),
                -1.0F,
                1.0F
        );

        float dotNegativeZ = Mth.clamp(
                forwardNegativeZ.dot(sight),
                -1.0F,
                1.0F
        );

        float anglePositiveZ = (float) Math.toDegrees(
                Math.acos(dotPositiveZ)
        );

        float angleNegativeZ = (float) Math.toDegrees(
                Math.acos(dotNegativeZ)
        );

        // Error axis for the currently assumed +Z forward direction.
        Vector3f errorAxis = new Vector3f(forwardPositiveZ)
                .cross(sight);

        float errorMagnitude = errorAxis.length();

        if (errorMagnitude > 1.0E-6F) {
            errorAxis.div(errorMagnitude);
        } else {
            errorAxis.zero();
        }

        int x = 4;
        int y = 4;
        int line = 11;

        gui.drawString(
                font,
                String.format(
                        "Q: %.3f  %.3f  %.3f  %.3f",
                        q.x, q.y, q.z, q.w
                ),
                x, y,
                0xFFFFFFFF,
                true
        );

        y += line;

        gui.drawString(
                font,
                String.format(
                        "Sight:   x=% .3f y=% .3f z=% .3f",
                        sight.x, sight.y, sight.z
                ),
                x, y,
                0xFFFFFF55,
                true
        );

        y += line;

        gui.drawString(
                font,
                String.format(
                        "Hull +Z: x=% .3f y=% .3f z=% .3f",
                        forwardPositiveZ.x,
                        forwardPositiveZ.y,
                        forwardPositiveZ.z
                ),
                x, y,
                0xFF55FFFF,
                true
        );

        y += line;

        gui.drawString(
                font,
                String.format(
                        "+Z dot=% .3f  error=%6.2f deg",
                        dotPositiveZ,
                        anglePositiveZ
                ),
                x, y,
                0xFF55FFFF,
                true
        );

        y += line;

        gui.drawString(
                font,
                String.format(
                        "Hull -Z: x=% .3f y=% .3f z=% .3f",
                        forwardNegativeZ.x,
                        forwardNegativeZ.y,
                        forwardNegativeZ.z
                ),
                x, y,
                0xFFFFAA55,
                true
        );

        y += line;

        gui.drawString(
                font,
                String.format(
                        "-Z dot=% .3f  error=%6.2f deg",
                        dotNegativeZ,
                        angleNegativeZ
                ),
                x, y,
                0xFFFFAA55,
                true
        );

        y += line;

        gui.drawString(
                font,
                String.format(
                        "Error axis: x=% .3f y=% .3f z=% .3f",
                        errorAxis.x,
                        errorAxis.y,
                        errorAxis.z
                ),
                x, y,
                0xFFFF55FF,
                true
        );

        Vector3f shipRight = q.transform(

                new Vector3f(1.0F, 0.0F, 0.0F)

        ).normalize();

        Vector3f shipUp = q.transform(

                new Vector3f(0.0F, 1.0F, 0.0F)

        ).normalize();

        Vector3f shipForward = q.transform(

                new Vector3f(0.0F, 0.0F, 1.0F)

        ).normalize();

        y += line;

        gui.drawString(
                font,
                String.format(
                        "Right:   x=% .3f y=% .3f z=% .3f",
                        shipRight.x,
                        shipRight.y,
                        shipRight.z
                ),
                x,
                y,
                0xFFFF5555,
                true
        );

        y += line;

        gui.drawString(
                font,
                String.format(
                        "Up:      x=% .3f y=% .3f z=% .3f",
                        shipUp.x,
                        shipUp.y,
                        shipUp.z), x, y,
                0xFF55FF55,
                true
        );

        y += line;

        gui.drawString(
                font,
                String.format(
                        "Forward: x=% .3f y=% .3f z=% .3f",
                        shipForward.x,
                        shipForward.y,
                        shipForward.z
                ),
                x,
                y,
                0xFF5555FF,
                true
        );

        final int HORIZON_COLOR = 0xFFFFFFFF;

        final float PIXELS_PER_RADIAN = 60.0F;

        // Avoid mutating the synchronized quaternion.

        Quaternionf orientation = new Quaternionf(craftOrientation).normalize();

        Vector3f forward = orientation.transform(

                new Vector3f(0.0F, 0.0F, 1.0F)

        );

        Vector3f right = orientation.transform(

                new Vector3f(1.0F, 0.0F, 0.0F)

        );

        Vector3f up = orientation.transform(

                new Vector3f(0.0F, 1.0F, 0.0F)

        );

        float pitch = (float) Math.asin(

                Mth.clamp(forward.y, -1.0F, 1.0F)

        );

        float roll = (float) Math.atan2(right.y, up.y);

        int centerX = gui.guiWidth() / 2;

        int centerY = gui.guiHeight() / 2;

        Matrix3x2fStack pose = gui.pose();

        pose.pushMatrix();

        // Make the screen center our local origin.

        pose.translate(centerX, centerY);

        // The horizon moves opposite the craft's roll.

        pose.rotate(-roll);

        // Nose-up means the horizon appears lower on the screen.

        pose.translate(0.0F, pitch * PIXELS_PER_RADIAN);

        // Draw in coordinates relative to the screen center.

        gui.fill(-70, -1, -8, 1, HORIZON_COLOR);

        gui.fill(  8, -1, 70, 1, HORIZON_COLOR);

        pose.popMatrix();

        // Fixed craft/reference marker.

        gui.fill(

                centerX - 18,

                centerY,

                centerX - 4,

                centerY + 2,

                HORIZON_COLOR

        );

        gui.fill(

                centerX + 4,

                centerY,

                centerX + 18,

                centerY + 2,

                HORIZON_COLOR

        );

        gui.fill(

                centerX - 1,

                centerY - 4,

                centerX + 1,

                centerY + 5,

                HORIZON_COLOR

        );

        // Target tracking
        ArcaneShipEntity ship = (ArcaneShipEntity) host.getVehicle();
        assert ship != null;
        Vec3 targetPoint = new Vec3(ship.getCurrentTarget());
        targetPoint = Minecraft.getInstance().gameRenderer.getMainCamera().position().add(targetPoint.scale(100));

        Vec3 ndc = Minecraft.getInstance()
                .gameRenderer
                .projectPointToScreen(new Vec3(targetPoint.toVector3f()));

        int screenX = (int)((ndc.x + 1.0) * 0.5 * gui.guiWidth());
        int screenY = (int)((1.0 - ndc.y) * 0.5 * gui.guiHeight());
        drawFlightTarget(gui, screenX + 1, screenY + 1, 0xA0000000);
        drawFlightTarget(gui, screenX,     screenY,     0xFFD050FF);
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        Vec3 cameraPos = camera.getPosition();

        drawDirection(gui, cameraPos, new Vec3(ship.getCurrentTarget()), 0xFFFF0000);   // Red - ship target
        drawDirection(gui, cameraPos, host.getLookAngle(),             0xFF00FF00);   // Green - player look
        drawDirection(gui, cameraPos, new Vec3(camera.getLookVector()),  0xFF0000FF);   // Blue - camera forward

        Camera cam = Minecraft.getInstance().gameRenderer.getMainCamera();

        Vector3f f = new Vector3f(0, 0, 1);
        f.rotate(cam.rotation());

        Vec3 point = cam.getPosition().add(
                f.x * 100,
                f.y * 100,
                f.z * 100
        );

        Vec3 NDC = Minecraft.getInstance()
                .gameRenderer
                .projectPointToScreen(point);
        Minagic.LOGGER.info("Detected camera forward NDC: {}", NDC);
    }

    private static void drawDirection(GuiGraphics gui, Vec3 origin, Vec3 direction, int color) {
        Vec3 point = origin.add(direction.normalize().scale(100.0));

        Vec3 ndc = Minecraft.getInstance()
                .gameRenderer
                .projectPointToScreen(point);

        if (ndc == null) {
            return;
        }

        int x = (int) ((ndc.x + 1.0) * 0.5 * gui.guiWidth());
        int y = (int) ((1.0 - ndc.y) * 0.5 * gui.guiHeight());

        gui.fill(x - 2, y - 2, x + 3, y + 3, color);
    }

    private static void drawFlightTarget(
            GuiGraphics gui,
            int centerX,
            int centerY,
            int color
    ) {
        int radius = 8;
        int arm = 5;

        // Upper-left angle: ┘-like, pointing toward center
        drawLine(
                gui,
                centerX - radius - arm, centerY - radius,
                centerX - radius,       centerY - radius,
                color
        );
        drawLine(
                gui,
                centerX - radius, centerY - radius,
                centerX - radius, centerY - radius + arm,
                color
        );

        // Upper-right angle: └-like, pointing toward center
        drawLine(
                gui,
                centerX + radius,       centerY - radius,
                centerX + radius + arm, centerY - radius,
                color
        );
        drawLine(
                gui,
                centerX + radius, centerY - radius,
                centerX + radius, centerY - radius + arm,
                color
        );

        // Bottom angle: ∧-like, pointing toward center
        drawLine(
                gui,
                centerX - arm, centerY + radius + arm,
                centerX,       centerY + radius,
                color
        );
        drawLine(
                gui,
                centerX,       centerY + radius,
                centerX + arm, centerY + radius + arm,
                color
        );
    }

    private static void drawLine(
            GuiGraphics gui,
            int x1,
            int y1,
            int x2,
            int y2,
            int color
    ) {
        if (y1 == y2) {
            gui.fill(
                    Math.min(x1, x2),
                    y1,
                    Math.max(x1, x2) + 1,
                    y1 + 1,
                    color
            );
            return;
        }

        if (x1 == x2) {
            gui.fill(
                    x1,
                    Math.min(y1, y2),
                    x1 + 1,
                    Math.max(y1, y2) + 1,
                    color
            );
            return;
        }

        // Simple one-pixel Bresenham line for the bottom chevron.
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int error = dx - dy;

        int x = x1;
        int y = y1;

        while (true) {
            gui.fill(x, y, x + 1, y + 1, color);

            if (x == x2 && y == y2) {
                break;
            }

            int doubledError = error * 2;

            if (doubledError > -dy) {
                error -= dy;
                x += sx;
            }

            if (doubledError < dx) {
                error += dx;
                y += sy;
            }
        }
    }

    private static float extractRollRadians(Quaternionf orientation) {
        Vector3f forward = new Vector3f(0.0F, 0.0F, 1.0F)
                .rotate(orientation)
                .normalize();

        Vector3f shipUp = new Vector3f(0.0F, 1.0F, 0.0F)
                .rotate(orientation)
                .normalize();

        Vector3f worldUp = new Vector3f(0.0F, 1.0F, 0.0F);

        Vector3f referenceRight = new Vector3f(worldUp).cross(forward);

        // Forward is nearly vertical, so roll becomes ambiguous.
        if (referenceRight.lengthSquared() < 1.0E-6F) {
            return 0.0F;
        }

        referenceRight.normalize();

        Vector3f referenceUp = new Vector3f(forward)
                .cross(referenceRight)
                .normalize();

        float sinRoll = shipUp.dot(referenceRight);
        float cosRoll = shipUp.dot(referenceUp);

        return (float) Math.atan2(sinRoll, cosRoll);
    }

    @Override
    public boolean shouldRender(LivingEntity host) {
        return host.getVehicle() instanceof ArcaneShipEntity;
    }


    public static final Codec<FlightControls> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("message").forGetter(fc -> fc.craftOrientation.x),
            Codec.FLOAT.fieldOf("color").forGetter(fc -> fc.craftOrientation.y),
            Codec.FLOAT.fieldOf("priority").forGetter(fc -> fc.craftOrientation.z),
            Codec.FLOAT.fieldOf("durationTicks").forGetter(fc -> fc.craftOrientation.w)
    ).apply(instance, FlightControls::new));

    public static final class Serializer implements IAttachmentSerializer<FlightControls> {

        @Override
        public FlightControls read(IAttachmentHolder holder, ValueInput input) {
            try {
                float x = input.read("x", Codec.FLOAT).get();
                float y = input.read("x", Codec.FLOAT).get();
                float z = input.read("x", Codec.FLOAT).get();
                float w = input.read("x", Codec.FLOAT).get();
                return new FlightControls(x, y, z, w);
            } catch (Exception e) {
                return new FlightControls();
            }

        }

        @Override
        public boolean write(FlightControls attachment, ValueOutput output) {
            output.store("x", Codec.FLOAT, attachment.craftOrientation.x);
            output.store("y", Codec.FLOAT, attachment.craftOrientation.y);
            output.store("z", Codec.FLOAT, attachment.craftOrientation.z);
            output.store("w", Codec.FLOAT, attachment.craftOrientation.w);

            return true;
        }
    }


}
