package com.minagic.minagic.registries;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.*;
import com.minagic.minagic.utilities.PlayerSpellCooldownsSerializer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Minagic.MODID);

    public static final Supplier<AttachmentType<PlayerSpellCooldowns>> PLAYER_SPELL_COOLDOWNS =
            ATTACHMENTS.register("player_spell_cooldowns", () ->
                    AttachmentType.<PlayerSpellCooldowns>builder(() -> new PlayerSpellCooldowns())
                            .serialize(new PlayerSpellCooldownsSerializer()) // persistent & sync-enabled
                            .sync(ByteBufCodecs.fromCodec(PlayerSpellCooldowns.CODEC))
                            .build()
            );

    public static final Supplier<AttachmentType<PlayerClass>> PLAYER_CLASS =
            ATTACHMENTS.register("player_class", () ->
                    AttachmentType.<PlayerClass>builder(PlayerClass::new)
                            .serialize(new PlayerClassSerializer()) // persistent & sync-enabled
                            .sync(ByteBufCodecs.fromCodec(PlayerClass.CODEC))
                            .copyOnDeath()
                            .build()
            );

    public static final Supplier<AttachmentType<Mana>> MANA =
            ATTACHMENTS.register("mana", () ->
                    AttachmentType.<Mana>builder(Mana::new)
                            .serialize(new ManaSerializer()) // persistent & sync-enabled
                            .sync(ByteBufCodecs.fromCodec(Mana.CODEC))
                            .build()
            );
    public static final Supplier<AttachmentType<PlayerSimulacraAttachment>> PLAYER_SIMULACRA =
            ATTACHMENTS.register("player_simulacra", () ->
                    AttachmentType.<PlayerSimulacraAttachment>builder(PlayerSimulacraAttachment::new)
                            .serialize(new PlayerSimulacraAttachment.Serializer())
                            .sync(ByteBufCodecs.fromCodec(PlayerSimulacraAttachment.CODEC))
                            .build()
            );



    public static void register(IEventBus bus) {
        ATTACHMENTS.register(bus);
    }
}