package net.superkat.lifesizebdubs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.superkat.lifesizebdubs.entity.client.BdubsEntityRenderer;
import net.superkat.lifesizebdubs.network.BdubsClientPayloadHandler;

public class LifeSizeBdubsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(LifeSizeBdubs.BDUBS_ENTITY, BdubsEntityRenderer::new);

        BdubsClientPayloadHandler.register();
    }

}
