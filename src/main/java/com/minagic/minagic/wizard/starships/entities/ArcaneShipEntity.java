package com.minagic.minagic.wizard.starships.entities;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.hudAlerts.FlightControls;
import com.minagic.minagic.client.input.ClientShipInputHandler;
import com.minagic.minagic.common.registry.ModEntityTypes;
import com.minagic.minagic.utilities.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import java.lang.Math;

public class ArcaneShipEntity extends LivingEntity {
    private ClientShipInputHandler.ShipInput currentInput =
            ClientShipInputHandler.ShipInput.NONE;

    private Vec3 thrustControl = Vec3.ZERO;

    private static final Vec3 ENGINE_MAX =
            new Vec3(1.0D, 3.0D, 10.0D);

    private static final double VELOCITY_RESPONSE = 0.7D;

    private static final Vector3f LOCAL_FORWARD =
            new Vector3f(0.0F, 0.0F, 1.0F);

    private static final Vector3f WORLD_UP =
            new Vector3f(0.0F, 1.0F, 0.0F);

    private static final float MAX_AIM_STEP =
            (float) Math.toRadians(0.5F);

    private static final float VECTOR_EPSILON = 1.0E-6F;

    private static final float ROLL_ACCELERATION =
            (float) Math.toRadians(0.25F);

    private static final float MAX_ROLL_VELOCITY =
            (float) Math.toRadians(5.0F);

    private static final float ROLL_DRAG = 0.96F;
    private static final float ROLL_STOP_EPSILON = 1.0E-5F;

    /*
     * Actual flight-control state.
     *
     * The quaternion is rebuilt from these values each tick rather than
     * incrementally accumulating yaw and pitch rotations.
     */
    private final Vector3f trackedForward =
            new Vector3f(LOCAL_FORWARD);

    private float rollAngle;
    private float rollVelocity;

    private boolean updateTarget = true;

    /*
     * Final orientation used by movement, synchronization and rendering.
     */
    private final Quaternionf orientation = new Quaternionf();


    public static final EntityDataAccessor<Quaternionf> ORIENTATION =
            SynchedEntityData.defineId(
                    ArcaneShipEntity.class,
                    EntityDataSerializers.QUATERNION
            );

    public ArcaneShipEntity(EntityType<? extends ArcaneShipEntity> entity, Level level) {
        super(entity, level);
        Minagic.LOGGER.info(

                "Ship runtime type: id={}, identity={}",

                BuiltInRegistries.ENTITY_TYPE.getKey(entity),

                System.identityHashCode(entity)

        );
        this.setNoGravity(true);
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public static AttributeSupplier.Builder createAttributes() {

        return LivingEntity.createLivingAttributes()

                .add(Attributes.MAX_HEALTH, 100.0D)

                .add(Attributes.MOVEMENT_SPEED, 0.0D)

                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);

    }

    @Override
    public @NotNull InteractionResult interact(
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        if (!level().isClientSide()) {
            player.startRiding(this);
        }

        return InteractionResult.SUCCESS;
    }


    @Override
    protected boolean canAddPassenger(@NotNull Entity passenger) {
        return getPassengers().isEmpty();
    }

