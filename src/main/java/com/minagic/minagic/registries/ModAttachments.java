package com.minagic.minagic.registries;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.*;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertManager;
import com.minagic.minagic.capabilities.hudAlerts.HudOverrideManager;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

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

    public static final Supplier<AttachmentType<PlayerClass>> PLAYER_CLASS =
            ATTACHMENTS.register("player_class", () ->
                    AttachmentType.builder(PlayerClass::new)
                            .serialize(new PlayerClass.Serializer()) // persistent & sync-enabled
                            .sync(ByteBufCodecs.fromCodec(PlayerClass.CODEC))
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

    public static final Supplier<AttachmentType<HudAlertManager>> HUD_ALERTS =
            ATTACHMENTS.register("hud_alerts", () ->
                    AttachmentType.builder(HudAlertManager::new)
                            .serialize(new HudAlertManager.Serializer())
                            .sync(ByteBufCodecs.fromCodec(HudAlertManager.CODEC))
                            .build()
            );

    public static final Supplier<AttachmentType<SpellMetadata>> SPELL_METADATA =
            ATTACHMENTS.register("spell_metadata", () ->
                    AttachmentType.builder(SpellMetadata::new)
                            .serialize(new SpellMetadata.Serializer())
                            .sync(ByteBufCodecs.fromCodec(SpellMetadata.CODEC))
                            .build());

    public static final Supplier<AttachmentType<HudOverrideManager>> HUD_OVERRIDES =
            ATTACHMENTS.register("hud_overrides", () ->
                    AttachmentType.builder(HudOverrideManager::new)
                            .serialize(new HudOverrideManager.Serializer())
                            .sync(ByteBufCodecs.fromCodec(HudOverrideManager.CODEC))
                            .build());


    public static void register(IEventBus bus) {
        ATTACHMENTS.register(bus);
    }
}