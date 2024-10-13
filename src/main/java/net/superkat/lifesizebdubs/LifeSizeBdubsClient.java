package net.superkat.lifesizebdubs;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.superkat.lifesizebdubs.entity.client.BdubsEntityRenderer;

@EventBusSubscriber(modid = LifeSizeBdubs.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class LifeSizeBdubsClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        LifeSizeBdubs.LOGGER.info("HELLO FROM CLIENT SETUP");
        LifeSizeBdubs.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(LifeSizeBdubs.BDUBS_ENTITY.get(), BdubsEntityRenderer::new);
    }
}
