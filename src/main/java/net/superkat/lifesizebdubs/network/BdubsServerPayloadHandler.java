package net.superkat.lifesizebdubs.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.superkat.lifesizebdubs.entity.BdubsEntity;

public class BdubsServerPayloadHandler {

    public static void register() {
        PayloadTypeRegistry.playC2S().register(BdubsEffectPacket.ID, BdubsEffectPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(BdubsEffectPacket.ID, BdubsEffectPacket.CODEC);

        PayloadTypeRegistry.playS2C().register(BdubsMessagePacket.ID, BdubsMessagePacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(BdubsEffectPacket.ID, BdubsServerPayloadHandler::onBdubsVariantChange);
    }

    public static void onBdubsVariantChange(BdubsEffectPacket payload, ServerPlayNetworking.Context context) {
        BdubsEntity bdubs = (BdubsEntity) context.player().getWorld().getEntityById(payload.bdubsId());
        if(bdubs != null) {
            for (ServerPlayerEntity player : PlayerLookup.tracking(bdubs)) {
                ServerPlayNetworking.send(player, new BdubsEffectPacket(payload.bdubsId(), payload.evenFunnierParticles()));
            }
        }
    }


}
