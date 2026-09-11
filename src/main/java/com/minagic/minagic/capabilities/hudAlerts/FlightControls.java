package com.minagic.minagic.capabilities.hudAlerts;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.AutoDetection;
import com.minagic.minagic.capabilities.powersource.AbstractPowerSource;
import com.minagic.minagic.capabilities.powersource.ActivePowerSourceAttachment;
import com.minagic.minagic.capabilities.powersource.ShipPowerSourceAttachment;
import com.minagic.minagic.client.input.ShipFlightTestRunner;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipMissileComputer;
import com.minagic.minagic.wizard.starships.utilities.Ordnance;
import com.minagic.minagic.wizard.starships.utilities.OrdnanceRegistry;
import com.minagic.minagic.wizard.starships.utilities.OrdnanceState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;


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

        Quaternionf orientation = new Quaternionf(ship2.getState().orientation()).normalize();

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

        //drawPrimaryReticles(ship, gui);
        renderOrdnanceHud(gui, ship);

        // draw acquisition reticle
        OrdnanceState ordnanceState = ship.getEntityData().get(ArcaneShipEntity.ORDNANCE_STATE);
        if (ordnanceState.lockOnPosition().lengthSquared() > VECTOR_EPSILON){
            Vec3 position = new Vec3( ordnanceState.lockOnPosition());
            Vec3 ndc = Minecraft.getInstance().gameRenderer.projectPointToScreen(position);

            int screenTargetX = Math.round(
                    (float) (
                            (ndc.x + 1.0)
                                    * 0.5
                                    * gui.guiWidth()
                    )
            );

            int screenTargetY = Math.round(
                    (float) (
                            (1.0 - ndc.y)
                                    * 0.5
                                    * gui.guiHeight()
                    )
            );


            float TICKROT = 0.25f;
            pose = gui.pose();

            pose.pushMatrix();
            pose.translate(screenTargetX, screenTargetY);
            pose.rotate(ship.tickCount*TICKROT);
            drawReticleTrillipse(gui, ordnanceState.available() > 0 ? 0xFFFF0000 : 0xFFFFFF00, 0, 0);
            pose.popMatrix();

            gui.drawString(font, ordnanceState.lockOnDescription(), screenTargetX+20, screenTargetY-20, ordnanceState.available() > 0 ? 0xFFFF0000 : 0xFFFFFF00);


        }
        float scale = 15f;
        ArcaneShipEntity.Renderer renderer = (ArcaneShipEntity.Renderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(ship);
        ArcaneShipEntity.Renderer.State state = renderer.createRenderState();
        renderer.extractRenderState(ship, state, 0);
        gui.submitEntityRenderState(state,scale,     new Vector3f(0.0f, state.boundingBoxHeight / 2.0f, 0.0f), new Quaternionf().rotationZ((float) (Math.PI)), null, 10, 100, 210, 300);

    }
    public static float trillipse(int x, int y){
        float thirdPoint = (float) (-5+5*1.75);
        return (float) (Math.sqrt((x-5)*(x-5) + (y-5)*(y-5)) + Math.sqrt((x+5)*(x+5) + (y-5)*(y-5)) + Math.sqrt((x)*(x) + (y+thirdPoint)*(y+thirdPoint)) - 20f);
    }
    private static void drawReticleTrillipse(GuiGraphics gui, int color, int x, int y){

        drawArbitrary(x,  y, 20, 20, FlightControls::trillipse, (xl, xy) -> false, gui, color);
    }
    // attempt to implement arbitrary function draw
    private static void drawArbitrary(
            int screenX,
            int screenY,
            int w,
            int h,
            BiFunction<Integer, Integer, Float> func,
            BiFunction<Integer, Integer, Boolean> restricted,
            GuiGraphics gui,
            int color
    ) {
        float[][] values = new float[w + 1][h + 1];

        for (int y = 0; y <= h; y++) {
            for (int x = 0; x <= w; x++) {
                int localX = x - w / 2;
                int localY = y - h / 2;

                values[x][y] = func.apply(localX, localY);
            }
        }

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {

                float a = values[x][y];
                float b = values[x + 1][y];
                float c = values[x][y + 1];
                float d = values[x + 1][y + 1];

                float min = Math.min(Math.min(a, b), Math.min(c, d));
                float max = Math.max(Math.max(a, b), Math.max(c, d));

                int localX = x - w / 2;
                int localY = y - h / 2;

                if (min <= 0 && max >= 0 &&
                        !restricted.apply(localX, localY)) {

                    gui.fill(
                            screenX + localX,
                            screenY + localY,
                            screenX + localX + 1,
                            screenY + localY + 1,
                            color
                    );
                }
            }
        }
    }

    private static void drawTriangleAtPosition(GuiGraphics gui, int x, int y, int color){
        float size = 6f;
        Vec2 point1 = new Vec2(x, y-size);
        Vec2 point2 = new Vec2((float) (x+size/Math.sqrt(2)), (float) (y+size/Math.sqrt(2)));
        Vec2 point3 = new Vec2((float) (x-size/Math.sqrt(2)), (float) (y+size/Math.sqrt(2)));

        drawLine(gui, (int) point1.x, (int) point1.y, (int) point2.x, (int) point2.y, color);
        drawLine(gui, (int) point2.x, (int) point2.y, (int) point3.x, (int) point3.y, color);
        drawLine(gui, (int) point3.x, (int) point3.y, (int) point1.x, (int) point1.y, color);


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

    private static final double PRIMARY_RETICLE_RANGE = 100.0;
    private static final double VECTOR_EPSILON = 1.0E-8;

    private static void drawPrimaryReticles(
            ArcaneShipEntity ship,
            GuiGraphics gui
    ) {
        List<Vec3> positions = ship.getPrimaryData().positions();
        List<Vec3> directions = ship.getPrimaryData().directions();

        int volleyCount = Math.min(
                positions.size(),
                directions.size()
        );

        if (volleyCount == 0) {
            return;
        }

        /*
         * Never mutate the quaternion stored in ShipState.
         */
        Quaternionf orientation =
                new Quaternionf(ship.getState().orientation())
                        .normalize();

        Minecraft minecraft = Minecraft.getInstance();
        Camera camera = minecraft.gameRenderer.getMainCamera();

        /*
         * This is the direction which projectPointToScreen() considers the
         * center of the visible screen.
         */
        Vector3f cameraForwardF = new Vector3f(
                0.0F,
                0.0F,
                1.0F
        );

        camera.rotation().transform(cameraForwardF);

        Vec3 cameraForward = new Vec3(cameraForwardF).normalize();
        Vec3 cameraPosition = camera.position();

        for (int i = 0; i < volleyCount; i++) {
            Vec3 localPosition = positions.get(i);
            Vec3 localDirection = directions.get(i).scale(-1);

            if (localDirection.lengthSqr() < VECTOR_EPSILON) {
                continue;
            }

            /*
             * Convert the muzzle offset from ship-local coordinates into
             * world coordinates.
             */
            Vector3f worldOffsetF =
                    orientation.transform(
                            localPosition.toVector3f(),
                            new Vector3f()
                    );

            Vec3 muzzleWorldPosition = ship.position().add(
                    worldOffsetF.x,
                    worldOffsetF.y,
                    worldOffsetF.z
            );

            /*
             * Convert the barrel direction from ship-local coordinates into
             * world coordinates.
             */
            Vector3f worldDirectionF =
                    orientation.transform(
                            localDirection.toVector3f(),
                            new Vector3f()
                    ).normalize();

            Vec3 worldDirection =
                    new Vec3(worldDirectionF);

            /*
             * Pick a point some distance along the barrel ray.
             */
            Vec3 targetPoint = muzzleWorldPosition.add(
                    worldDirection.scale(PRIMARY_RETICLE_RANGE)
            );

            /*
             * Do not draw a mirrored marker for a point behind the camera.
             */
            Vec3 cameraToTarget =
                    targetPoint.subtract(cameraPosition);

            if (cameraToTarget.dot(cameraForward) <= 0.0) {
                continue;
            }

            Vec3 ndc = minecraft.gameRenderer
                    .projectPointToScreen(targetPoint);

            if (!Double.isFinite(ndc.x)
                    || !Double.isFinite(ndc.y)
                    || !Double.isFinite(ndc.z)) {
                continue;
            }

            int screenX = Math.round(
                    (float) (
                            (ndc.x + 1.0)
                                    * 0.5
                                    * gui.guiWidth()
                    )
            );

            int screenY = Math.round(
                    (float) (
                            (1.0 - ndc.y)
                                    * 0.5
                                    * gui.guiHeight()
                    )
            );

            /*
             * Skip markers outside the visible GUI. You could clamp these
             * later if you want edge indicators.
             */
            if (screenX < 0
                    || screenX >= gui.guiWidth()
                    || screenY < 0
                    || screenY >= gui.guiHeight()) {
                continue;
            }

            drawPrimaryReticle(
                    gui,
                    screenX,
                    screenY,
                    i
            );
        }
    }
    private static void drawPrimaryReticle(
            GuiGraphics gui,
            int centerX,
            int centerY,
            int index
    ) {
        final int color = 0xFFFF5555;
        final int shadow = 0xA0000000;

        /*
         * Slightly vary the radius so overlapping gun markers remain visible.
         */
        int radius = 3 + index % 2;

        drawPrimaryReticleShape(
                gui,
                centerX + 1,
                centerY + 1,
                radius,
                shadow
        );

        drawPrimaryReticleShape(
                gui,
                centerX,
                centerY,
                radius,
                color
        );
    }

    private static void drawPrimaryReticleShape(
            GuiGraphics gui,
            int centerX,
            int centerY,
            int radius,
            int color
    ) {
        /*
         * Four disconnected ticks around the computed bore point.
         */
        gui.fill(
                centerX - radius - 2,
                centerY,
                centerX - radius,
                centerY + 1,
                color
        );

        gui.fill(
                centerX + radius + 1,
                centerY,
                centerX + radius + 3,
                centerY + 1,
                color
        );

        gui.fill(
                centerX,
                centerY - radius - 2,
                centerX + 1,
                centerY - radius,
                color
        );

        gui.fill(
                centerX,
                centerY + radius + 1,
                centerX + 1,
                centerY + radius + 3,
                color
        );
    }

    private static final int ORDNANCE_READY_COLOR       = 0xFF55FF55;

    private static final int ORDNANCE_BUILDING_COLOR    = 0xFFFFFF55;

    private static final int ORDNANCE_UNAVAILABLE_COLOR = 0xFFFF5555;

    private static final int ORDNANCE_OUTLINE_COLOR     = 0xCC000000;

    private static final int ORDNANCE_TEXT_COLOR        = 0xFFFFFFFF;

    private static final int ORDNANCE_PANEL_WIDTH  = 110;

    private static final int ORDNANCE_PANEL_HEIGHT = 72;

    private static final int MOUNT_ICON_WIDTH  = 7;

    private static final int MOUNT_ICON_HEIGHT = 11;

    public static void renderOrdnanceHud(
            GuiGraphics gui,
            ArcaneShipEntity ship
    ) {
        OrdnanceState state = ship
                .getEntityData()
                .get(ArcaneShipEntity.ORDNANCE_STATE);

        if (state == null) {
            return;
        }

        Ordnance ordnance =
                OrdnanceRegistry.get(state.ordnanceID());

        if (ordnance == null) {
            return;
        }

        List<Vec3> positions = ordnance.localPosition();

        if (positions.isEmpty()) {
            return;
        }

        int mountCount = positions.size();

        int available = Mth.clamp(
                state.available(),
                0,
                mountCount
        );

        int mountIndex = ordnance.maxStock() - state.available() -1;

        int panelLeft =
                gui.guiWidth() - ORDNANCE_PANEL_WIDTH - 8;

        int panelTop =
                gui.guiHeight() - ORDNANCE_PANEL_HEIGHT - 8;

        int panelRight =
                panelLeft + ORDNANCE_PANEL_WIDTH;

        int panelBottom =
                panelTop + ORDNANCE_PANEL_HEIGHT;

        // Dark translucent panel.
        gui.fill(
                panelLeft,
                panelTop,
                panelRight,
                panelBottom,
                0x90000000
        );

        Font font = Minecraft.getInstance().font;

        gui.drawString(
                font,
                "ORDNANCE",
                panelLeft + 5,
                panelTop + 5,
                ORDNANCE_TEXT_COLOR,
                true
        );

        gui.drawString(
                font,
                available + " / " + mountCount,
                panelRight - 30,
                panelTop + 5,
                ORDNANCE_TEXT_COLOR,
                true
        );

        /*
         * Determine the local X/Z extents so that the physical mount layout
         * can be fitted into the HUD panel.
         */
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (Vec3 position : positions) {
            minX = Math.min(minX, position.x);
            maxX = Math.max(maxX, position.x);

            minZ = Math.min(minZ, position.z);
            maxZ = Math.max(maxZ, position.z);
        }

        double width = Math.max(maxX - minX, 1.0);
        double depth = Math.max(maxZ - minZ, 1.0);

        int drawingLeft = panelLeft + 10;
        int drawingRight = panelRight - 10;
        int drawingTop = panelTop + 21;
        int drawingBottom = panelBottom - 14;

        /*
         * The next unavailable mount after all currently ready mounts is the
         * mount being fabricated.
         */
        int rebuildingIndex =
                available < mountCount
                        ? Math.floorMod(
                        mountIndex + available,
                        mountCount
                )
                        : -1;

        for (int index = 0; index < mountCount; index++) {
            Vec3 localPosition = positions.get(index);


            float normalizedZ =
                    (float) ((localPosition.z - minZ) / depth);

            float normalizedX =

                    (float) ((localPosition.x - minX) / width);

            int iconX = Math.round(

                    Mth.lerp(

                            1.0F - normalizedX,

                            drawingLeft,

                            drawingRight

                    )

            );
            /*
             * Local +Z is drawn toward the top of the HUD diagram.
             * Remove the `1.0F -` if you want +Z toward the bottom.
             */
            int iconY = Math.round(
                    Mth.lerp(
                            1.0F - normalizedZ,
                            drawingTop,
                            drawingBottom
                    )
            );

            int color;

            if (index > mountIndex) {

                color = ORDNANCE_READY_COLOR;

            } else if (index == mountIndex && mountIndex < mountCount) {

                color = ORDNANCE_BUILDING_COLOR;

            } else {

                color = ORDNANCE_UNAVAILABLE_COLOR;

            }

            drawOrdnanceMount(
                    gui,
                    iconX,
                    iconY,
                    color
            );
        }

        if (rebuildingIndex >= 0) {
            int percent = Mth.clamp(
                    Math.round(state.buildProgress() * 100.0F),
                    0,
                    100
            );

            gui.drawString(
                    font,
                    "BUILD " + percent + "%",
                    panelLeft + 5,
                    panelBottom - 10,
                    ORDNANCE_BUILDING_COLOR,
                    true
            );
        }
    }

    private static boolean isReadyMount(
            int candidateIndex,
            int mountIndex,
            int available,
            int mountCount
    ) {
        if (available <= 0) {
            return false;
        }

        if (available >= mountCount) {
            return true;
        }

        int distanceFromNextMount =
                Math.floorMod(
                        candidateIndex - mountIndex,
                        mountCount
                );

        return distanceFromNextMount < available;
    }
    private static void drawOrdnanceMount(
            GuiGraphics gui,
            int centerX,
            int centerY,
            int color
    ) {
        int halfWidth = MOUNT_ICON_WIDTH / 2;
        int halfHeight = MOUNT_ICON_HEIGHT / 2;

        int left = centerX - halfWidth;
        int right = centerX + halfWidth;
        int top = centerY - halfHeight;
        int bottom = centerY + halfHeight;

        // Outline/body shadow.
        gui.fill(
                left - 1,
                top + 2,
                right + 2,
                bottom + 1,
                ORDNANCE_OUTLINE_COLOR
        );

        // Missile body.
        gui.fill(
                left,
                top + 2,
                right + 1,
                bottom,
                color
        );

        // Pointed nose.
        gui.fill(
                centerX - 1,
                top,
                centerX + 2,
                top + 2,
                color
        );

        // Small fins.
        gui.fill(
                left - 2,
                bottom - 3,
                left,
                bottom,
                color
        );

        gui.fill(
                right + 1,
                bottom - 3,
                right + 3,
                bottom,
                color
        );
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
