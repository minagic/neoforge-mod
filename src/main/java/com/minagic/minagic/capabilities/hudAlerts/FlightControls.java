package com.minagic.minagic.capabilities.hudAlerts;

import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.powersource.AbstractPowerSource;
import com.minagic.minagic.capabilities.powersource.ActivePowerSourceAttachment;
import com.minagic.minagic.capabilities.powersource.ShipPowerSourceAttachment;
import com.minagic.minagic.client.input.ShipFlightTestRunner;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;



public class FlightControls implements AutoDetection.IRenderableAttachment {
    private Quaternionf craftOrientation;
    private float rollSpeed;

    private Vec3 netForce;
    private Vec3 engineForce;
    private Vec3 dragForce;
    private Vec3 gravityForce;

    private FlightControls(
            float x,
            float y,
            float z,
            float w,
            float rollSpeed,
            Vec3 netForce,
            Vec3 engineForce,
            Vec3 dragForce,
            Vec3 gravityForce
    ) {
        this.craftOrientation = new Quaternionf(x, y, z, w);
        this.rollSpeed = rollSpeed;

        this.netForce = netForce;
        this.engineForce = engineForce;
        this.dragForce = dragForce;
        this.gravityForce = gravityForce;
    }

    public FlightControls() {
        this.craftOrientation = new Quaternionf();
        this.rollSpeed = 0.0F;
        this.netForce = Vec3.ZERO;
        this.engineForce = Vec3.ZERO;
        this.dragForce = Vec3.ZERO;
        this.gravityForce = Vec3.ZERO;
    }

    private Quaternionf getCraftOrientation() {
        return craftOrientation;
    }

    private float getRollSpeed() {
        return rollSpeed;
    }

    private Vec3 getNetForce() {
        return netForce;
    }

    private Vec3 getEngineForce() {
        return engineForce;
    }

    private Vec3 getDragForce() {
        return dragForce;
    }

    private Vec3 getGravityForce() {
        return gravityForce;
    }



    private void setCraftOrientation(Quaternionf craftOrientation) {
        this.craftOrientation = new Quaternionf(craftOrientation);
    }

    private void setRollSpeed(float rollSpeed) {
        this.rollSpeed = rollSpeed;
    }

    private void setNetForce(Vec3 netForce) {
        this.netForce = netForce;
    }

    private void setEngineForce(Vec3 engineForce) {
        this.engineForce = engineForce;
    }

    private void setDragForce(Vec3 dragForce) {
        this.dragForce = dragForce;
    }

    private void setGravityForce(Vec3 gravityForce) {
        this.gravityForce = gravityForce;
    }

    private static FlightControls getAttachment(Entity host) {
        return host.getData(ModAttachments.FLIGHT_CONTROLS);
    }


    private static void writeAttachment(Entity host, FlightControls attachment) {
        host.setData(ModAttachments.FLIGHT_CONTROLS, attachment);
    }

    public static Quaternionf getCraftOrientation(Entity host) {
        return getAttachment(host).getCraftOrientation();
    }

    public static float getRollSpeed(Entity host) {
        return getAttachment(host).getRollSpeed();
    }

    public static Vec3 getNetForce(Entity host) {
        return getAttachment(host).getNetForce();
    }

    public static Vec3 getEngineForce(Entity host) {
        return getAttachment(host).getEngineForce();
    }

    public static Vec3 getDragForce(Entity host) {
        return getAttachment(host).getDragForce();
    }

    public static Vec3 getGravityForce(Entity host) {
        return getAttachment(host).getGravityForce();
    }



    public static void setCraftOrientation(
            Entity host,
            Quaternionf craftOrientation
    ) {
        FlightControls attachment = getAttachment(host);

        attachment.setCraftOrientation(craftOrientation);

        writeAttachment(host, attachment);
    }

    public static void setRollSpeed(
            Entity host,
            float rollSpeed
    ) {
        FlightControls attachment = getAttachment(host);

        attachment.setRollSpeed(rollSpeed);

        writeAttachment(host, attachment);
    }

    public static void setNetForce(
            Entity host,
            Vec3 netForce
    ) {
        FlightControls attachment = getAttachment(host);

        attachment.setNetForce(netForce);

        writeAttachment(host, attachment);
    }

    public static void setEngineForce(
            Entity host,
            Vec3 engineForce
    ) {
        FlightControls attachment = getAttachment(host);

        attachment.setEngineForce(engineForce);

        writeAttachment(host, attachment);
    }

    public static void setDragForce(
            Entity host,
            Vec3 dragForce
    ) {
        FlightControls attachment = getAttachment(host);

        attachment.setDragForce(dragForce);

        writeAttachment(host, attachment);
    }

    public static void setGravityForce(
            Entity host,
            Vec3 gravityForce
    ) {
        FlightControls attachment = getAttachment(host);

        attachment.setGravityForce(gravityForce);

        writeAttachment(host, attachment);
    }
    public static void setForceData(
            Entity host,
            Vec3 netForce,
            Vec3 engineForce,
            Vec3 dragForce,
            Vec3 gravityForce
    ) {
        FlightControls attachment = getAttachment(host);

        attachment.setNetForce(netForce);
        attachment.setEngineForce(engineForce);
        attachment.setDragForce(dragForce);
        attachment.setGravityForce(gravityForce);

        writeAttachment(host, attachment);
    }

