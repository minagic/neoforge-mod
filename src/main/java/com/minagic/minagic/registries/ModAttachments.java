package com.minagic.minagic.registries;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.capabilities.PlayerSpellCooldowns;
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

    public static void register(IEventBus bus) {
        ATTACHMENTS.register(bus);
    }
}