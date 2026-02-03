package com.minagic.minagic.utilities;

import com.minagic.minagic.Minagic;
import net.minecraft.client.Minecraft;

public final class Profiler {
    public static long start() {
        return System.nanoTime();
    }

    public static void end(String job, long startTime) {
        long dt = System.nanoTime() - startTime;
        Minagic.LOGGER.debug("{} took {} nano seconds, one tick is 50000000 ns, this took {}% of a tick", job, dt, dt/500000);

    }


}
