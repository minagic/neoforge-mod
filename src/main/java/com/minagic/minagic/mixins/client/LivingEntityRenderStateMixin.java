package com.minagic.minagic.mixins.client;

import com.minagic.interfaces.MinagicLivingRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements MinagicLivingRenderState {
    @Unique
    private boolean minagic$insideArcaneShip;

    @Override
    public boolean minagic$isInsideArcaneShip() {
        return this.minagic$insideArcaneShip;
    }

    @Override
    public void minagic$setInsideArcaneShip(boolean state) {
        this.minagic$insideArcaneShip = state;
    }
}
