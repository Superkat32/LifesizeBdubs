package net.superkat.lifesizebdubs.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.superkat.lifesizebdubs.LifeSizeBdubs;

public record BdubsMessagePacket(Text message, boolean tangoMode) implements CustomPayload {
    public static final CustomPayload.Id<BdubsMessagePacket> ID = new Id<>(Identifier.of(LifeSizeBdubs.MOD_ID, "bdubs_message"));
    public static final PacketCodec<RegistryByteBuf, BdubsMessagePacket> CODEC = PacketCodec.tuple(
            TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, BdubsMessagePacket::message,
            PacketCodecs.BOOL, BdubsMessagePacket::tangoMode,
            BdubsMessagePacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
