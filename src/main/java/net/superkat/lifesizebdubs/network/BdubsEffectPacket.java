package net.superkat.lifesizebdubs.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.superkat.lifesizebdubs.LifeSizeBdubs;

public record BdubsEffectPacket(int bdubsId, boolean evenFunnierParticles) implements CustomPayload {

    public static final CustomPayload.Id<BdubsEffectPacket> ID = new Id<>(Identifier.of(LifeSizeBdubs.MOD_ID));
    public static final PacketCodec<RegistryByteBuf, BdubsEffectPacket> CODEC = CustomPayload.codecOf(BdubsEffectPacket::write, BdubsEffectPacket::new);

    public BdubsEffectPacket(RegistryByteBuf buf) {
        this(buf.readInt(), buf.readBoolean());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeInt(this.bdubsId);
        buf.writeBoolean(this.evenFunnierParticles);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
