package net.superkat.lifesizebdubs.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;
import net.superkat.lifesizebdubs.entity.BdubsEntity;

public class BdubsClientPayloadHandler {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(BdubsEffectPacket.ID, BdubsClientPayloadHandler::onBdubsVariantChange);
        ClientPlayNetworking.registerGlobalReceiver(BdubsMessagePacket.ID, BdubsClientPayloadHandler::onBdubsMessage);
    }

    public static void onBdubsVariantChange(BdubsEffectPacket payload, ClientPlayNetworking.Context context) {
        BdubsEntity bdubs = (BdubsEntity) context.player().getWorld().getEntityById(payload.bdubsId());
        if(bdubs != null) {
            bdubs.onEffectUpdate(payload.evenFunnierParticles());
        }
    }

    public static void onBdubsMessage(BdubsMessagePacket payload, ClientPlayNetworking.Context context) {
        Text message = payload.message();
        context.client().player.sendMessage(message, false);
    }

}
