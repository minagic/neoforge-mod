package com.minagic.minagic;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageType;

import java.util.List;
import java.util.Set;

public class MinagicTestCommand {
    private static final List<ResourceKey<DamageType>> TEST_DAMAGE_TYPES = List.of(
            DamageTypes.MAGIC,
            DamageTypes.ELEMENTAL,
            DamageTypes.DOT,
            DamageTypes.FIRE,
            DamageTypes.NATURAL,
            DamageTypes.POISON,
            DamageTypes.LIGHTNING,
            DamageTypes.PHYSICAL,
            DamageTypes.ARMOR_PIERCING,
            DamageTypes.INJURY,
            DamageTypes.PSYCHIC,
            DamageTypes.ETHEREAL,
            DamageTypes.EXECUTION,
            DamageTypes.UNBLOCKABLE
    );

    private static final int DELAY_TICKS = 40;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("testmindamage")
                .requires(cs -> cs.hasPermission(2))
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    MinecraftServer server = ctx.getSource().getServer();

                    for (int i = 0; i < TEST_DAMAGE_TYPES.size(); i++) {
                        ResourceKey<DamageType> key = TEST_DAMAGE_TYPES.get(i);
                        int delay = i * DELAY_TICKS;

                        MinagicTaskScheduler.schedule(server, delay, () -> {
                            if (!player.isAlive()) return;

                            player.setHealth(player.getMaxHealth());

                            MinagicDamage dmg = new MinagicDamage(player, player, 1.0f, Set.of(key));
                            dmg.hurt((ServerLevel) player.level());

                            player.sendSystemMessage(Component.literal("Applied damage type: " + key.location()));
                        });
                    }

                    ctx.getSource().sendSuccess(() -> Component.literal("Starting Minagic damage test..."), false);
                    return 1;
                }));
    }
}