package com.minagic.minagic.item;

import com.minagic.minagic.spells.SpellUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FireballWandItem extends Item {

    public FireballWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            SpellUtils.castFireball(level, player);
            // Optional: reduce durability, cooldown, or mana
        }

        return InteractionResult.SUCCESS;
    }
}