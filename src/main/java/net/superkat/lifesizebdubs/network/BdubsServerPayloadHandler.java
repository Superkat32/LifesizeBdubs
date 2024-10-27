package net.superkat.lifesizebdubs.network;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.superkat.lifesizebdubs.entity.BdubsEntity;

public class BdubsServerPayloadHandler {

    public static void onBdubsVariantChange(BdubsVariantChangeEffectsPacket payload, IPayloadContext context) {
        BdubsEntity bdubsEntity = (BdubsEntity) context.player().level().getEntity(payload.BdubsId());
        if(bdubsEntity != null) {
            PacketDistributor.sendToPlayersTrackingEntity(bdubsEntity, new BdubsVariantChangeEffectsPacket(payload.BdubsId(), payload.evenFunnierParticles()));
        }
    }

}
