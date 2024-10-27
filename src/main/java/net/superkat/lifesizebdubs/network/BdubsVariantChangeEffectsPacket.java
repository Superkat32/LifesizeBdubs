package net.superkat.lifesizebdubs.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.superkat.lifesizebdubs.LifeSizeBdubs;

public record BdubsVariantChangeEffectsPacket(int BdubsId, boolean evenFunnierParticles) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BdubsVariantChangeEffectsPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(LifeSizeBdubs.MODID, "bdubs_particles"));

    public static final StreamCodec<ByteBuf, BdubsVariantChangeEffectsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            BdubsVariantChangeEffectsPacket::BdubsId,
            ByteBufCodecs.BOOL,
            BdubsVariantChangeEffectsPacket::evenFunnierParticles,
            BdubsVariantChangeEffectsPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