    public LivingEntity getPilot(){
        Entity passanger = getFirstPassenger();
        if (passanger instanceof LivingEntity livingEntity){
            return livingEntity;
        }
        return null;
    }
    public void acceptInputs(ClientShipInputHandler.ShipInput input) {
        currentInput = input;
        updateTarget = input.pitch > 0.5F;
    }
    @Override
    public void tick() {
        setNoGravity(true);
        super.tick();

        if (level().isClientSide()) {
            return;
        }

        thrustControl = new Vec3(
                Mth.clamp(
                        thrustControl.x + currentInput.strafe * 0.01D,
                        -1.0D,
                        1.0D
                ),
                Mth.clamp(
                        thrustControl.y + currentInput.vertical * 0.01D,
                        -1.0D,
                        1.0D
                ),
                Mth.clamp(
                        thrustControl.z + currentInput.forward * 0.01D,
                        -1.0D,
                        1.0D
                )
        );

        tickRollInput();

        LivingEntity pilot = getPilot();

        if (pilot != null && updateTarget) {
            trackEyesight(pilot.getLookAngle());
        }

        /*
         * Orientation is an output of trackedForward + rollAngle.
         * It is not incrementally yawed and pitched.
         */
        rebuildOrientation();

        tickShipMovement();

        /*
         * Always store a new Quaternionf so SynchedEntityData detects and
         * transmits the changed value.
         */
        entityData.set(
                ORIENTATION,
                new Quaternionf(orientation)
        );

        if (pilot != null) {
            FlightControls.setCraftOrientation(
                    pilot,
                    new Quaternionf(orientation)
            );
        }
    }
    public void tickShipMovement() {
        Vec3 thrust = MathUtils.hadamard(
                thrustControl,
                ENGINE_MAX
        );

        Quaternionf normalizedOrientation =
                new Quaternionf(orientation).normalize();

        Vector3f rightF = normalizedOrientation.transform(
                new Vector3f(1.0F, 0.0F, 0.0F)
        );

        Vector3f upF = normalizedOrientation.transform(
                new Vector3f(0.0F, 1.0F, 0.0F)
        );

        Vector3f forwardF = normalizedOrientation.transform(
                new Vector3f(LOCAL_FORWARD)
        );

        Vec3 shipRight =
                new Vec3(rightF.x, rightF.y, rightF.z);

        Vec3 shipUp =
                new Vec3(upF.x, upF.y, upF.z);

        Vec3 shipForward =
                new Vec3(forwardF.x, forwardF.y, forwardF.z);

        Vec3 worldThrust = shipRight.scale(thrust.x)
                .add(shipUp.scale(thrust.y))
                .add(shipForward.scale(thrust.z));

        Vec3 nextVelocity = getDeltaMovement().lerp(
                worldThrust,
                VELOCITY_RESPONSE
        );

        setDeltaMovement(nextVelocity);
    }

    private static final float IMPROVEMENT_EPSILON = 1.0E-6F;

    /*
     * Used only for the exactly-180-degree case, where left and right
     * are mathematically equally valid.
     */
    private void trackEyesight(Vec3 lookDirection) {


        Vector3f target = lookDirection.toVector3f();

        if (target.lengthSquared() < VECTOR_EPSILON) {
            return;
        }

        target.normalize();

        Vector3f shipRight = new Quaternionf(orientation)
                .transform(new Vector3f(1.0F, 0.0F, 0.0F))
                .normalize();

        Vector3f shipUp = new Quaternionf(orientation)
                .transform(new Vector3f(0.0F, 1.0F, 0.0F))
                .normalize();

        Vector3f shipForward = new Quaternionf(orientation)
                .transform(new Vector3f(0.0F, 0.0F, 1.0F))
                .normalize();

        Minagic.LOGGER.info("Roll: {}, Pitch Axis: {}, Yaw Axis: {}, Roll Axis: {}", rollAngle, shipRight, shipUp, shipForward);

        /*
         * Signed angular errors in the craft's rolled frame.
         *
         * Positive/negative signs may need inversion to match your mouse
         * convention, but these are the correct physical axes.
         */
        float yawError = (float) Math.atan2(
                target.dot(shipRight),
                target.dot(shipForward)
        );

        float pitchError = (float) Math.atan2(
                target.dot(shipUp),
                target.dot(shipForward)
        );

        float yawStep = Mth.clamp(
                yawError,
                -MAX_AIM_STEP,
                MAX_AIM_STEP
        );

        float pitchStep = Mth.clamp(
                -pitchError,
                -MAX_AIM_STEP,
                MAX_AIM_STEP
        );

        Quaternionf steeringDelta = new Quaternionf()
                .rotationAxis(yawStep, shipUp)
                .rotateAxis(pitchStep, shipRight);

        steeringDelta.transform(trackedForward);
        trackedForward.normalize();
    }

    private void tickRollInput() {
        float input = Mth.clamp(
                currentInput.roll,
                -1.0F,
                1.0F
        );

        if (Math.abs(input) > 0.001F) {
            rollVelocity += input * ROLL_ACCELERATION;
        } else {
            rollVelocity *= ROLL_DRAG;

            if (Math.abs(rollVelocity) < ROLL_STOP_EPSILON) {
                rollVelocity = 0.0F;
            }
        }

        rollVelocity = Mth.clamp(
                rollVelocity,
                -MAX_ROLL_VELOCITY,
                MAX_ROLL_VELOCITY
        );

        rollAngle = wrapRadians(
                rollAngle + rollVelocity
        );
    }

