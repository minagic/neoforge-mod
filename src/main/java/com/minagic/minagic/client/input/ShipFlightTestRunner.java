package com.minagic.minagic.client.input;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;

public final class ShipFlightTestRunner {

    public static TestScript activeScript;
    private static int stepIndex;
    private static int ticksInStep;
    private static boolean running;

    private ShipFlightTestRunner() {
    }

    /*
     * One fixed input held for a specified number of client ticks.
     */
    private record TestStep(
            int durationTicks,
            float pitch,
            float yaw,
            float roll
    ) {
        private ClientShipInputHandler.ShipInput createInput() {
            return new ClientShipInputHandler.ShipInput(
                    0.0F, // vertical thrust
                    0.0F, // forward thrust
                    0.0F, // strafe thrust
                    pitch,
                    yaw,
                    roll
            );
        }
    }

    public record TestScript(
            String name,
            List<TestStep> steps
    ) {
    }

    // ============================================================
    // TEST DEFINITIONS
    // ============================================================

    public static final TestScript PURE_PITCH_POSITIVE =
            new TestScript(
                    "Pure positive pitch",
                    List.of(
                            step(200, 1.0F, 0.0F, 0.0F),
                            neutral(40)
                    )
            );

    public static final TestScript PURE_PITCH_NEGATIVE =
            new TestScript(
                    "Pure negative pitch",
                    List.of(
                            step(200, -1.0F, 0.0F, 0.0F),
                            neutral(40)
                    )
            );

    public static final TestScript PURE_YAW_POSITIVE =
            new TestScript(
                    "Pure positive yaw",
                    List.of(
                            step(200, 0.0F, 1.0F, 0.0F),
                            neutral(40)
                    )
            );

    public static final TestScript PURE_YAW_NEGATIVE =
            new TestScript(
                    "Pure negative yaw",
                    List.of(
                            step(200, 0.0F, -1.0F, 0.0F),
                            neutral(40)
                    )
            );

    public static final TestScript PURE_ROLL_POSITIVE =
            new TestScript(
                    "Pure positive roll",
                    List.of(
                            step(200, 0.0F, 0.0F, 1.0F),
                            neutral(40)
                    )
            );

    public static final TestScript PURE_ROLL_NEGATIVE =
            new TestScript(
                    "Pure negative roll",
                    List.of(
                            step(200, 0.0F, 0.0F, -1.0F),
                            neutral(40)
                    )
            );

    /*
     * Adjust the roll duration until it produces approximately 90°.
     * The correct value depends on your roll acceleration and drag.
     */
    public static final TestScript ROLL_THEN_PITCH =
            new TestScript(
                    "Roll then pitch",
                    List.of(
                            step(30, 0.0F, 0.0F, 1.0F),
                            neutral(30),
                            step(100, 1.0F, 0.0F, 0.0F),
                            neutral(40)
                    )
            );

    public static final TestScript ROLL_THEN_YAW =
            new TestScript(
                    "Roll then yaw",
                    List.of(
                            step(30, 0.0F, 0.0F, 1.0F),
                            neutral(30),
                            step(100, 0.0F, 1.0F, 0.0F),
                            neutral(40)
                    )
            );

    public static final TestScript DIAGONAL_PITCH_YAW =
            new TestScript(
                    "Combined pitch and yaw",
                    List.of(
                            step(200, 1.0F, 1.0F, 0.0F),
                            neutral(40)
                    )
            );

    public static final TestScript PITCH_REVERSAL =
            new TestScript(
                    "Pitch reversal",
                    List.of(
                            step(100, 1.0F, 0.0F, 0.0F),
                            step(100, -1.0F, 0.0F, 0.0F),
                            neutral(40)
                    )
            );

    public static final TestScript FOUR_AXIS_SEQUENCE =
            new TestScript(
                    "Pitch yaw inverse sequence",
                    List.of(
                            step(80, 1.0F, 0.0F, 0.0F),
                            step(80, 0.0F, 1.0F, 0.0F),
                            step(80, -1.0F, 0.0F, 0.0F),
                            step(80, 0.0F, -1.0F, 0.0F),
                            neutral(40)
                    )
            );

