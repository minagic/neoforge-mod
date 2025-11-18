package com.minagic.minagic.registries;

import com.minagic.minagic.Minagic;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, Minagic.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> VOID_PARTICLE =
            PARTICLES.register("void_particle", () -> new SimpleParticleType(true));
}