    private static float wrapRadians(float angle) {
        return Mth.wrapDegrees(
                (float) Math.toDegrees(angle)
        ) * Mth.DEG_TO_RAD;
    }

    private void rebuildOrientation() {
        Vector3f forward =
                new Vector3f(trackedForward).normalize();

        /*
         * Construct the zero-roll frame.
         */
        Vector3f right =
                new Vector3f(WORLD_UP).cross(forward);

        /*
         * When flying almost vertically, WORLD_UP and forward are parallel,
         * so they cannot define a right vector. Preserve the prior right
         * direction projected onto the new forward plane.
         */
        if (right.lengthSquared() < VECTOR_EPSILON) {
            right = new Quaternionf(orientation)
                    .transform(new Vector3f(1.0F, 0.0F, 0.0F));

            right.fma(
                    -right.dot(forward),
                    forward
            );

            if (right.lengthSquared() < VECTOR_EPSILON) {
                right.set(1.0F, 0.0F, 0.0F);
            }
        }

        right.normalize();

        Vector3f up = new Vector3f(forward)
                .cross(right)
                .normalize();

        Matrix3f basis = new Matrix3f()
                .setColumn(0, right)
                .setColumn(1, up)
                .setColumn(2, forward);

        Minagic.LOGGER.info("R {}", right);
        Minagic.LOGGER.info("U {}", up);
        Minagic.LOGGER.info("F {}", forward);
        Minagic.LOGGER.info("R×U {}", new Vector3f(right).cross(up));


        orientation
                .setFromNormalized(basis)
                .rotateZ(rollAngle)
                .normalize();
    }

    private enum AxisType {
        YAW,
        PITCH
    }

    private void applyBestLocalRotation(
            Vector3f target,
            AxisType axis,
            float maximumStep
    ) {
        float currentScore = forwardDot(
                orientation,
                target
        );

        Quaternionf positiveCandidate =
                new Quaternionf(orientation);

        Quaternionf negativeCandidate =
                new Quaternionf(orientation);

        switch (axis) {
            case YAW -> {
                positiveCandidate.rotateLocalY(maximumStep);
                negativeCandidate.rotateLocalY(-maximumStep);
            }

            case PITCH -> {
                positiveCandidate.rotateLocalX(maximumStep);
                negativeCandidate.rotateLocalX(-maximumStep);
            }
        }

        positiveCandidate.normalize();
        negativeCandidate.normalize();

        float positiveScore = forwardDot(
                positiveCandidate,
                target
        );

        float negativeScore = forwardDot(
                negativeCandidate,
                target
        );

        Quaternionf bestCandidate;
        float bestScore;

        if (positiveScore >= negativeScore) {
            bestCandidate = positiveCandidate;
            bestScore = positiveScore;
        } else {
            bestCandidate = negativeCandidate;
            bestScore = negativeScore;
        }

        /*
         * Never apply a rotation unless it actually improves alignment.
         *
         * dot = 1   means perfectly aligned
         * dot = -1  means exactly opposite
         */
        if (bestScore > currentScore + IMPROVEMENT_EPSILON) {
            orientation.set(bestCandidate);
        }
    }



    private static final double AIM_DEADZONE_RADIANS = Math.toRadians(0.25);
    private static float forwardDot(
            Quaternionf candidate,
            Vector3f target
    ) {
        Vector3f candidateForward =
                new Quaternionf(candidate)
                        .transform(new Vector3f(LOCAL_FORWARD))
                        .normalize();

        return candidateForward.dot(target);
    }
    @Override
    protected void defineSynchedData(
            SynchedEntityData.Builder builder
    ) {
        super.defineSynchedData(builder);

        builder.define(
                ORIENTATION,
                new Quaternionf()
        );
    }

