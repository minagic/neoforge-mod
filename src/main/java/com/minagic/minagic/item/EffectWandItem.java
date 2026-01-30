package com.minagic.minagic.item;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EffectWandItem extends Item {
    public EffectWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        Random random = new Random();

        List<Holder<MobEffect>> effects = new ArrayList<>();
        for (MobEffect e : BuiltInRegistries.MOB_EFFECT) {
            if (e != null)
                effects.add(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(e));
        }

        if (effects.isEmpty()) {
            return super.use(level, player, hand);
        }

        if (!level.isClientSide()) {
            Holder<MobEffect> effect = effects.get(random.nextInt(effects.size()));
            int durationTicks = 20 * (5 + random.nextInt(26));
            int amplifier = random.nextInt(3);
            player.addEffect(new MobEffectInstance(effect, durationTicks, amplifier));
        }

        return InteractionResult.SUCCESS;
    }
}
