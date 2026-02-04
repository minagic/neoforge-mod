package com.minagic.minagic.utilities;

import com.minagic.minagic.Minagic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EntityFreezer {

    // Tracks who is frozen and when they were last updated.
    private final Map<Entity, Entry> frozen = new HashMap<>();

    // Call this whenever the radar sweeps an entity on this tick.
    public void freeze(Entity entity, ServerLevel level) {
        long tick = level.getGameTime();

        Entry entry = frozen.get(entity);
        if (entry == null) {

            entry = new Entry(entity.getDeltaMovement(), tick, entity.position());
            frozen.put(entity, entry);
        } else {
            entry.lastSeenTick = tick; // refresh heartbeat

        }

        freezeArrow(entity, entry);


    }

    public Entry getStorageEntry(Entity entity) {
        return frozen.get(entity);
    }

    // Call once per tick from ANY stable location (your field block, a global tick handler, etc.)
    // This checks for entities that haven't been "seen" recently.
    @SubscribeEvent
    public void tick(LevelTickEvent.Pre event) {
        if (event.getLevel().isClientSide()) return;
        ServerLevel level = (ServerLevel) event.getLevel();
        long tick = level.getGameTime();
        long timeout = 1; // ticks without contact before unfreezing

        Iterator<Map.Entry<Entity, Entry>> it = frozen.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Entity, Entry> pair = it.next();
            Entity entity = pair.getKey();
            Entry entry = pair.getValue();

            if (entity.isRemoved() || tick - entry.lastSeenTick > timeout) {
                // Restore momentum
                Minagic.LOGGER.debug("Restoring frozen entity momentum: {}", entry.momentum);
                entity.setDeltaMovement(entry.momentum);
                entity.setPos(entry.originalPosition);
                entity.setNoGravity(false);
                it.remove();

            }
        }
    }

    private void freezeGeneric(Entity e, Entry entry) {
        e.setDeltaMovement(Vec3.ZERO);
        e.setNoGravity(true);
    }


    // --------------- internal freeze logic ---------------

    private void freezeArrow(Entity e, Entry entry) {
        e.setNoGravity(true);
        Vec3 dir = entry.originalMotion.normalize();
        e.setDeltaMovement(dir.scale(0.001));
        e.setPos(entry.originalPosition);
        e.hurtMarked = true;
    }

    public static class Entry {
        final Vec3 originalMotion;
        long lastSeenTick;
        final Vec3 originalPosition;
        Vec3 momentum;

        Entry(Vec3 originalMotion, long lastSeenTick, Vec3 originalPosition) {
            this.originalMotion = originalMotion;
            this.lastSeenTick = lastSeenTick;
            this.originalPosition = originalPosition;
            this.momentum = originalMotion;
        }

        public void add() {
            momentum = momentum.add(originalMotion);
        }

        public Vec3 getOriginalMotion() {
            return originalMotion;
        }
    }


}
