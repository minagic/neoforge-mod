package com.minagic.minagic.mixins.client;

import com.minagic.minagic.Minagic;
import com.minagic.interfaces.MinagicLivingRenderState;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin  {

    @Inject(

            method = "extractRenderState",

            at = @At("TAIL")

    )

    private void minagic$hidePassengersInShip(

            LivingEntity entity,

            LivingEntityRenderState state,

            float partialTick,

            CallbackInfo ci

    ) {

        if (entity.getVehicle() instanceof ArcaneShipEntity && state instanceof MinagicLivingRenderState mstate) {

            mstate.minagic$setInsideArcaneShip(true);
            Minagic.LOGGER.info("The ship has successfully byte-manipulated your game");

        }

    }


}