    @Override
    public void render(LivingEntity host, GuiGraphics gui) {
        Font font = Minecraft.getInstance().font;


        int x = 4;
        int y = 4;
        int line = 11;

        gui.drawString(
                font,
                String.format(
                        "RollSpeed: % .3f",
                        rollSpeed
                ),
                x, y,
                0xFFFF55FF,
                true
        );

        y += line;

        if (ShipFlightTestRunner.isRunning()) {
            gui.drawString(
                    font,
                    String.format(
                            "activeTest: %s",
                            ShipFlightTestRunner.activeScript.name()
                    ),
                    x, y,
                    0xFFFF55FF,
                    true
            );
            y += line;
        }
        ArcaneShipEntity ship2 = (ArcaneShipEntity) host.getVehicle();
        float pitch = ship2.getState().pendingPitch();
        float yaw = ship2.getState().pendingYaw();
        gui.drawString(
                font,
                String.format(
                        "Current inputs: pitch % .3f, yaw % .3f ",
                        pitch, yaw

                ),
                x, y,
                0xFFFF55FF,
                true
        );

        y+= line;

        y = drawVector(

                gui,

                font,

                x,

                y,

                line,

                "Net force       ",

                netForce,

                0xFFFF55FF

        );

        y = drawVector(

                gui,

                font,

                x,

                y,

                line,

                "Engine force   ",

                engineForce,

                0xFF55FF55

        );

        y = drawVector(

                gui,

                font,

                x,

                y,

                line,

                "Drag force     ",

                dragForce,

                0xFFFFAA55

        );

        y = drawVector(

                gui,

                font,

                x,

                y,

                line,

                "Gravity force  ",

                gravityForce,

                0xFF55AAFF

        );

        y=y = drawVector(

                gui,

                font,

                x,

                y,

                line,

                "Velocity      ",

                ship2.getDeltaMovement(),

                0xFF55AAFF

        );

        Vector3f tc = ship2.getState().throttleControl();

        y = drawVector(

                gui,

                font,

                x,

                y,

                line,

                "Thrust control",

                new Vec3(tc),

                0xFFFFAAFF

        );
        AbstractPowerSource aps = ActivePowerSourceAttachment.getActivePowerSource(ship2);
        if ((aps instanceof ShipPowerSourceAttachment shipPower)) {
            gui.drawString(
                    font,
                    String.format(
                            "Fuel: %d / %d",
                                shipPower.getFuel(), shipPower.getMaxFuel()
                            ),
                    x,
                    y,
                    0xFFFFFFFF,
                    true
            );
        }
        y+=line;



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

        pitch = (float) Math.asin(

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

        gui.fill(8, -1, 70, 1, HORIZON_COLOR);

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
        float hfov = (float) Math.toRadians(
                Minecraft.getInstance().options.fov().get()
        );

        float vfov =
                2.0f * (float) Math.atan(
                        Math.tan(hfov * 0.5f)
                                * gui.guiHeight()
                                / gui.guiWidth()
                );

        float focalX =
                gui.guiWidth() * 0.5f
                        / (float) Math.tan(hfov * 0.5f);

        float focalY =
                gui.guiHeight() * 0.5f
                        / (float) Math.tan(vfov * 0.5f);
        int screenX =

                Math.round(

                        gui.guiWidth() * 0.5f
                                - focalX * (float) Math.tan(ship.getState().pendingYaw())

                );

        int screenY =

                Math.round(

                        gui.guiHeight() * 0.5f

                                + focalY * (float) Math.tan(ship.getState().pendingPitch())

                );


        drawFlightTarget(gui, screenX + 1, screenY + 1, 0xA0000000);
        drawFlightTarget(gui, screenX, screenY, 0xFFD050FF);


    }

    private static int drawVector(
            GuiGraphics gui,
            Font font,
            int x,
            int y,
            int lineHeight,
            String name,
            Vec3 vector,
            int color
    ) {
        if (vector == null) {
            return y;
        }

        gui.drawString(
                font,
                String.format(
                        "%s: x=% .3f y=% .3f z=% .3f |F|=% .3f",
                        name,
                        vector.x,
                        vector.y,
                        vector.z,
                        vector.length()
                ),
                x,
                y,
                color,
                true
        );

        return y + lineHeight;
    }

    private static void drawFlightTarget(
            GuiGraphics gui,
            int centerX,
            int centerY,
            int color
    ) {
        int radius = 4;
        int arm = 5;
        int offset = 2;

        // Upper-left angle: ┘-like, pointing toward center
        drawLine(
                gui,
                centerX - radius, centerY+radius-offset,
                centerX - radius - arm, centerY +radius+arm -offset,
                color
        );
        drawLine(
                gui,
                centerX - radius, centerY + radius - offset,
                centerX - radius, centerY - offset - arm,
                color
        );

        // Upper-right angle: └-like, pointing toward center
        drawLine(
                gui,
                centerX + radius, centerY+radius-offset,
                centerX + radius + arm, centerY +radius+arm -offset,
                color
        );
        drawLine(
                gui,
                centerX + radius, centerY + radius - offset,
                centerX + radius, centerY  - offset - arm,
                color
        );

        // Bottom angle: ∧-like, pointing toward center
        drawLine(
                gui,
                centerX - arm, centerY + radius + arm,
                centerX, centerY + radius,
                color
        );
        drawLine(
                gui,
                centerX, centerY + radius,
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

    @Override
    public boolean shouldRender(LivingEntity host) {
        return host.getVehicle() instanceof ArcaneShipEntity;
    }

    public static final Codec<Vec3> VEC3_CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.DOUBLE
                                    .fieldOf("x")
                                    .forGetter(vector -> vector.x),

                            Codec.DOUBLE
                                    .fieldOf("y")
                                    .forGetter(vector -> vector.y),

                            Codec.DOUBLE
                                    .fieldOf("z")
                                    .forGetter(vector -> vector.z)
                    ).apply(instance, Vec3::new)
            );
    public static final Codec<FlightControls> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.FLOAT
                                    .fieldOf("orientation_x")
                                    .forGetter(fc -> fc.craftOrientation.x),

                            Codec.FLOAT
                                    .fieldOf("orientation_y")
                                    .forGetter(fc -> fc.craftOrientation.y),

                            Codec.FLOAT
                                    .fieldOf("orientation_z")
                                    .forGetter(fc -> fc.craftOrientation.z),

                            Codec.FLOAT
                                    .fieldOf("orientation_w")
                                    .forGetter(fc -> fc.craftOrientation.w),

                            Codec.FLOAT
                                    .fieldOf("roll_speed")
                                    .forGetter(fc -> fc.rollSpeed),

                            VEC3_CODEC
                                    .fieldOf("net_force")
                                    .forGetter(fc -> fc.netForce),

                            VEC3_CODEC
                                    .fieldOf("engine_force")
                                    .forGetter(fc -> fc.engineForce),

                            VEC3_CODEC
                                    .fieldOf("drag_force")
                                    .forGetter(fc -> fc.dragForce),

                            VEC3_CODEC
                                    .fieldOf("gravity_force")
                                    .forGetter(fc -> fc.gravityForce)
                    ).apply(instance, FlightControls::new)
            );



    public static final class Serializer
            implements IAttachmentSerializer<FlightControls> {

        @Override
        public @NotNull FlightControls read(
                IAttachmentHolder holder,
                ValueInput input
        ) {
            try {
                float x = input.read(
                        "orientation_x",
                        Codec.FLOAT
                ).orElse(0.0F);

                float y = input.read(
                        "orientation_y",
                        Codec.FLOAT
                ).orElse(0.0F);

                float z = input.read(
                        "orientation_z",
                        Codec.FLOAT
                ).orElse(0.0F);

                float w = input.read(
                        "orientation_w",
                        Codec.FLOAT
                ).orElse(1.0F);

                float rollSpeed = input.read(
                        "roll_speed",
                        Codec.FLOAT
                ).orElse(0.0F);

                Vec3 netForce = input.read(
                        "net_force",
                        VEC3_CODEC
                ).orElse(Vec3.ZERO);

                Vec3 engineForce = input.read(
                        "engine_force",
                        VEC3_CODEC
                ).orElse(Vec3.ZERO);

                Vec3 dragForce = input.read(
                        "drag_force",
                        VEC3_CODEC
                ).orElse(Vec3.ZERO);

                Vec3 gravityForce = input.read(
                        "gravity_force",
                        VEC3_CODEC
                ).orElse(Vec3.ZERO);

                return new FlightControls(
                        x,
                        y,
                        z,
                        w,
                        rollSpeed,
                        netForce,
                        engineForce,
                        dragForce,
                        gravityForce
                );
            } catch (Exception exception) {
                return new FlightControls();
            }
        }

        @Override
        public boolean write(
                FlightControls attachment,
                ValueOutput output
        ) {
            output.store(
                    "orientation_x",
                    Codec.FLOAT,
                    attachment.craftOrientation.x
            );

            output.store(
                    "orientation_y",
                    Codec.FLOAT,
                    attachment.craftOrientation.y
            );

            output.store(
                    "orientation_z",
                    Codec.FLOAT,
                    attachment.craftOrientation.z
            );

            output.store(
                    "orientation_w",
                    Codec.FLOAT,
                    attachment.craftOrientation.w
            );

            output.store(
                    "roll_speed",
                    Codec.FLOAT,
                    attachment.rollSpeed
            );

            output.store(
                    "net_force",
                    VEC3_CODEC,
                    attachment.netForce
            );

            output.store(
                    "engine_force",
                    VEC3_CODEC,
                    attachment.engineForce
            );

            output.store(
                    "drag_force",
                    VEC3_CODEC,
                    attachment.dragForce
            );

            output.store(
                    "gravity_force",
                    VEC3_CODEC,
                    attachment.gravityForce
            );

            return true;
        }
    }


}
