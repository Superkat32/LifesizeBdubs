package net.superkat.lifesizebdubs.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.superkat.lifesizebdubs.entity.BdubsEntity;

public class BdubsClientPayloadHandler {

    public static void register() {
//        PayloadTypeRegistry.playC2S().register(BdubsEffectPacket.ID, BdubsEffectPacket.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(BdubsEffectPacket.ID, BdubsClientPayloadHandler::onBdubsVariantChange);
    }

    public static void onBdubsVariantChange(BdubsEffectPacket payload, ClientPlayNetworking.Context context) {
        BdubsEntity bdubs = (BdubsEntity) context.player().getWorld().getEntityById(payload.bdubsId());
        if(bdubs != null) {
            bdubs.onEffectUpdate(payload.evenFunnierParticles());
        }
    }

}
