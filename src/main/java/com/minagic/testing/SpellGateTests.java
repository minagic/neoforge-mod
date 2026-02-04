package com.minagic.testing;


import com.minagic.minagic.capabilities.ManaAttachment;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.sorcerer.celestial.spells.SolarSurge;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGateChain;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpellGateTests {

    public static void testManaGates(GameTestHelper helper){
        ServerLevel level = helper.getLevel();

        Player fakePlayer = helper.makeMockPlayer(GameType.CREATIVE);
        fakePlayer.setPos(1, 2, 1);

        ManaAttachment.drainMana(fakePlayer, ManaAttachment.getMana(fakePlayer));

        SpellCastContext ctx = new SpellCastContext(fakePlayer);

        new SpellGateChain()
                .addGate(new DefaultGates.ManaGate(10, new SolarSurge()))
                .setEffect(
                        (context, simData) ->
                                helper.fail("Effect should not have run due to insufficient mana, worked instead")
                )
                .execute(ctx, null);

        List<String> warnings = TestingUtils.getUserWarnings(fakePlayer);
        helper.assertTrue(warnings.contains("Not enough mana to cast Solar Surge."), Component.literal("Message in hud alerts should state: " +
                "'Not enough mana to cast Solar Surge.', got "+warnings+" instead"));

        // restore manaAttachement

        ManaAttachment.setMaxMana(fakePlayer, 200);
        ManaAttachment.restoreMana(fakePlayer, ManaAttachment.getMaxMana(fakePlayer));

        helper.runAfterDelay(1,
                () -> {

                    helper.assertFalse(ManaAttachment.getMana(fakePlayer) == 0, Component.nullToEmpty("ManaAttachment not initialized!"));

                    com.minagic.minagic.spellCasting.SpellCastContext context = new SpellCastContext(fakePlayer);
                    new SpellGateChain()
                            .addGate(new DefaultGates.ManaGate(10, new SolarSurge()))
                            .setEffect(
                                    (context1, simData) ->
                                            helper.succeed()
                            )
                            .execute(context, null);

                    helper.assertTrue(ManaAttachment.getMana(fakePlayer) == 190, Component.nullToEmpty("Mana should have been spent resulting in 190 mana, got " + ManaAttachment.getMana(fakePlayer) + " instead"));
                }
        );

    }

    public static void testCooldownGates(GameTestHelper helper){
        Player fakePlayer = helper.makeMockPlayer(GameType.CREATIVE);
        fakePlayer.setPos(1, 2, 1);


        SpellCastContext ctx = new SpellCastContext(fakePlayer);
        AtomicBoolean flag = new AtomicBoolean(false);

        new SpellGateChain()
                .addGate(new DefaultGates.CooldownGate(new SolarSurge(), 20))
                .setEffect(
                        (context, simData) ->
                                flag.set(true)
                )
                .execute(ctx, null);

        helper.assertTrue(flag.get(), Component.literal("SpellGate effect should have worked without cooldown, failed instead"));
        ctx = new SpellCastContext(fakePlayer);
        new SpellGateChain()
                .addGate(new DefaultGates.CooldownGate( new SolarSurge(), 20))
                .setEffect(
                        (context, simData) ->
                                helper.fail("SpellGate effect should have failed with cooldown, worked instead")
                )
                .execute(ctx, null);

        List<String> warnings = TestingUtils.getUserWarnings(fakePlayer);
        helper.assertTrue(warnings.contains("Spell is on cooldown!"), Component.literal("Message in hud alerts should state: " +
                "'Spell is on cooldown!', got "+warnings+" instead"));

        helper.succeed();




    }


}