    public static final TestScript FULL_SUITE =
            new TestScript(
                    "Full flight-control suite",
                    List.of(
                            neutral(40),

                            // Pitch forward and backward.
                            step(80, 1.0F, 0.0F, 0.0F),
                            neutral(30),
                            step(80, -1.0F, 0.0F, 0.0F),
                            neutral(40),

                            // Yaw both ways.
                            step(80, 0.0F, 1.0F, 0.0F),
                            neutral(30),
                            step(80, 0.0F, -1.0F, 0.0F),
                            neutral(40),

                            // Roll both ways.
                            step(50, 0.0F, 0.0F, 1.0F),
                            neutral(30),
                            step(50, 0.0F, 0.0F, -1.0F),
                            neutral(40),

                            // Composite control.
                            step(120, 1.0F, 1.0F, 0.0F),
                            neutral(40),

                            // Roll followed by pitch.
                            step(30, 0.0F, 0.0F, 1.0F),
                            neutral(20),
                            step(100, 1.0F, 0.0F, 0.0F),
                            neutral(60)
                    )
            );

    private static TestStep step(
            int ticks,
            float pitch,
            float yaw,
            float roll
    ) {
        return new TestStep(
                ticks,
                pitch,
                yaw,
                roll
        );
    }

    private static TestStep neutral(int ticks) {
        return step(
                ticks,
                0.0F,
                0.0F,
                0.0F
        );
    }

    // ============================================================
    // CONTROL
    // ============================================================

    public static void start(TestScript script) {
        activeScript = script;
        stepIndex = 0;
        ticksInStep = 0;
        running = true;

        Minagic.LOGGER.info(
                "Started flight test: {}",
                script.name()
        );
    }

    public static void stop() {
        running = false;
        activeScript = null;
        stepIndex = 0;
        ticksInStep = 0;

        sendNeutralPacket();

        Minagic.LOGGER.info("Stopped flight test");
    }

    public static boolean isRunning() {
        return running;
    }

    // ============================================================
    // TICK EXECUTION
    // ============================================================

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!running || activeScript == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null
                || !(minecraft.player.getVehicle() instanceof ArcaneShipEntity)) {
            stop();
            return;
        }

        if (stepIndex >= activeScript.steps().size()) {

            finishTest();
            return;
        }

        TestStep step = activeScript.steps().get(stepIndex);

        ClientShipInputHandler.ShipInput input =
                step.createInput();

        ClientPacketDistributor.sendToServer(
                input.createPacket()
        );

        if (ticksInStep == 0) {
            Minagic.LOGGER.info(
                    "Flight test '{}': step {}/{} — ticks={}, pitch={}, yaw={}, roll={}",
                    activeScript.name(),
                    stepIndex + 1,
                    activeScript.steps().size(),
                    step.durationTicks(),
                    step.pitch(),
                    step.yaw(),
                    step.roll()
            );
        }

        ticksInStep++;

        if (ticksInStep >= step.durationTicks()) {
            ticksInStep = 0;
            stepIndex++;
        }
    }

    private static void finishTest() {
        ClientPacketDistributor.sendToServer(
                new ClientShipInputHandler.ShipInput(
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F
                ).createPacket()
        );

        String name = activeScript.name();

        sendNeutralPacket();

        running = false;
        activeScript = null;
        stepIndex = 0;
        ticksInStep = 0;

        Minagic.LOGGER.info("Completed flight test: {}", name);

        if (runningSuite) {



            suiteIndex++;

            if (suiteIndex < ALL_TESTS.size()) {

                Minagic.LOGGER.info(
                        "--------------------------------------------------"
                );

                start(ALL_TESTS.get(suiteIndex));

                return;

            }

            runningSuite = false;

            Minagic.LOGGER.info(
                    "========== FULL FLIGHT TEST SUITE COMPLETE =========="
            );
        }


    }

    private static void sendNeutralPacket() {
        ClientPacketDistributor.sendToServer(
                new ClientShipInputHandler.ShipInput(
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F
                ).createPacket()
        );
    }

    private static final List<TestScript> ALL_TESTS = List.of(
//            PURE_PITCH_POSITIVE,
//            PURE_PITCH_NEGATIVE,
//            PURE_YAW_POSITIVE,
//            PURE_YAW_NEGATIVE,
           // PURE_ROLL_POSITIVE,
          //  PURE_ROLL_NEGATIVE,
           // ROLL_THEN_PITCH,
           // ROLL_THEN_YAW,
            DIAGONAL_PITCH_YAW,
           // PITCH_REVERSAL,
            FOUR_AXIS_SEQUENCE
    );

    private static boolean runningSuite;
    private static int suiteIndex;

    public static void runAllTests() {
        runningSuite = true;
        suiteIndex = 0;

        start(ALL_TESTS.get(0));

        Minagic.LOGGER.info(
                "========== Starting FULL FLIGHT TEST SUITE =========="
        );
    }
}
