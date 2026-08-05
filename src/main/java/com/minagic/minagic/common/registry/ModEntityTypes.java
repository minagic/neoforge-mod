package com.minagic.minagic.common.registry;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.entity.sorcerer.voidbourne.VoidborneSorcererEntity;
import com.minagic.minagic.sorcerer.celestial.mechanics.StarShard;
import com.minagic.minagic.sorcerer.celestial.spells.TracerBullet;
import com.minagic.minagic.sorcerer.celestial.spells.novaburst.NovaImpactProxyEntity;
import com.minagic.minagic.sorcerer.spells.VoidBlastEntity;
import com.minagic.minagic.spells.FireballEntity;
import com.minagic.minagic.utilities.ProjectilePortal;
import com.minagic.minagic.wizard.starships.entities.ArcaneShipEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.minagic.minagic.Minagic.MODID;

public final class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.createEntities(MODID);
    // REGISTER FIREBALL ENTITY TYPE
    public static final DeferredHolder<EntityType<?>, EntityType<FireballEntity>> FIREBALL =
            ENTITY_TYPES.register("fireball",
                    () -> EntityType.Builder.<FireballEntity>of(FireballEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F) // Size of the entity
                            .clientTrackingRange(32) // Tracking range
                            .updateInterval(1) // Update interval
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(MODID + ":fireball"))));

    // Register VOID BLAST ENTITY TYPE
    // (Example of how to register another entity type, similar to FIREBALL)
    public static final DeferredHolder<EntityType<?>, EntityType<VoidBlastEntity>> VOID_BLAST_ENTITY =
            ENTITY_TYPES.register("void_blast_entity",
                    () -> EntityType.Builder.<VoidBlastEntity>of(VoidBlastEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F) // Size of the entity
                            .clientTrackingRange(32) // Tracking range
                            .updateInterval(1) // Update interval
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(MODID + ":void_blast_entity"))));

    public static final DeferredHolder<EntityType<?>, EntityType<TracerBullet.TracerBulletProjectile>> TRACER_BULLET_PROJECTILE =
            ENTITY_TYPES.register("tracer_bullet_projectile",
                    () -> EntityType.Builder.<TracerBullet.TracerBulletProjectile>of(TracerBullet.TracerBulletProjectile::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F) // Size of the entity
                            .clientTrackingRange(32) // Tracking range
                            .updateInterval(1) // Update interval
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(MODID + ":tracer_bullet_projectile"))));

    public static final DeferredHolder<EntityType<?>, EntityType<StarShard>> STAR_SHARD =
            ENTITY_TYPES.register("star_shard",
                    () -> EntityType.Builder.<StarShard>of(StarShard::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F) // Size of the entity
                            .clientTrackingRange(32) // Tracking range
                            .updateInterval(1) // Update interval
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(MODID + ":star_shard"))));

    public static final DeferredHolder<EntityType<?>, EntityType<VoidborneSorcererEntity>> VOIDBOURNE_SORCERER_ENEMY =
            ENTITY_TYPES.register("voidbourne_sorcerer_enemy",
                    () -> EntityType.Builder.of(VoidborneSorcererEntity::new, MobCategory.MONSTER)
                            .sized(0.5F, 0.5F) // Size of the entity
                            .clientTrackingRange(32) // Tracking range
                            .updateInterval(1) // Update interval
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(MODID + ":voidbourne_sorcerer_entity"))));
    public static final DeferredHolder<EntityType<?>, EntityType<NovaImpactProxyEntity>> NOVA_PROXY =
            ENTITY_TYPES.register("nova_proxy",
                    () -> EntityType.Builder.<NovaImpactProxyEntity>of(NovaImpactProxyEntity::new, MobCategory.MISC)
                            .sized(0.1f, 0.1f)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "nova_proxy"))));

    public static final DeferredHolder<EntityType<?>, EntityType<ProjectilePortal>> PROJECTILE_PORTAL =
            ENTITY_TYPES.register("projectile_portal",
                    () -> EntityType.Builder.<ProjectilePortal>of(ProjectilePortal::new, MobCategory.MISC)
                            .sized(0.1f, 0.1f)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "projectile_portal"))));
    public static final DeferredHolder<EntityType<?>, EntityType<ArcaneShipEntity>> ARCANE_SHIP =
            ENTITY_TYPES.register("arcane_ship",
                    () -> EntityType.Builder.<ArcaneShipEntity>of(ArcaneShipEntity::new, MobCategory.MISC)
                            .sized(3f, 1f)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "arcane_ship"))));
    public static final DeferredHolder<EntityType<?>, EntityType<ArcaneShipEntity.MK1Bullet>> MK1_BULLET =
            ENTITY_TYPES.register("mk1_bullet",
                    () -> EntityType.Builder.<ArcaneShipEntity.MK1Bullet>of(ArcaneShipEntity.MK1Bullet::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F) // Size of the entity
                            .clientTrackingRange(32) // Tracking range
                            .updateInterval(1) // Update interval
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(MODID + ":mk1_bullet"))));


    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
