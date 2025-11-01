package com.minagic.minagic.capabilities;

import com.mojang.serialization.Codec;

public enum PlayerSubClassEnum {
    WIZARD_ELEMANCY,
    WIZARD_NECROMANCY,
    WIZARD_CONJURATION,
    SORCERER_CELESTIAL,
    SORCERER_DRACONIC,
    SORCERER_INFERNAL,
    SORCERER_VOIDBOURNE,
    SORCERER_SPIRITUAL,
    WARLOCK_CHAIN,
    WARLOCK_WEAPON,
    WARLOCK_TOME,
    WARLOCK_PACTS,
    CLERIC_ZEALOT,
    CLERIC_APOSTLE,
    CLERIC_ORACLE,
    DRUID_TREES,
    DRUID_ANIMALS,
    DRUID_MONSTERS,
    DRUID_UNDEAD,
    DRUID_SPIRITS,
    BARD_HARMONY,
    BARD_DISCORD,
    BARD_ELOQUENCE,
    BLADESINGER_AFTERTONE,
    BLADESINGER_STINGS,
    BLADESINGER_GROUNDED,
    MONK_SHADOW,
    MONK_MIRRORING_HAND,
    MONK_SPECTRAL_FIST,
    HERBALIST_THORNS,
    HERBALIST_ROOTS,
    HERBALIST_FLOWERS,
    HERBALIST_STEMS,
    HERBALIST_LEAVES,
    ALCHEMIST_DISTILLATION,
    ALCHEMIST_FERMENTATION,
    ALCHEMIST_DISPERSION,
    RUNE_ARTIFICER_ENCHANTMENT,
    RUNE_ARTIFICER_MYTHIC_FORGE,
    RUNE_ARTIFICER_RUNOMANCY,
    RUNE_ARTIFICER_OVERRIDE,
    SHAMAN_WILD,
    SHAMAN_FOUR_ELEMENTS,
    SHAMAN_SPIRITS,
    UNDECLARED;


    public static final Codec<PlayerSubClassEnum> CODEC = Codec.STRING.xmap(PlayerSubClassEnum::valueOf, PlayerSubClassEnum::name);

    public PlayerClassEnum getParentClass() {
        // Map subclasses to their parent classes
        return switch (this) {
            case WIZARD_ELEMANCY, WIZARD_NECROMANCY, WIZARD_CONJURATION -> PlayerClassEnum.WIZARD;
            case SORCERER_CELESTIAL, SORCERER_DRACONIC, SORCERER_INFERNAL,
                 SORCERER_VOIDBOURNE, SORCERER_SPIRITUAL -> PlayerClassEnum.SORCERER;
            case WARLOCK_CHAIN, WARLOCK_WEAPON, WARLOCK_TOME, WARLOCK_PACTS -> PlayerClassEnum.WARLOCK;
            case CLERIC_ZEALOT, CLERIC_APOSTLE, CLERIC_ORACLE -> PlayerClassEnum.CLERIC;
            case DRUID_TREES, DRUID_ANIMALS, DRUID_MONSTERS,
                 DRUID_UNDEAD, DRUID_SPIRITS -> PlayerClassEnum.DRUID;
            case BARD_HARMONY, BARD_DISCORD, BARD_ELOQUENCE -> PlayerClassEnum.BARD;
            case BLADESINGER_AFTERTONE, BLADESINGER_STINGS, BLADESINGER_GROUNDED -> PlayerClassEnum.BLADESINGER;
            case MONK_SHADOW, MONK_MIRRORING_HAND, MONK_SPECTRAL_FIST -> PlayerClassEnum.MONK;
            case HERBALIST_THORNS, HERBALIST_ROOTS, HERBALIST_FLOWERS,
                 HERBALIST_STEMS, HERBALIST_LEAVES -> PlayerClassEnum.HERBALIST;
            case ALCHEMIST_DISTILLATION, ALCHEMIST_FERMENTATION, ALCHEMIST_DISPERSION -> PlayerClassEnum.ALCHEMIST;
            case RUNE_ARTIFICER_ENCHANTMENT, RUNE_ARTIFICER_MYTHIC_FORGE,
                 RUNE_ARTIFICER_RUNOMANCY, RUNE_ARTIFICER_OVERRIDE -> PlayerClassEnum.RUNE_ARTIFICER;
            case SHAMAN_WILD, SHAMAN_FOUR_ELEMENTS, SHAMAN_SPIRITS -> PlayerClassEnum.SHAMAN;
            default -> PlayerClassEnum.UNDECLARED;
        };
    }

