package com.minagic.minagic.registries;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.*;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertAttachment;
import com.minagic.minagic.capabilities.hudAlerts.WhiteFlashAttachment;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import java.util.List;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Minagic.MODID);

    public static final Supplier<AttachmentType<CooldownAttachment>> PLAYER_SPELL_COOLDOWNS =
            ATTACHMENTS.register("player_spell_cooldowns", () ->
                    AttachmentType.builder(CooldownAttachment::new)
                            .serialize(new CooldownAttachment.Serializer()) // persistent & sync-enabled
                            .sync(ByteBufCodecs.fromCodec(CooldownAttachment.CODEC))
                            .build()
            );

    public static final Supplier<AttachmentType<MagicClass>> PLAYER_CLASS =
            ATTACHMENTS.register("player_class", () ->
                    AttachmentType.builder(MagicClass::new)
                            .serialize(new MagicClass.Serializer()) // persistent & sync-enabled
                            .sync(ByteBufCodecs.fromCodec(MagicClass.CODEC))
                            .copyOnDeath()
                            .build()
            );

    public static final Supplier<AttachmentType<ManaAttachment>> MANA =
            ATTACHMENTS.register("mana", () ->
                    AttachmentType.builder(ManaAttachment::new)
                            .serialize(new ManaAttachment.Serializer()) // persistent & sync-enabled
                            .sync(ByteBufCodecs.fromCodec(ManaAttachment.CODEC))
                            .build()
            );
    public static final Supplier<AttachmentType<SimulacraAttachment>> PLAYER_SIMULACRA =
            ATTACHMENTS.register("player_simulacra", () ->
                    AttachmentType.builder(SimulacraAttachment::new)
                            .serialize(new SimulacraAttachment.Serializer())
                            .sync(ByteBufCodecs.fromCodec(SimulacraAttachment.CODEC))
                            .build()
            );

    public static final Supplier<AttachmentType<HudAlertAttachment>> HUD_ALERTS =
            ATTACHMENTS.register("hud_alerts", () ->
                    AttachmentType.builder(HudAlertAttachment::new)
                            .serialize(new HudAlertAttachment.Serializer())
                            .sync(ByteBufCodecs.fromCodec(HudAlertAttachment.CODEC))
                            .build()
            );

    public static final Supplier<AttachmentType<SpellMetadata>> SPELL_METADATA =
            ATTACHMENTS.register("spell_metadata", () ->
                    AttachmentType.builder(SpellMetadata::new)
                            .serialize(new SpellMetadata.Serializer())
                            .sync(ByteBufCodecs.fromCodec(SpellMetadata.CODEC))
                            .build());

    public static final Supplier<AttachmentType<WhiteFlashAttachment>> WHITE_FLASH =
            ATTACHMENTS.register("white_flash_override", () ->
                    AttachmentType.builder(WhiteFlashAttachment::new)
                            .serialize(new WhiteFlashAttachment.Serializer())
                            .sync(ByteBufCodecs.fromCodec(WhiteFlashAttachment.CODEC))
                            .build()
            );


    private static final List<AttachmentEntry<?>> REGISTERED_ATTACHMENTS = List.of(
            new AttachmentEntry<>(PLAYER_SPELL_COOLDOWNS, CooldownAttachment::new),
            new AttachmentEntry<>(PLAYER_CLASS, MagicClass::new),
            new AttachmentEntry<>(MANA, ManaAttachment::new),
            new AttachmentEntry<>(PLAYER_SIMULACRA, SimulacraAttachment::new),
            new AttachmentEntry<>(HUD_ALERTS, HudAlertAttachment::new),
            new AttachmentEntry<>(SPELL_METADATA, SpellMetadata::new),
            new AttachmentEntry<>(WHITE_FLASH, WhiteFlashAttachment::new)
    );

    public static void resetAllAttachments(Entity entity) {
        for (AttachmentEntry<?> entry : REGISTERED_ATTACHMENTS) {
            resetAttachment(entity, entry);
        }
    }

    private static <T> void resetAttachment(Entity entity, AttachmentEntry<T> entry) {

        entity.setData(entry.typeSupplier().get(), entry.instanceFactory().get());
    }

    private record AttachmentEntry<T>(Supplier<AttachmentType<T>> typeSupplier,
                                      Supplier<T> instanceFactory) {
    }

    public static void register(IEventBus bus) {
        ATTACHMENTS.register(bus);
    }
}
