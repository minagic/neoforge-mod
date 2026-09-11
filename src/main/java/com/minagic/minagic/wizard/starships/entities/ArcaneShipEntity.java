package com.minagic.minagic.wizard.starships.entities;

import com.minagic.minagic.DamageTypes;
import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertAttachment;
import com.minagic.minagic.capabilities.powersource.ActivePowerSourceAttachment;
import com.minagic.minagic.client.input.ClientShipInputHandler;
import com.minagic.minagic.common.registry.ModEntityTypes;
import com.minagic.minagic.wizard.starships.entities.models.wizard_fighter;
import com.minagic.minagic.wizard.starships.rendering.OrdnanceRenderer;
import com.minagic.minagic.wizard.starships.utilities.*;
import com.minagic.minagic.wizard.starships.utilities.weapons.targeting.TargetingComputer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Math;
import java.util.*;

public class ArcaneShipEntity extends LivingEntity {
    private static final Logger log = LoggerFactory.getLogger(ArcaneShipEntity.class);
    public ShipState state = ShipState.DEFAULT();
    public OrdnanceState ordnance = new OrdnanceState(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "ordnance_mk1_missile_rack"), 3, 0f, new Vector3f(0, 0, 0), "none");
    public final TargetingComputer targetingComputer = OrdnanceRegistry.get(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "ordnance_mk1_missile_rack")).computerProvider().getComputer();

    public ShipAITelemetry telemetry = new ShipAITelemetry();
    private ShipAI ai = new ShipAI();
    private boolean override;

    public void overrideControls(){
        this.override = true;
    }
    float K = 0.02f;

    public Vector3f getCockpitOffset(){
        return new Vector3f(0, 1f, 0);
    }
    protected List<DragSurface> getDragProfile(){
        return  List.of(
                new DragSurface(new Vec3(0, 0, 1), 3f, K),
                new DragSurface(new Vec3(0, 0, -1), 3f, K),
                new DragSurface(new Vec3(0, 1, 0), 18f, K),
                new DragSurface(new Vec3(0, -1, 0), 18f, K),
                new DragSurface(new Vec3(1, 0, 0), 6f, K),
                new DragSurface(new Vec3(-1, 0, 0), 6f, K)
        );
    }


    public static final EntityDataAccessor<ShipState> SHIP_STATE =
            SynchedEntityData.defineId(
                    ArcaneShipEntity.class,
                    ShipState.ENTITY_DATA_SERIALIZER
            );
    public static final EntityDataAccessor<OrdnanceState> ORDNANCE_STATE =
            SynchedEntityData.defineId(
                    ArcaneShipEntity.class,
                    OrdnanceState.ENTITY_DATA_SERIALIZER
            );

    public ArcaneShipEntity(EntityType<? extends ArcaneShipEntity> entity, Level level) {
        super(entity, level);
        Minagic.LOGGER.info(

                "Ship runtime type: id={}, identity={}",

                BuiltInRegistries.ENTITY_TYPE.getKey(entity),

                System.identityHashCode(entity)

        );
        this.setNoGravity(true);
        ActivePowerSourceAttachment.activate(this, ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "power_source_ship"));
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public static AttributeSupplier.Builder createAttributes() {

        return LivingEntity.createLivingAttributes()

                .add(Attributes.MAX_HEALTH, 10D)

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
            ActivePowerSourceAttachment.getActivePowerSource(player).deactivate();
            ActivePowerSourceAttachment.activate(player, ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "power_source_wizardry"));
        }

        return InteractionResult.SUCCESS;
    }




    @Override
    protected boolean canAddPassenger(@NotNull Entity passenger) {
        return getPassengers().isEmpty();
    }

    public LivingEntity getPilot(){
        Entity passenger = getFirstPassenger();
        if (passenger instanceof LivingEntity livingEntity){
            return livingEntity;
        }
        return null;
    }
    public void acceptInputs(ClientShipInputHandler.ShipInput input) {

        state = state.acceptShipInput(input);
        entityData.set(SHIP_STATE, state);
    }


    public ShipState getState(){
        return entityData.get(SHIP_STATE);
    }


    @Override
    public void tick() {
        long t0 = System.nanoTime();
        float impactVelocity = (float) this.getDeltaMovement().y;
        boolean wasOnGround = this.onGround();
        setNoGravity(true);

        super.tick();
        long t05 = System.nanoTime();
        if (level().isClientSide()) {return;}
        if (getPilot() == null && this.tickCount > 40) {
            ai.doShipState(this);
        }
        long t1 = System.nanoTime();
        state = state.parseShipInput(this);


        LivingEntity pilot = getPilot();

        long t2 = System.nanoTime();
        ShipPhysics.tickTranslational(this);
        long t3 = System.nanoTime();
        state = ShipPhysics.tickRotational(this);
        long t4 = System.nanoTime();
        entityData.set(
                SHIP_STATE,
                state
        );
        long t45 = System.nanoTime();
        this.telemetry.GPWS = checkGPWS();
        long t5 = System.nanoTime();
        if (pilot != null) {
//            FlightControls.setCraftOrientation(
//                    pilot,
//                    new Quaternionf(state.orientation())
//            );
            pilot.setXRot(0);
            pilot.setYRot(0);
            pilot.fallDistance = 0d;
            if(this.telemetry.GPWS){
                HudAlertAttachment.addToEntity(pilot, "Terrain, Pull Up", 0xFFFF0000, 1, 30);
            }
        }

        long t6 = System.nanoTime();



       if (wasOnGround){applyLandingDamage(impactVelocity);}
       this.targetingComputer.acquireTarget(this);
       this.ordnance = ordnance.tickProgress(this);
       this.ordnance = ordnance
               .setLockOnPosition(this.targetingComputer.lockOnPosition(this.level()))
               .setLockOnDescription(targetingComputer.lockOnDescription(this.level()));
       entityData.set(ORDNANCE_STATE, ordnance);
       long t7 = System.nanoTime();



       if (t7-t0 > 10000000) {
           Minagic.LOGGER.info("Ticking ship entity {} took {} ns", this.debugIdentity(), t7 - t0);
           Minagic.LOGGER.info("Breakdown: supertick + cheap stuff: {}, ai: {}, input: {}, translational: {}, rotational: {}, data write: {}, gpws: {}, pilot modifications: {}, ordnance and fall damage: {}", t05 - t0, t1-t05, t2 - t1, t3 - t2, t4 - t3, t45-t4, t5 - t45, t6 - t5, t7 - t6);
       }
    }

    public boolean checkGPWS(){
        if( this.level().isClientSide())return false;
        Vec3 start = position();
        float predictionTicks = 25.0f;
        float maxlen = 96f;
        Vec3 course = getDeltaMovement().scale(predictionTicks);
        if (course.lengthSqr() > Math.pow(maxlen, 2)){
            course = course.normalize().scale(maxlen);
        }
        Vec3 end = start.add(course);

        //long t0 = System.nanoTime();

        HitResult hit = level().clip(
                new ClipContext(
                        start,
                        end,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.SOURCE_ONLY,
                        this
                )
        );

//        long elapsed = System.nanoTime() - t0;
//
//        double travelled = hit.getType() == HitResult.Type.BLOCK
//                ? start.distanceTo(hit.getLocation())
//                : course.length();

//        Minagic.LOGGER.info(
//                "GPWS {} ms ray={} travelled={} hit={} start={} end={}",
//                elapsed / 1e6,
//                course.length(),
//                travelled,
//                hit.getType(),
//                start,
//                end
//        );
        return hit.getType() == HitResult.Type.BLOCK;

    }
    private static final double SAFE_LANDING_SPEED = 0.25;

    private static final double DAMAGE_PER_EXCESS_SPEED = 20.0;

    private void applyLandingDamage(double verticalVelocity) {

        double downwardSpeed = Math.max(0.0, -verticalVelocity);

        double excessSpeed = downwardSpeed - SAFE_LANDING_SPEED;

        if (excessSpeed <= 0.0) {

            return;

        }

        float damage = (float) (
                excessSpeed * DAMAGE_PER_EXCESS_SPEED
        );
        hurtServer(

                (ServerLevel) this.level(),
                damageSources().fall(),
                damage
        );

    }

    @Override

    public void travel(@NotNull Vec3 ignoredInput) {

        this.move(MoverType.SELF, getDeltaMovement());

    }


    @Override
    protected void defineSynchedData(
            SynchedEntityData.Builder builder
    ) {
        super.defineSynchedData(builder);

        builder.define(SHIP_STATE, ShipState.DEFAULT());
        builder.define(ORDNANCE_STATE, OrdnanceState.DEFAULT());

    }

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

    public PhysicsData getPhysicsData() {
        return new PhysicsData(
                getDragProfile(),
                new Vec3(Math.toRadians(1f), Math.toRadians(1f), Math.toRadians(2f)),
                0.7f,
                2F,
                1/240F,
                createEngines()
        );
    }

    public PrimaryData getPrimaryData(){
        return new PrimaryData(
                List.of(new Vec3(1.3, 0, 3.2),new Vec3(-1.3, 0, 3.2), new Vec3(0, 0, 3.2)),
                List.of(new Vec3(0, 0, 1), new Vec3(0, 0, 1), new Vec3(0, 0, 1)),
                2,
                1,
                new MK1Bullet(ModEntityTypes.MK1_BULLET.get(), this.level())
        );
    }

    public OrdnanceState getOrdnance(){
        return ordnance;
    }

    public static class MK1Bullet extends ArcaneShipProjectile implements ItemSupplier {

        public MK1Bullet(EntityType<? extends ArcaneShipProjectile> type, Level level) {
            super(type, level);
        }

        @Override
        public ArcaneShipProjectile create(Level level, Vec3 pos, Vec3 dir, UUID sourceUUID, UUID shipUUID, TargetingComputer computer) {
            MK1Bullet bullet = new MK1Bullet(ModEntityTypes.MK1_BULLET.get(), level);
            bullet.baseDmg = 10;
            bullet.shipUUID = shipUUID;
            bullet.sourceUUID = sourceUUID;
            bullet.direction = dir;
            bullet.speed = 5;
            bullet.gravity = 0;
            bullet.setPos(pos);
            bullet.tags = Set.of(DamageTypes.MAGIC, DamageTypes.INJURY, DamageTypes.ETHEREAL);
            level.addFreshEntity(bullet);
            return bullet;
        }

        @Override
        public ItemStack getItem() {
            return new ItemStack(Items.PRISMARINE_SHARD);
        }
    }

    protected static Map<EngineDirection, EngineSystem> createEngines() {
        EnumMap<EngineDirection, EngineSystem> engines =
                new EnumMap<>(EngineDirection.class);

        engines.put(
                EngineDirection.FORWARD,
                new EngineSystem(0.5F, 8.0F)
        );

        engines.put(
                EngineDirection.BACKWARD,
                new EngineSystem(0.45F, 5.0F)
        );

        engines.put(
                EngineDirection.RIGHT,
                new EngineSystem(0.55F, 5.0F)
        );

        engines.put(
                EngineDirection.LEFT,
                new EngineSystem(0.55F, 5.0F)
        );

        engines.put(
                EngineDirection.UP,
                new EngineSystem(0.4F, 7.0F)
        );

        engines.put(
                EngineDirection.DOWN,
                new EngineSystem(0.2F, 4.0F)
        );

        return engines;
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
        private static EntityModel<State> model;

        public Renderer(EntityRendererProvider.Context context) {
            super(context);
            this.model = new wizard_fighter<State>(context.bakeLayer(wizard_fighter.LAYER_LOCATION));
            Minagic.LOGGER.info("Constructed ArcaneShip renderer");
        }

        // =========================
        // RENDER STATE
        // =========================

        public static class State extends EntityRenderState {
            public Quaternionf orientation = new Quaternionf();
            public OrdnanceState ordnanceState = OrdnanceState.DEFAULT();
            public int packedLight;

            public State(){
                this.entityType = ModEntityTypes.ARCANE_SHIP.get();
            }
            public static State extract(@NotNull ArcaneShipEntity ship){
                State state = new State();
                state.orientation.set(ship.getEntityData().get(SHIP_STATE).orientation());
                state.ordnanceState = ship.getEntityData().get(ORDNANCE_STATE).copy();
                return state;
            }

        }



        @Override
        public @NotNull State createRenderState() {
            return new State();
        }
        private static final float DEBUG_AXIS_LENGTH = 4.0F;


        private void addLine(
                VertexConsumer consumer,
                Matrix4f matrix,

                float x1, float y1, float z1,
                float x2, float y2, float z2,

                int r, int g, int b, int a
        ) {
            consumer.addVertex(matrix, x1, y1, z1)
                    .setColor(r, g, b, a)
                    .setNormal(0, 1, 0);

            consumer.addVertex(matrix, x2, y2, z2)
                    .setColor(r, g, b, a)
                    .setNormal(0, 1, 0);
        }

        @Override
        public void extractRenderState(
                @NotNull ArcaneShipEntity entity,
                @NotNull State state,
                float partialTick
        ) {
            super.extractRenderState(entity, state, partialTick);
            state.orientation.set(entity.getEntityData().get(SHIP_STATE).orientation());
            state.ordnanceState = entity.getEntityData().get(ORDNANCE_STATE).copy();
            state.packedLight = getPackedLightCoords(

                    entity,

                    partialTick

            );
           // Minagic.LOGGER.info("Entity: {}, quaternion: {}", entity.debugIdentity(), entity.getEntityData().get(ORIENTATION));


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
            long t0 = System.nanoTime();
            ResourceLocation FIGHTER_TEXTURE = ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "textures/entity/arcane_ship_fighter.png");
            poseStack.pushPose();
            poseStack.mulPose(state.orientation);

            collector.submitModel(model, state, poseStack, RenderType.entityTranslucent(FIGHTER_TEXTURE), state.packedLight,

                    OverlayTexture.NO_OVERLAY,

                    0x00000000,

                    null);

            renderOrdnance(poseStack, collector, state);

            poseStack.popPose();
            if (Minecraft.getInstance().getFps() < 20) {
                Minagic.LOGGER.info("Rendering ship took {} ns, current FPS is measured at {}", System.nanoTime() - t0, Minecraft.getInstance().getFps());
            }
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

            //this.renderAxes(pose, consumer);

        }

        // Ordnance
        private void renderOrdnance(PoseStack pose, SubmitNodeCollector collector, State state){
            OrdnanceState ordnanceState = state.ordnanceState;
            Ordnance ordnance = OrdnanceRegistry.get(ordnanceState.ordnanceID());
            OrdnanceRenderer renderer = ordnance.renderer();
            int buildIndex = ordnance.maxStock() - ordnanceState.available() - 1;
            for(int index =0; index < ordnance.maxStock(); index++){
                OrdnanceRenderer.MountRenderState renderState;
                if (index>buildIndex) {
                    renderState = OrdnanceRenderer.MountRenderState.READY;
                } else if (index == buildIndex && buildIndex < ordnance.maxStock()) {
                    renderState = OrdnanceRenderer.MountRenderState.BUILDING;
                }
                else {
                    renderState = OrdnanceRenderer.MountRenderState.EMPTY;
                }
                renderer.render(pose, collector, ordnance.localPosition().get(index), ordnance.localDirection().get(index), renderState);
            }

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

    public String debugIdentity() {

        return String.format(

                "id=%d uuid=%s tick=%d object=%d",

                getId(),

                getUUID(),

                tickCount,

                System.identityHashCode(this)

        );

    }
}
