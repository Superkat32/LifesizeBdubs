package net.superkat.lifesizebdubs;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.superkat.lifesizebdubs.entity.client.BdubsEntityRenderer;

//@EventBusSubscriber(modid = LifeSizeBdubs.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@Mod(value = LifeSizeBdubs.MODID, dist = Dist.CLIENT)
public class LifeSizeBdubsClient {

    public LifeSizeBdubsClient(IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (mc, parent) -> new ConfigurationScreen(container, parent));

        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::registerRenderers);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
//        LifeSizeBdubs.LOGGER.info("HELLO FROM CLIENT SETUP");
//        LifeSizeBdubs.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(LifeSizeBdubs.BDUBS_ENTITY.get(), BdubsEntityRenderer::new);
    }

    public static boolean earsLoaded() {
        return ModList.get().isLoaded("ears");
    }
}