    public String getString() {
        return switch (this) {
            // --- Wizards ---
            case WIZARD_ELEMANCY -> "Tradition of Elemancy";
            case WIZARD_NECROMANCY -> "Tradition of Necromancy";
            case WIZARD_CONJURATION -> "Tradition of Conjuration";

            // --- Sorcerers ---
            case SORCERER_CELESTIAL -> "Celestial Bloodline";
            case SORCERER_DRACONIC -> "Draconic Bloodline";
            case SORCERER_INFERNAL -> "Infernal Bloodline";
            case SORCERER_VOIDBOURNE -> "Voidbourne Bloodline";
            case SORCERER_SPIRITUAL -> "Spiritual Bloodline";

            // --- Warlocks ---
            case WARLOCK_CHAIN -> "Pact of the Chain";
            case WARLOCK_WEAPON -> "Pact of the Blade";
            case WARLOCK_TOME -> "Pact of the Tome";
            case WARLOCK_PACTS -> "Pact of the Pacts";

            // --- Clerics ---
            case CLERIC_ZEALOT -> "Domain of Zealot";
            case CLERIC_APOSTLE -> "Domain of Apostle";
            case CLERIC_ORACLE -> "Domain of Oracle";

            // --- Druids ---
            case DRUID_TREES -> "Circle of Trees";
            case DRUID_ANIMALS -> "Circle of Animals";
            case DRUID_MONSTERS -> "Circle of Monsters";
            case DRUID_UNDEAD -> "Circle of Undead";
            case DRUID_SPIRITS -> "Circle of Spirits";

            // --- Bards ---
            case BARD_HARMONY -> "College of Harmony";
            case BARD_DISCORD -> "College of Discord";
            case BARD_ELOQUENCE -> "College of Eloquence";

            // --- Bladesingers ---
            case BLADESINGER_AFTERTONE -> "Voice of Aftertone";
            case BLADESINGER_STINGS -> "Voice Stinging Stings";
            case BLADESINGER_GROUNDED -> "Voice of Grounded Melody";

            // --- Monks ---
            case MONK_SHADOW -> "Way of Shadow";
            case MONK_MIRRORING_HAND -> "Way of the Mirroring Hand";
            case MONK_SPECTRAL_FIST -> "Way of the Spectral Fist";

            // --- Herbalists ---
            case HERBALIST_THORNS -> "Garden of Thorns";
            case HERBALIST_ROOTS -> "Garden of Roots";
            case HERBALIST_FLOWERS -> "Garden of Flowers";
            case HERBALIST_STEMS -> "Garden of Stems";
            case HERBALIST_LEAVES -> "Garden of Leaves";

            // --- Alchemists ---
            case ALCHEMIST_DISTILLATION -> "Cauldron of Distillation";
            case ALCHEMIST_FERMENTATION -> "Cauldron of Fermentation";
            case ALCHEMIST_DISPERSION -> "Cauldron of Dispersion";

            // --- Rune Artificers ---
            case RUNE_ARTIFICER_ENCHANTMENT -> "Codex of  Enchantment";
            case RUNE_ARTIFICER_MYTHIC_FORGE -> "Codex of Mythic Forge";
            case RUNE_ARTIFICER_RUNOMANCY -> "Codex of Runomancy";
            case RUNE_ARTIFICER_OVERRIDE -> "Codex of Override";

            // --- Shamans ---
            case SHAMAN_WILD -> "Omen of the Wild";
            case SHAMAN_FOUR_ELEMENTS -> "Omen of the Four Elements";
            case SHAMAN_SPIRITS -> "Omen of the Spirits";

            // --- Fallback ---
            case UNDECLARED -> "Undeclared";
        };
    }
}
