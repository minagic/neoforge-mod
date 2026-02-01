package com.minagic.testing;

import com.minagic.minagic.Minagic;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

import static com.minagic.minagic.Minagic.MODID;

public class TestsRegistration {

    public static final DeferredRegister<Consumer<GameTestHelper>> TEST_FUNCTION = DeferredRegister.create(
            BuiltInRegistries.TEST_FUNCTION,
            MODID
    );

    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> MANA_GATE_TEST = TEST_FUNCTION.register(
            "mana_gate_test",
            () -> SpellGateTests::testManaGates
    );

    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COOLDOWN_GATE_TEST = TEST_FUNCTION.register(
            "cooldown_gate_test",
            () -> SpellGateTests::testCooldownGates
    );


}
