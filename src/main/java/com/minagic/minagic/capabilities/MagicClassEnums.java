package com.minagic.minagic.capabilities;

import com.mojang.serialization.Codec;

public class MagicClassEnums {
    public enum PlayerClassEnum {
        WIZARD,
        SORCERER,
        WARLOCK,
        CLERIC,
        DRUID,
        BARD,
        BLADESINGER,
        MONK,
        HERBALIST,
        ALCHEMIST,
        RUNE_ARTIFICER,
        SHAMAN,
        UNDECLARED;

        public static final Codec<PlayerClassEnum> CODEC = Codec.STRING.xmap(PlayerClassEnum::valueOf, PlayerClassEnum::name);

        public final String getUnknownSpellMessage() {
            return switch (this) {
                case WIZARD -> "Arcane pattern unrecognized.";
                case SORCERER -> "Your blood rejects this spell.";
                case WARLOCK -> "Your patron denies this power.";
                case CLERIC -> "Your deity forbids this magic.";
                case DRUID -> "Nature spurns this spell.";
                case BARD -> "You cannot recall this melody.";
                case BLADESINGER -> "You lack the rhythm of this form.";
                case MONK -> "Your qi cannot shape this art.";
                case HERBALIST -> "These essences elude your touch.";
                case ALCHEMIST -> "Formula unbalanced — reaction fails.";
                case RUNE_ARTIFICER -> "Runic pattern not bound to your craft.";
                case SHAMAN -> "The spirits do not answer this call.";
                case UNDECLARED -> "You lack the knowledge to cast this spell.";
            };
        }

        public final String getSubclassMismatchMessage() {
            return switch (this) {
                case WIZARD -> "This school lies beyond your study.";
                case SORCERER -> "Your lineage bears no trace of this power.";
                case WARLOCK -> "Your pact grants no claim to this magic.";
                case CLERIC -> "Your domain holds no sway here.";
                case DRUID -> "This circle does not dance with such forces.";
                case BARD -> "That tune belongs to another college.";
                case BLADESINGER ->
                        "Your voice is not aligned with this discipline.";
                case MONK -> "Your way rejects this technique.";
                case HERBALIST -> "Those herbs belong to another garden.";
                case ALCHEMIST ->
                        "Your specialty lacks the reagents for this craft.";
                case RUNE_ARTIFICER -> "Your codex rejects these sigils.";
                case SHAMAN -> "The totems you know are silent to this rite.";
                case UNDECLARED -> "You have not yet chosen your path.";
            };
        }

        public final String getLevelTooLowMessage() {
            return switch (this) {
                case WIZARD ->
                        "Your mastery is insufficient for this incantation.";
                case SORCERER -> "Your essence burns too dim for such might.";
                case WARLOCK -> "Your patron withholds this boon — grow stronger.";
                case CLERIC -> "Your faith has not yet earned this blessing.";
                case DRUID -> "Your bond with nature is not yet deep enough.";
                case BARD -> "Your voice falters at the complexity of this song.";
                case BLADESINGER ->
                        "Your blade-dance lacks the precision required.";
                case MONK -> "Your spirit has not yet reached that harmony.";
                case HERBALIST -> "Your hands tremble at such potent mixtures.";
                case ALCHEMIST -> "Your knowledge of reactions remains too basic.";
                case RUNE_ARTIFICER ->
                        "You lack the attunement to carve such runes.";
                case SHAMAN -> "The great spirits deem you unready.";
                case UNDECLARED ->
                        "You lack the foundation to attempt this power.";
            };
        }
    }

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
                case WIZARD_ELEMANCY, WIZARD_NECROMANCY, WIZARD_CONJURATION ->
                        PlayerClassEnum.WIZARD;
                case SORCERER_CELESTIAL, SORCERER_DRACONIC, SORCERER_INFERNAL,
                     SORCERER_VOIDBOURNE, SORCERER_SPIRITUAL ->
                        PlayerClassEnum.SORCERER;
                case WARLOCK_CHAIN, WARLOCK_WEAPON, WARLOCK_TOME, WARLOCK_PACTS ->
                        PlayerClassEnum.WARLOCK;
                case CLERIC_ZEALOT, CLERIC_APOSTLE, CLERIC_ORACLE ->
                        PlayerClassEnum.CLERIC;
                case DRUID_TREES, DRUID_ANIMALS, DRUID_MONSTERS,
                     DRUID_UNDEAD, DRUID_SPIRITS -> PlayerClassEnum.DRUID;
                case BARD_HARMONY, BARD_DISCORD, BARD_ELOQUENCE ->
                        PlayerClassEnum.BARD;
                case BLADESINGER_AFTERTONE, BLADESINGER_STINGS,
                     BLADESINGER_GROUNDED -> PlayerClassEnum.BLADESINGER;
                case MONK_SHADOW, MONK_MIRRORING_HAND, MONK_SPECTRAL_FIST ->
                        PlayerClassEnum.MONK;
                case HERBALIST_THORNS, HERBALIST_ROOTS, HERBALIST_FLOWERS,
                     HERBALIST_STEMS, HERBALIST_LEAVES ->
                        PlayerClassEnum.HERBALIST;
                case ALCHEMIST_DISTILLATION, ALCHEMIST_FERMENTATION,
                     ALCHEMIST_DISPERSION -> PlayerClassEnum.ALCHEMIST;
                case RUNE_ARTIFICER_ENCHANTMENT, RUNE_ARTIFICER_MYTHIC_FORGE,
                     RUNE_ARTIFICER_RUNOMANCY, RUNE_ARTIFICER_OVERRIDE ->
                        PlayerClassEnum.RUNE_ARTIFICER;
                case SHAMAN_WILD, SHAMAN_FOUR_ELEMENTS, SHAMAN_SPIRITS ->
                        PlayerClassEnum.SHAMAN;
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

    public enum DeityEnum {
        // Cleric Deities (mythological)
        THOR(DeityType.CLERIC),
        LOKI(DeityType.CLERIC),
        THOTH(DeityType.CLERIC),
        ANUBIS(DeityType.CLERIC),
        ATHENA(DeityType.CLERIC),
        HERMES(DeityType.CLERIC),
        PERUN(DeityType.CLERIC),
        MOKOSH(DeityType.CLERIC),
        INANNA(DeityType.CLERIC),
        ENKI(DeityType.CLERIC),

        // Warlock Deities (fantastical/cosmic)
        ASTRAEL(DeityType.WARLOCK),
        SOLNAR(DeityType.WARLOCK),
        NERTHUL(DeityType.WARLOCK),
        XURTHAN(DeityType.WARLOCK),
        ZYTHOR(DeityType.WARLOCK),
        VORATH(DeityType.WARLOCK),
        IMRAHN_DURN(DeityType.WARLOCK),
        LILITH(DeityType.WARLOCK),
        SYLVARIS(DeityType.WARLOCK),
        ELYNDRA(DeityType.WARLOCK),

        // Fallback
        UNDECLARED(DeityType.NONE);

        public static final Codec<DeityEnum> CODEC = Codec.STRING.xmap(DeityEnum::valueOf, DeityEnum::name);

        private final DeityType type;

        DeityEnum(DeityType type) {
            this.type = type;
        }

        public boolean isCleric() {
            return type == DeityType.CLERIC;
        }

        public boolean isWarlock() {
            return type == DeityType.WARLOCK;
        }

        public DeityType getType() {
            return type;
        }

        public enum DeityType {
            CLERIC,
            WARLOCK,
            NONE
        }
    }
}
