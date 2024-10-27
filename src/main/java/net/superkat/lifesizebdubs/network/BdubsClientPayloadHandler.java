package net.superkat.lifesizebdubs.network;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.superkat.lifesizebdubs.Config;
import net.superkat.lifesizebdubs.entity.BdubsEntity;

public class BdubsClientPayloadHandler {

    public static void onBdubsMessage(BdubsMessagePacket payload, IPayloadContext context) {
        if(Config.displayBdubsMessages) {
            context.player().displayClientMessage(payload.message(), false);
        }
    }

    public static void onBdubsVariantChange(BdubsVariantChangeEffectsPacket payload, IPayloadContext context) {
        BdubsEntity bdubs = (BdubsEntity) context.player().level().getEntity(payload.BdubsId());
        if(bdubs != null) {
            if(payload.evenFunnierParticles()) {
                context.player().level().playSound(context.player(), bdubs.getX(), bdubs.getY(), bdubs.getZ(), SoundEvents.ALLAY_ITEM_GIVEN, SoundSource.AMBIENT, 2, 1);
                context.player().level().playSound(context.player(), bdubs.getX(), bdubs.getY(), bdubs.getZ(), SoundEvents.BREEZE_CHARGE, SoundSource.AMBIENT, 1f, 1.25f);
                context.player().level().playSound(context.player(), bdubs.getX(), bdubs.getY(), bdubs.getZ(), SoundEvents.BREEZE_DEFLECT, SoundSource.AMBIENT, 1f, 1.25f);
                bdubs.spawnEvenFunnierParticles();
                bdubs.givenSugarAnim();
            } else {
                bdubs.spawnFunnyParticles();
            }
        }
    }

}
