package com.minagic.minagic;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;



public final class DamageTypes  {
    private final static String modID =  Minagic.MODID;

    public static final ResourceKey<DamageType> MAGIC = ResourceKey.create(
            Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(modID, "magic"));

    public static final ResourceKey<DamageType> ELEMENTAL = ResourceKey.create(
            Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(modID, "elemental"));

    public static final ResourceKey<DamageType> DOT = ResourceKey.create(
            Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(modID, "dot"));

    public static final ResourceKey<DamageType> FIRE = ResourceKey.create(
            Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(modID, "fire"));

    public static final ResourceKey<DamageType> NATURAL = ResourceKey.create(
            Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(modID, "natural"));

    public static final ResourceKey<DamageType> POISON = ResourceKey.create(
            Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(modID, "poison"));

    public static final ResourceKey<DamageType> LIGHTNING = ResourceKey.create(
            Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(modID, "lightning"));

    public static final ResourceKey<DamageType> PHYSICAL = ResourceKey.create(
            Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(modID, "physical"));

    public static final ResourceKey<DamageType> ARMOR_PIERCING = ResourceKey.create(
            Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(modID, "armor_piercing"));

    public static final ResourceKey<DamageType> INJURY = ResourceKey.create(
            Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(modID, "injury"));

    public static final ResourceKey<DamageType> PSYCHIC = ResourceKey.create(
            Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(modID, "psychic"));

    public static final ResourceKey<DamageType> ETHEREAL = ResourceKey.create(
            Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(modID, "ethereal"));

    public static final ResourceKey<DamageType> EXECUTION = ResourceKey.create(
            Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(modID, "execution"));

    public static final ResourceKey<DamageType> UNBLOCKABLE = ResourceKey.create(
            Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(modID, "unblockable"));
}   