    public Quaternionf getOrientation() {
        return new Quaternionf(
                entityData.get(ORIENTATION)
        );
    }

//    private void tickShipRotation() {
//
//        Vec3 rotThrust = MathUtils.hadamard(rotControl, ROT_MAX);
//        Minagic.LOGGER.info("Entity: {}, quaternion before {}", this.debugIdentity(), orientation);
//        orientation = orientation.rotateLocalX((float) Math.toRadians(rotThrust.x));
//        orientation = orientation.rotateLocalY((float) Math.toRadians(rotThrust.y));
//        orientation = orientation.rotateLocalZ((float) Math.toRadians(rotThrust.z));
//        orientation = orientation.normalize();
//
//        entityData.set(ORIENTATION, new Quaternionf(orientation));
//        Minagic.LOGGER.info("Entity: {}, quaternion after {}", this.debugIdentity(), orientation);
//        snapPassengersToShipRotation();
//    }

    @Override

    protected void positionRider(Entity passenger,

                                 MoveFunction moveFunction) {

        if (!hasPassenger(passenger)) {

            return;

        }

        Vec3 seatOffset = getRotatedSeatOffset();

        moveFunction.accept(

                passenger,

                getX() + seatOffset.x,

                getY() + seatOffset.y,

                getZ() + seatOffset.z

        );

    }
    private Vec3 getRotatedSeatOffset() {
        Vec3 localSeat = new Vec3(
                0.0D,  // local right
                0.6D,  // local up
                0.0D   // local forward
        );

        return localSeat
                .xRot(-getXRot() * Mth.DEG_TO_RAD)
                .yRot(-getYRot() * Mth.DEG_TO_RAD);
    }

    private void snapPassengersToShipRotation() {

        for (Entity passenger : getPassengers()) {

            passenger.setYRot(getYRot());

            passenger.setXRot(getXRot());

            passenger.setYHeadRot(getYRot());

            if (passenger instanceof LivingEntity living) {

                living.setYBodyRot(getYRot());

            }

        }

    }

