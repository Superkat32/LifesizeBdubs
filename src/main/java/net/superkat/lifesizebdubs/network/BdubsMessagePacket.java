package net.superkat.lifesizebdubs.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.superkat.lifesizebdubs.LifeSizeBdubs;

public record BdubsMessagePacket(Component message) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BdubsMessagePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(LifeSizeBdubs.MODID, "bdubs_message"));

    public static final StreamCodec<ByteBuf, BdubsMessagePacket> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC,
            BdubsMessagePacket::message,
            BdubsMessagePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
