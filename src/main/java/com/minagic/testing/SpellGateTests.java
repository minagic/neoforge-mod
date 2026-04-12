package com.minagic.testing;


import com.minagic.minagic.capabilities.SimulacrumData;
import com.minagic.minagic.registries.ModSpells;
import com.minagic.minagic.sorcerer.celestial.spells.SolarSurge;
import com.minagic.minagic.spellCasting.SpellCastContext;
import com.minagic.minagic.spellgates.DefaultGates;
import com.minagic.minagic.spellgates.SpellGateChain;
import com.minagic.minagic.spells.NoneSpell;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpellGateTests {

//    public static void testManaGates(GameTestHelper helper){
//        ServerLevel level = helper.getLevel();
//
//        Player fakePlayer = helper.makeMockPlayer(GameType.CREATIVE);
//        fakePlayer.setPos(1, 2, 1);
//
//        ManaAttachment.drainMana(fakePlayer, ManaAttachment.getMana(fakePlayer));
//
//        SpellCastContext ctx = new SpellCastContext(fakePlayer);
//
//        new SpellGateChain(new NoneSpell())
//                .addGate(new DefaultGates.PowerSourceSustainGate(10))
//                .setEffect(
//                        (context, simData) ->
//                                helper.fail("Effect should not have run due to insufficient mana, worked instead")
//                )
//                .execute(ctx, null);
//
//        List<String> warnings = TestingUtils.getUserWarnings(fakePlayer);
//        helper.assertTrue(warnings.contains("Not enough mana to cast Solar Surge."), Component.literal("Message in hud alerts should state: " +
//                "'Not enough mana to cast Solar Surge.', got "+warnings+" instead"));
//
//        // restore manaAttachement
//
//        ManaAttachment.setMaxMana(fakePlayer, 200);
//        ManaAttachment.restoreMana(fakePlayer, ManaAttachment.getMaxMana(fakePlayer));
//
//        helper.runAfterDelay(1,
//                () -> {
//
//                    helper.assertFalse(ManaAttachment.getMana(fakePlayer) == 0, Component.nullToEmpty("ManaAttachment not initialized!"));
//
//                    com.minagic.minagic.spellCasting.SpellCastContext context = new SpellCastContext(fakePlayer);
//                    new SpellGateChain(new NoneSpell())
//                            .addGate(new DefaultGates.PowerSourceCostGate(10, new SolarSurge()))
//                            .setEffect(
//                                    (context1, simData) ->
//                                            helper.succeed()
//                            )
//                            .execute(context, null);
//
//                    helper.assertTrue(ManaAttachment.getMana(fakePlayer) == 190, Component.nullToEmpty("Mana should have been spent resulting in 190 mana, got " + ManaAttachment.getMana(fakePlayer) + " instead"));
//                }
//        );
//
//    }

    public static void testCooldownGates(GameTestHelper helper){
        Player fakePlayer = helper.makeMockPlayer(GameType.CREATIVE);
        fakePlayer.setPos(1, 2, 1);


        SpellCastContext ctx = new SpellCastContext(fakePlayer);
        AtomicBoolean flag = new AtomicBoolean(false);

        new SpellGateChain(new NoneSpell())
                .addGate(new DefaultGates.CooldownGate(new NoneSpell(), 20))
                .setEffect(
                        (context, simData) ->
                                flag.set(true)
                )
                .execute(ctx, null);

        helper.assertTrue(flag.get(), Component.literal("SpellGate effect should have worked without cooldown, failed instead"));
        ctx = new SpellCastContext(fakePlayer);
        new SpellGateChain(new NoneSpell())
                .addGate(new DefaultGates.CooldownGate( new NoneSpell(), 20))
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

    public static void testSimulacrumGates(GameTestHelper helper){
        Player fakePlayer = helper.makeMockPlayer(GameType.CREATIVE);
        fakePlayer.setPos(1, 2, 1);

        new SpellGateChain(new NoneSpell())
                .addGate(new DefaultGates.SimulacrumGate())
                .setEffect((ctx, simulacrumData)->{helper.fail("SimulacrumGate should have failed with null simulacrumData");})
                .execute(new SpellCastContext(fakePlayer), null);

        new SpellGateChain(new NoneSpell())
                .addGate(new DefaultGates.SimulacrumGate())
                .setEffect((ctx, simulacrumData)->{helper.fail("SimulacrumGate should have failed with invalid due to 0 remaining lifetime simulacrumData");})
                .execute(new SpellCastContext(fakePlayer), new SimulacrumData(new NoneSpell().getID(), 0, 100, 1, 1, fakePlayer));

        new SpellGateChain(new NoneSpell())
                .addGate(new DefaultGates.SimulacrumGate())
                .setEffect((ctx, simulacrumData)->{helper.fail("SimulacrumGate should have failed with invalid due to lifetime above threshold simulacrumData");})
                .execute(new SpellCastContext(fakePlayer), new SimulacrumData(new NoneSpell().getID(), 1, 100, 10, 1, fakePlayer));

        new SpellGateChain(new NoneSpell())
                .addGate(new DefaultGates.SimulacrumGate())
                .setEffect((ctx, simulacrumData)->{helper.fail("SimulacrumGate should have failed with invalid due to lifetime above maxLifetime simulacrumData");})
                .execute(new SpellCastContext(fakePlayer), new SimulacrumData(new NoneSpell().getID(), 1, 100, 200, 300, fakePlayer));
        new SpellGateChain(new NoneSpell())
                .addGate(new DefaultGates.SimulacrumGate())
                .setEffect((ctx, simulacrumData)->{helper.fail("SimulacrumGate should have failed with invalid due to remainingLifetime above maxLifetime simulacrumData");})
                .execute(new SpellCastContext(fakePlayer), new SimulacrumData(new NoneSpell().getID(), 2000, 100, 10, 300, fakePlayer));

        new SpellGateChain(new NoneSpell())
                .addGate(new DefaultGates.SimulacrumGate())
                .setEffect((ctx, simulacrumData)->{helper.succeed();})
                .execute(new SpellCastContext(fakePlayer), new SimulacrumData(new NoneSpell().getID(), 1, 100, 9, 10, fakePlayer));
    }


}
