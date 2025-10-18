package com.minagic.minagic;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;

import net.neoforged.neoforge.event.tick.ServerTickEvent.Pre;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MinagicTaskScheduler {
    private static final List<ScheduledTask> TASKS = new ArrayList<>();

    public static void schedule(MinecraftServer server, int delayTicks, Runnable task) {
        TASKS.add(new ScheduledTask(server.getTickCount() + delayTicks, task));
    }

    @SubscribeEvent
    public void onServerTick(Pre event) {

        int currentTick = event.getServer().getTickCount();
        Iterator<ScheduledTask> iterator = TASKS.iterator();

        while (iterator.hasNext()) {
            ScheduledTask t = iterator.next();
            if (currentTick >= t.tick) {
                t.task.run();
                iterator.remove();
            }
        }
    }

    private record ScheduledTask(int tick, Runnable task) {}
}