    private static int clampDiscrete(float value) {

        return Float.compare(value, 0);

    }
    // DANGER ZONE
    public static class Renderer
            extends EntityRenderer<ArcaneShipEntity, Renderer.State> {

        // =========================
        // MODEL DIMENSIONS
        // =========================

        private static final float WIDTH = 3.0F;
        private static final float HEIGHT = 1.0F;
        private static final float LENGTH = 6.0F;

        public Renderer(EntityRendererProvider.Context context) {
            super(context);
            Minagic.LOGGER.info("Constructed ArcaneShip renderer");
        }

        // =========================
        // RENDER STATE
        // =========================

        public static class State extends EntityRenderState {

            public Quaternionf orientation = new Quaternionf();

        }

        @Override
        public @NotNull State createRenderState() {
            return new State();
        }

        @Override
        public void extractRenderState(
                @NotNull ArcaneShipEntity entity,
                @NotNull State state,
                float partialTick
        ) {
            super.extractRenderState(entity, state, partialTick);
            state.orientation.set(entity.getEntityData().get(ORIENTATION));
            Minagic.LOGGER.info("Entity: {}, quaternion: {}", entity.debugIdentity(), entity.getEntityData().get(ORIENTATION));


        }

        // =========================
        // RENDER
        // =========================

        @Override
        public void submit(
                State state,
                @NotNull PoseStack poseStack,
                @NotNull SubmitNodeCollector collector,
                @NotNull CameraRenderState cameraState
        ) {

            poseStack.pushPose();
            Minagic.LOGGER.info("quaternion:{}" ,state.orientation.toString());
            poseStack.mulPose(state.orientation);

            collector.submitCustomGeometry(
                    poseStack,
                    RenderType.debugQuads(),
                    this::renderShipBody
            );

            poseStack.popPose();
        }

        // =========================
        // SHIP BODY
        // =========================

        private void renderShipBody(
                PoseStack.Pose pose,
                VertexConsumer consumer
        ) {
            Matrix4f matrix = pose.pose();

            float halfWidth = WIDTH / 2.0F;
            float halfLength = LENGTH / 2.0F;

            float minX = -halfWidth;
            float maxX = halfWidth;

            /*
             * The bottom of the rectangle is placed at entity Y = 0.
             */
            float minY = 0.0F;
            float maxY = HEIGHT;

            float minZ = -halfLength;
            float maxZ = halfLength;

            /*
             * Front face.
             */
            addQuad(
                    consumer,
                    matrix,

                    minX, minY, minZ,
                    maxX, minY, minZ,
                    maxX, maxY, minZ,
                    minX, maxY, minZ,

                    0.0F, 0.0F, -1.0F,

                    110, 180, 255, 255
            );

            /*
             * Back face.
             */
            addQuad(
                    consumer,
                    matrix,

                    maxX, minY, maxZ,
                    minX, minY, maxZ,
                    minX, maxY, maxZ,
                    maxX, maxY, maxZ,

                    0.0F, 0.0F, 1.0F,

                    70, 110, 180, 255
            );

            float noseLength = 1.5F;
            float noseTipZ = maxZ + noseLength;
            float noseMidY = (minY + maxY) * 0.5F;

            /*
             * Left face.
             */
            addQuad(
                    consumer,
                    matrix,

                    minX, minY, maxZ,
                    minX, minY, minZ,
                    minX, maxY, minZ,
                    minX, maxY, maxZ,

                    -1.0F, 0.0F, 0.0F,

                    80, 140, 220, 255
            );

            /*
             * Right face.
             */
            addQuad(
                    consumer,
                    matrix,

                    maxX, minY, minZ,
                    maxX, minY, maxZ,
                    maxX, maxY, maxZ,
                    maxX, maxY, minZ,

                    1.0F, 0.0F, 0.0F,

                    80, 140, 220, 255
            );

            /*
             * Top face.
             */
            addQuad(
                    consumer,
                    matrix,

                    minX, maxY, minZ,
                    maxX, maxY, minZ,
                    maxX, maxY, maxZ,
                    minX, maxY, maxZ,

                    0.0F, 1.0F, 0.0F,

                    150, 210, 255, 255
            );

            /*
             * Bottom face.
             */
            addQuad(
                    consumer,
                    matrix,

                    minX, minY, maxZ,
                    maxX, minY, maxZ,
                    maxX, minY, minZ,
                    minX, minY, minZ,

                    0.0F, -1.0F, 0.0F,

                    45, 65, 100, 255
            );
            addQuad(
                    consumer,
                    matrix,

                    minX, minY, minZ,
                    maxX, minY, minZ,
                    0.0F, noseMidY, noseTipZ,
                    0.0F, noseMidY, noseTipZ,

                    0,0,-1,

                    255,80,80,255
            );

        }



        // =========================
        // QUAD HELPER
        // =========================

        private void addQuad(
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
                int alpha
        ) {
            addVertex(
                    consumer,
                    matrix,
                    x1,
                    y1,
                    z1,
                    normalX,
                    normalY,
                    normalZ,
                    red,
                    green,
                    blue,
                    alpha
            );

            addVertex(
                    consumer,
                    matrix,
                    x2,
                    y2,
                    z2,
                    normalX,
                    normalY,
                    normalZ,
                    red,
                    green,
                    blue,
                    alpha
            );

            addVertex(
                    consumer,
                    matrix,
                    x3,
                    y3,
                    z3,
                    normalX,
                    normalY,
                    normalZ,
                    red,
                    green,
                    blue,
                    alpha
            );

            addVertex(
                    consumer,
                    matrix,
                    x4,
                    y4,
                    z4,
                    normalX,
                    normalY,
                    normalZ,
                    red,
                    green,
                    blue,
                    alpha
            );
        }

        private void addVertex(
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
                int alpha
        ) {
            consumer.addVertex(matrix, x, y, z)
                    .setColor(red, green, blue, alpha)
                    .setNormal(normalX, normalY, normalZ);
        }

        // =========================
        // FRUSTUM
        // =========================

        @Override
        public boolean shouldRender(
                @NotNull ArcaneShipEntity entity,
                @NotNull Frustum frustum,
                double cameraX,
                double cameraY,
                double cameraZ
        ) {
            return true;
        }
    }

    private String debugIdentity() {

        return String.format(

                "id=%d uuid=%s tick=%d object=%d",

                getId(),

                getUUID(),

                tickCount,

                System.identityHashCode(this)

        );

    }
}
