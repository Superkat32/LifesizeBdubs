package net.superkat.lifesizebdubs;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = net.superkat.lifesizebdubs.LifeSizeBdubs.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class LifeSizeBdubsClient {
    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
//    public static class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        net.superkat.lifesizebdubs.LifeSizeBdubs.LOGGER.info("HELLO FROM CLIENT SETUP");
        net.superkat.lifesizebdubs.LifeSizeBdubs.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
//    }
}
