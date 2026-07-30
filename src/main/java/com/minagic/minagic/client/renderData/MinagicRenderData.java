package com.minagic.minagic.client.renderData;

import com.minagic.minagic.Minagic;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;

public final class MinagicRenderData {

    public static final ContextKey<Boolean> HIDE_PLAYER_IN_SHIP =

            new ContextKey<>(

                    ResourceLocation.fromNamespaceAndPath(

                            Minagic.MODID,
                            "renderdata_hide_player_in_ship"
                    )

            );

    private MinagicRenderData() {}

}