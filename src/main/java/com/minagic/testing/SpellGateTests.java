package com.minagic.testing;


import com.minagic.minagic.capabilities.ManaAttachement;
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

        ManaAttachement manaAttachement = fakePlayer.getData(ModAttachments.MANA);
        manaAttachement.drainMana(manaAttachement.getMana());
        fakePlayer.setData(ModAttachments.MANA, manaAttachement);

        SpellCastContext ctx = new SpellCastContext(fakePlayer);

        new SpellGateChain()
                .addGate(new DefaultGates.ManaGate(10, new SolarSurge()))
                .setEffect(
                        (context, simData) ->
                        {
                            helper.fail("Effect should not have run due to insufficient manaAttachement, worked instead");
                        }
                )
                .execute(ctx, null);

        List<String> warnings = TestingUtils.getUserWarnings(fakePlayer);
        helper.assertTrue(warnings.contains("Not enough manaAttachement to cast Solar Surge."), Component.literal("Message in hud alerts should state: " +
                "'Not enough manaAttachement to cast Solar Surge.', got "+warnings+" instead"));

        // restore manaAttachement


        ManaAttachement data = fakePlayer.getData(ModAttachments.MANA);
        data.setMaxMana(200);
        data.restoreMana(data.getMaxMana());

        fakePlayer.setData(ModAttachments.MANA, data);
        helper.runAfterDelay(1,
                () -> {
                    ManaAttachement data2 = fakePlayer.getData(ModAttachments.MANA);

                    helper.assertTrue(manaAttachement.getMana() == 200, Component.nullToEmpty("ManaAttachement not initialized!"));

                    com.minagic.minagic.spellCasting.SpellCastContext context = new SpellCastContext(fakePlayer);
                    new SpellGateChain()
                            .addGate(new DefaultGates.ManaGate(10, new SolarSurge()))
                            .setEffect(
                                    (context1, simData) ->
                                    {
                                        helper.succeed();
                                    }
                            )
                            .execute(context, null);
                    data2 = fakePlayer.getData(ModAttachments.MANA);

                    helper.assertTrue(manaAttachement.getMana() == 190, Component.nullToEmpty("ManaAttachement should have been spent resulting in 190 manaAttachement, got " + manaAttachement.getMana() + " instead"));
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
                        {
                            flag.set(true);
                        }
                )
                .execute(ctx, null);

        helper.assertTrue(flag.get(), Component.literal("SpellGate effect should have worked without cooldown, failed instead"));
        ctx = new SpellCastContext(fakePlayer);
        new SpellGateChain()
                .addGate(new DefaultGates.CooldownGate( new SolarSurge(), 20))
                .setEffect(
                        (context, simData) ->
                        {
                            helper.fail("SpellGate effect should have failed with cooldown, worked instead");
                        }
                )
                .execute(ctx, null);

        List<String> warnings = TestingUtils.getUserWarnings(fakePlayer);
        helper.assertTrue(warnings.contains("Spell is on cooldown!"), Component.literal("Message in hud alerts should state: " +
                "'Spell is on cooldown!', got "+warnings+" instead"));

        helper.succeed();




    }


}
