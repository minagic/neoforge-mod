package com.minagic.minagic.utilities;

// DumpSpellsCommand.java
import com.minagic.minagic.Minagic;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;

//public final class DumpSpellsCommand {
//    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
//        dispatcher.register(Commands.literal("dumpspells")
//                .requires(src -> src.hasPermission(2)) // op-only
//                .executes(ctx -> {
//                    CommandSourceStack source = ctx.getSource();
//                    ServerPlayer player = source.getPlayerOrException();
//                    MinecraftServer server = player.level().getServer();
//                    dumpSpellsToPlayer(source, server);
//                    return 1;
//                }));
//    }

//    private static void dumpSpellsToPlayer(CommandSourceStack source, MinecraftServer server) {
//        RegistryAccess access = server.registryAccess();
//
//        // Try registry-based access (recommended)
//        access.get(ModRegistries.SPELL_REGISTRY_KEY).ifPresentOrElse(reg -> {
//            StringBuilder sb = new StringBuilder();
//            sb.append("Registry '").append(ModRegistries.SPELL_REGISTRY_KEY.location()).append("' contains:\n");
//
//            // Try to list keys (ResourceLocation)
//            try {
//                for (ResourceLocation key : reg.value().keySet()) {
//                    Object val = reg.value().get(key); // might be ISpell or similar
//                    sb.append(" - ").append(key).append(" -> ").append(val == null ? "null" : val.getClass().getName()).append("\n");
//                }
//            } catch (Throwable t) {
//                // fallback: iterate by integer ids or holders if API differs
//                sb.append(" (Could not iterate with reg.keySet())\n");
////                access.get(ModRegistries.SPELL_REGISTRY_KEY).ifPresent(lookup -> {
////                    // lookup is HolderLookup.RegistryLookup<T> in some mappings
////                    lookup.().forEach(ref -> {
////                        ResourceLocation id = ref.key().location();
////                        Object val = ref.value(); // Holder / Reference wrapper depending on mappings
////                        sb.append(" - ").append(id).append(" -> ").append(val == null ? "null" : val.getClass().getName()).append("\n");
////                    });
////                });
//            }
//
//            // send message and log
//            source.sendSuccess(() -> Component.literal(sb.toString()), false);
//            Minagic.LOGGER.info(sb.toString());
//        }, () -> {
//            source.sendFailure(Component.literal("Registry not present for " + ModRegistries.SPELL_REGISTRY_KEY.location()));
//            Minagic.LOGGER.warn("Registry not present for " + ModRegistries.SPELL_REGISTRY_KEY.location());
//        });
//    }
//}