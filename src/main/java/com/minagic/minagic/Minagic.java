package com.minagic.minagic;

import com.minagic.minagic.capabilities.hudAlerts.HudAlert;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertManager;
import com.minagic.minagic.gui.CooldownOverlay;
import com.minagic.minagic.packets.MinagicNetwork;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModItems;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.sorcerer.spells.VoidBlastEntity;
import com.minagic.minagic.spellCasting.ClearData;
import com.minagic.minagic.spellCasting.ManaHandler;
import com.minagic.minagic.spellCasting.PlayerSimulacraHandler;
import com.minagic.minagic.spellCasting.SpellCooldownHandler;
import com.minagic.minagic.spells.FireballEntity;
import com.minagic.minagic.registries.ModDataComponents;
import com.minagic.minagic.utilities.WorldEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.units.qual.N;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Minagic.MODID)
public class Minagic {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "minagic";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "minagic" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "minagic" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "minagic" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    // ENTITIES REGISTRATION WOULD GO HERE
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.createEntities(MODID);

    // REGISTER FIREBALL ENTITY TYPE
    public static final DeferredHolder<EntityType<?>, EntityType<FireballEntity>> FIREBALL =
            ENTITY_TYPES.register("fireball",
                    () -> EntityType.Builder.<FireballEntity>of(FireballEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F) // Size of the entity
                            .clientTrackingRange(32) // Tracking range
                            .updateInterval(1) // Update interval
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(MODID + ":fireball"))));

    // Register VOID BLAST ENTITY TYPE
    // (Example of how to register another entity type, similar to FIREBALL)
    public static final DeferredHolder<EntityType<?>, EntityType<VoidBlastEntity>> VOID_BLAST_ENTITY =
            ENTITY_TYPES.register("void_blast_entity",
                    () -> EntityType.Builder.<VoidBlastEntity>of(VoidBlastEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F) // Size of the entity
                            .clientTrackingRange(32) // Tracking range
                            .updateInterval(1) // Update interval
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(MODID + ":void_blast_entity"))));

    // Creates a creative tab with the id "minagic:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.minagic")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .displayItems((parameters, output) -> {
                output.accept(ModItems.EFFECT_WAND.get()); // Add the effect wand to the tab
            }).build());

    // Command registration method
    private void onRegisterCommands(RegisterCommandsEvent event) {
        MinagicTestCommand.register(event.getDispatcher());
    }

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Minagic(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so entity types get registered
        ENTITY_TYPES.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Minagic) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        ModSpells.register();
        // Register your tick handlers here!
        NeoForge.EVENT_BUS.register(new MinagicTaskScheduler());
        NeoForge.EVENT_BUS.register(new ManaHandler());
        NeoForge.EVENT_BUS.register(new SpellCooldownHandler());
        NeoForge.EVENT_BUS.register(new ClientInputHandler());
        NeoForge.EVENT_BUS.register(new CooldownOverlay());
        NeoForge.EVENT_BUS.register(new WorldEvents());
        NeoForge.EVENT_BUS.register(new PlayerSimulacraHandler());
        NeoForge.EVENT_BUS.register(new ClearData());
        NeoForge.EVENT_BUS.register(new HudAlertManager());
        //NeoForge.EVENT_BUS.register(new PlayerItemUsageCheck());

        ModItems.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModAttachments.register(modEventBus);




        // Register packet handlers
        MinagicNetwork network = new MinagicNetwork();
        network.register(modEventBus);

        // Register client-side mod event handlers

        modEventBus.register(new ClientModEvents());

        // Register commands (optional)
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
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

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.EFFECT_WAND);
            event.accept(ModItems.SORCERER_STAFF);
            event.accept(ModItems.WIZARD_WAND);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");

    }
}
