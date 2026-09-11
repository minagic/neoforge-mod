package com.minagic.minagic.wizard.starships.utilities;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.common.registry.ModEntityTypes;
import com.minagic.minagic.wizard.starships.entities.MK1Missile;
import com.minagic.minagic.wizard.starships.rendering.DefaultOrdnanceRenderer;
import com.minagic.minagic.wizard.starships.utilities.weapons.targeting.MissileTargetingComputer;
import com.minagic.minagic.wizard.starships.utilities.weapons.targeting.NoTargetingComputer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public final class OrdnanceRegistry {

    private static final Map<ResourceLocation, Ordnance> REGISTRY =

            new HashMap<>();

    /*

     * IdentityHashMap is appropriate if ordnance definitions are singleton

     * objects and should only resolve by exact object identity.

     */

    private static final Map<Ordnance, ResourceLocation> REVERSE =

            new IdentityHashMap<>();

    private OrdnanceRegistry() {

    }

    public static void register(

            ResourceLocation id,

            Ordnance ordnance

    ) {

        Objects.requireNonNull(id, "Ordnance id cannot be null");

        Objects.requireNonNull(ordnance, "Ordnance cannot be null");

        if (REGISTRY.containsKey(id)) {

            throw new IllegalArgumentException(

                    "Ordnance with ID " + id + " is already registered"

            );

        }

        if (REVERSE.containsKey(ordnance)) {

            throw new IllegalArgumentException(

                    "Ordnance instance is already registered as "

                            + REVERSE.get(ordnance)

            );

        }

        REGISTRY.put(id, ordnance);

        REVERSE.put(ordnance, id);

        Minagic.LOGGER.debug(

                "Registered ordnance: {} -> {}",

                id,

                ordnance

        );

    }

    public static Ordnance get(ResourceLocation id) {

        return id == null ? null : REGISTRY.get(id);

    }

    public static ResourceLocation getId(Ordnance ordnance) {

        return ordnance == null ? null : REVERSE.get(ordnance);

    }

    public static Ordnance require(ResourceLocation id) {

        Ordnance ordnance = get(id);

        if (ordnance == null) {

            throw new IllegalArgumentException(

                    "Unknown ordnance ID: " + id

            );

        }

        return ordnance;

    }

    public static ResourceLocation requireId(Ordnance ordnance) {

        ResourceLocation id = getId(ordnance);

        if (id == null) {

            throw new IllegalArgumentException(

                    "Unregistered ordnance: " + ordnance

            );

        }

        return id;

    }

    public static Collection<Ordnance> values() {

        return Collections.unmodifiableCollection(

                REGISTRY.values()

        );

    }

    public static Map<ResourceLocation, Ordnance> entries() {

        return Collections.unmodifiableMap(REGISTRY);

    }

//    public static final Codec<Ordnance> CODEC =
//
//            ResourceLocation.CODEC.comapFlatMap(
//
//                    id -> {
//
//                        Ordnance ordnance = get(id);
//
//                        if (ordnance == null) {
//
//                            return DataResult.error(
//
//                                    () -> "Unknown ordnance ID: " + id
//
//                            );
//
//                        }
//
//                        return DataResult.success(ordnance);
//
//                    },
//
//                    ordnance -> {
//
//                        ResourceLocation id = getId(ordnance);
//
//                        if (id == null) {
//
//                            throw new IllegalStateException(
//
//                                    "Cannot encode unregistered ordnance: "
//
//                                            + ordnance
//
//                            );
//
//                        }
//
//                        return id;
//
//                    }
//
//            );
    public static void defaultMissiles(){
        Ordnance mk1MissileRack = new Ordnance(

                (level, position, direction, pilot, ship, targetingComputer) ->

                        new MK1Missile(

                                ModEntityTypes.MK1_MISSILE.get(),

                                level

                        ).create(

                                level,

                                position,

                                direction,

                                pilot,

                                ship,
                                targetingComputer

                        ),

                List.of( new Vec3(-1.5, -0.35, 0), new Vec3(1.5, -0.35, 0), new Vec3(0, -0.35, 1)),

                List.of(new Vec3(0.0, 0.0, 1.0), new Vec3(0.0, 0.0, 1.0), new Vec3(0.0, 0.0, 1.0)),

                250,

                3,
                5,
                new DefaultOrdnanceRenderer(),
                MissileTargetingComputer::new


        );
        register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "ordnance_mk1_missile_rack"), mk1MissileRack);
        Ordnance noOrdnance = new Ordnance(
                ((level, position, direction, sourceUUID, shipUUID, t) -> null),
                List.of(),
                List.of(),
                0, 0, 0,
                new DefaultOrdnanceRenderer(), NoTargetingComputer::new);
        register(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "ordnance_no_rack"), noOrdnance);

    }
}