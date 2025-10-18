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
            DamageTypes.INJURY,
            DamageTypes.PSYCHIC,
            DamageTypes.ETHEREAL,
            DamageTypes.EXECUTION,
            DamageTypes.UNBLOCKABLE
    );

    private static final int DELAY_TICKS = 40;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("testmindamagefull")
                .requires(cs -> cs.hasPermission(2))
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    MinecraftServer server = ctx.getSource().getServer();

                    int tickCounter = 0;

                    for (ResourceKey<DamageType> key : TEST_DAMAGE_TYPES) {
                        // 1. Regular
                        int regularDelay = tickCounter * DELAY_TICKS;

                        MinagicTaskScheduler.schedule(server, regularDelay, () -> {
                            if (!player.isAlive()) return;
                            player.setHealth(player.getMaxHealth());

                            MinagicDamage dmg = new MinagicDamage(player, player, player,.0f, Set.of(key));
                            dmg.hurt((ServerLevel) player.level());
                            player.sendSystemMessage(Component.literal("Applied: " + key.location()));
                        });

                        tickCounter++;

                        // 2. Armor-piercing version
                        int armorPiercingDelay = tickCounter * DELAY_TICKS;
                        MinagicTaskScheduler.schedule(server, armorPiercingDelay, () -> {
                            if (!player.isAlive()) return;
                            player.setHealth(player.getMaxHealth());

                            MinagicDamage dmg = new MinagicDamage(player, player, player,1.0f, Set.of(key, DamageTypes.ARMOR_PIERCING));
                            dmg.hurt((ServerLevel) player.level());
                            player.sendSystemMessage(Component.literal("Applied: " + key.location() + " + ARMOR_PIERCING"));
                        });

                        tickCounter++;
                    }

                    ctx.getSource().sendSuccess(() -> Component.literal("Starting extended Minagic damage test..."), false);
                    return 1;
                }));
    }
}