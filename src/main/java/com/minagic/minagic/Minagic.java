package com.minagic.minagic;

import com.minagic.minagic.client.event.ClientEventRegistration;
import com.minagic.minagic.common.events.CommonEventRegistration;
import com.minagic.minagic.common.registry.ModRegistries;
import com.minagic.minagic.common.registry.MinagicNetwork;
import com.minagic.minagic.utilities.*;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import static com.minagic.testing.TestsRegistration.TEST_FUNCTION;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Minagic.MODID)
public class Minagic {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "minagic";


    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "minagic" namespace

    // Create a Deferred Register to hold Items which will all be registered under the "minagic" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "minagic" namespace

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Minagic(IEventBus modEventBus, ModContainer modContainer) {


        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Minagic) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        ClientEventRegistration.register(NeoForge.EVENT_BUS, modEventBus);
        CommonEventRegistration.register();
        ModRegistries.register(modEventBus);
        // Client-side mod event handlers are registered via @EventBusSubscriber in ClientModEvents.

        // Register commands (optional)


        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        //HudOverrideRegistry.register(ResourceLocation.fromNamespaceAndPath(MODID, "white_flash_primary"), WhiteFlashOverride::new, WhiteFlashOverride.WhiteFlashData.CODEC);
    }


    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));

    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");

    }
}
