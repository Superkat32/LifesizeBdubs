package net.superkat.lifesizebdubs;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.superkat.lifesizebdubs.data.BdubsVariant;
import net.superkat.lifesizebdubs.entity.BdubsEntity;
import org.slf4j.Logger;
import java.util.Set;

@Mod(LifeSizeBdubs.MODID)
public class LifeSizeBdubs {
    public static final String MODID = "lifesizebdubs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceKey<Registry<BdubsVariant>> BDUBS_VARIANT_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MODID, "bdubs_variant"));
    public static final ResourceKey<BdubsVariant> BDUBS_DEFAULT_VARIANT = ResourceKey.create(BDUBS_VARIANT_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(MODID, "mybdubs"));
    public static final ResourceKey<BdubsVariant> TEST_VARIANT = ResourceKey.create(BDUBS_VARIANT_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(MODID, "belovedbdubs"));

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<BdubsEntity>> BDUBS_ENTITY = registerEntity("bdubsentity", BdubsEntity::new, 0.5f, 0.5f);

    public LifeSizeBdubs(IEventBus modEventBus, ModContainer modContainer) {
        ENTITIES.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerDatapackRegistries);
        modEventBus.addListener(this::onGatherData);
        modEventBus.addListener(this::registerEntityAttributes);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    public void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(BDUBS_VARIANT_REGISTRY_KEY,
                BdubsVariant.DIRECT_CODEC,
                BdubsVariant.DIRECT_CODEC);
    }

    public void onGatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeServer(), (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(),
                new RegistrySetBuilder()
                .add(
                    BDUBS_VARIANT_REGISTRY_KEY, bootstrap -> {
                        bootstrap.register(BDUBS_DEFAULT_VARIANT, BdubsVariant.DEFAULT);
                        bootstrap.register(TEST_VARIANT, new BdubsVariant(
                                "mossybdubs",
                                ResourceLocation.fromNamespaceAndPath(MODID, "textures/bdubs/mossybdubs.png"),
                                Items.MOSS_BLOCK.getDefaultInstance()
                        ));
                    }
                ),
                Set.of(MODID)
        ));
    }

    public void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(BDUBS_ENTITY.get(), PathfinderMob.createMobAttributes().build());
    }

    private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String name, EntityType.EntityFactory<T> entity, float width, float height) {
        return ENTITIES.register(name, () -> EntityType.Builder.of(entity, MobCategory.CREATURE).sized(width, height).build(name));
    }
}
