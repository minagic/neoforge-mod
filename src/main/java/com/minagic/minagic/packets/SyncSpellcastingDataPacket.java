package com.minagic.minagic.packets;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.SpellcastingItem;
import com.minagic.minagic.api.SpellcastingItemData;
import com.minagic.minagic.registries.ModDataComponents;
import com.minagic.minagic.sorcerer.StaffData;
import com.minagic.minagic.wizard.WizardWandData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncSpellcastingDataPacket(ItemStack stack) implements CustomPacketPayload {
    public static final Type<SyncSpellcastingDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "sync_staff_data"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncSpellcastingDataPacket pkt, IPayloadContext ctx) {
        if (ctx.player() == null) return;
        Minecraft.getInstance().execute(() -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            ItemStack main = player.getMainHandItem();
            if (!(main.getItem() instanceof SpellcastingItem<?> sci)) return;
            setStackData(main, sci, pkt.stack().get(sci.getType()));

        });
    }

    private static void setStackData(ItemStack stack, SpellcastingItem<?> sci, SpellcastingItemData data) {
        System.out.println("[-SYNC SPELLCASTING ITEM DATA-] Syncing data for SpellcastingItem to stack");
        System.out.println("[-SYNC SPELLCASTING ITEM DATA-] Expecting this data component: " + sci.getType());

        if (sci.getType() == ModDataComponents.STAFF_DATA.get()) {
            stack.set(ModDataComponents.STAFF_DATA.get(), (StaffData) data);
        }
        else if (sci.getType() == ModDataComponents.WIZARD_WAND_DATA.get()) {
            stack.set(ModDataComponents.WIZARD_WAND_DATA.get(), (WizardWandData) data);
        }
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSpellcastingDataPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.STREAM_CODEC, SyncSpellcastingDataPacket::stack,
                    SyncSpellcastingDataPacket::new
            );
}
