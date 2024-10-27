package net.superkat.lifesizebdubs;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
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
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.superkat.lifesizebdubs.data.BdubsVariant;
import net.superkat.lifesizebdubs.duck.LifeSizeBdubsPlayer;
import net.superkat.lifesizebdubs.entity.BdubsEntity;
import net.superkat.lifesizebdubs.network.BdubsClientPayloadHandler;
import net.superkat.lifesizebdubs.network.BdubsServerPayloadHandler;
import net.superkat.lifesizebdubs.network.BdubsVariantChangeEffectsPacket;
import net.superkat.lifesizebdubs.network.BdubsMessagePacket;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mod(LifeSizeBdubs.MODID)
public class LifeSizeBdubs {
    public static final String MODID = "lifesizebdubs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceKey<Registry<BdubsVariant>> BDUBS_VARIANT_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MODID, "bdubs_variant"));
    public static final ResourceKey<BdubsVariant> BDUBS_DEFAULT_VARIANT = ResourceKey.create(BDUBS_VARIANT_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(MODID, "bdubs"));
    public static final ResourceKey<BdubsVariant> BDUBS_MOSSY_VARIANT = ResourceKey.create(BDUBS_VARIANT_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(MODID, "mossy_bdubs"));

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<BdubsEntity>> BDUBS_ENTITY = registerEntity("bdubsentity", BdubsEntity::new, 0.5f, 0.5f);

    //TODO - Test on multiplayer
    //TODO - custom icon
    //TODO - tnt bdubs
    //TODO - Scar(Enchanter), Etho(Ladder), Grian(?)?
    //TODO - (after upload) wiki

    public LifeSizeBdubs(IEventBus modEventBus, ModContainer modContainer) {
        //I really don't understand when to use which event - I just trial and error-ed what worked
        ENTITIES.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerDatapackRegistries);
        modEventBus.addListener(this::onGatherData);
        modEventBus.addListener(this::registerEntityAttributes);
        modEventBus.addListener(this::registerPackets);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(this::allowLockingShoulderEntities);
        NeoForge.EVENT_BUS.addListener(this::tickLastLockingShoulderEntity);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
//        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
//        LOGGER.info("HELLO from server starting");
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
                        bootstrap.register(BDUBS_MOSSY_VARIANT, new BdubsVariant(
                                Component.translatable("lifesizebdubs.variant.mossy"),
                                ResourceLocation.fromNamespaceAndPath(MODID, "textures/bdubs/mossybdubs.png"),
                                Items.MOSS_BLOCK.getDefaultInstance(),
                                List.of("Moss my beloved"),
                                Optional.of(List.of((Pair.of("Uh oh! Time to swheep!", 12500))))
                        ));
                    }
                ),
                Set.of(MODID)
        ));
    }

    public void registerPackets(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(BdubsMessagePacket.TYPE, BdubsMessagePacket.STREAM_CODEC, BdubsClientPayloadHandler::onBdubsMessage);
        registrar.playBidirectional(
                BdubsVariantChangeEffectsPacket.TYPE,
                BdubsVariantChangeEffectsPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        BdubsClientPayloadHandler::onBdubsVariantChange,
                        BdubsServerPayloadHandler::onBdubsVariantChange
                )
        );
    }

    @SubscribeEvent
    public void allowLockingShoulderEntities(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if(event.getItemStack().is(Items.SLIME_BALL)) {
            if(!player.getShoulderEntityLeft().isEmpty() || !player.getShoulderEntityRight().isEmpty()) {
                LifeSizeBdubsPlayer bdubsPlayer = (LifeSizeBdubsPlayer) player;
                if(bdubsPlayer.lifesizebdubs$lastLockTicks() <= 20) return;
                boolean locked = bdubsPlayer.lifesizebdubs$lockedShoulderEntity();
                boolean newLocked = !locked;
                bdubsPlayer.lifesizebdubs$setLockedShoulderEntity(newLocked);
                if(newLocked) {
                    if(!player.level().isClientSide) {
                        player.displayClientMessage(Component.translatable("lifesizebdubs.entityshoulderlocked"), true);
                        player.playNotifySound(SoundEvents.SLIME_SQUISH, SoundSource.NEUTRAL, 1f, 1f);
                        ((ServerLevel)player.level()).sendParticles(ParticleTypes.ITEM_SLIME, player.getX(), player.getY() + 0.15, player.getZ(), 7, 0, 0, 0, 0);
//                        for (int i = 0; i < 7; i++) {
//                            player.level().addParticle(ParticleTypes.ITEM_SLIME, player.getX(), player.getY() + 0.15, player.getZ(), 0, 0, 0);
//                        }
                    }
                } else {
                    if(!player.level().isClientSide) {
                        player.displayClientMessage(Component.translatable("lifesizebdubs.entityshoulderunlocked"), true);
                        player.playNotifySound(SoundEvents.AXE_WAX_OFF, SoundSource.NEUTRAL, 1f, 1f);
                        ((ServerLevel)player.level()).sendParticles(ParticleTypes.DUST_PLUME, player.getX(), player.getY() + 0.15, player.getZ(), 7, 0, 0, 0, 0);
//                        for (int i = 0; i < 7; i++) {
//                            player.level().addParticle(ParticleTypes.DUST_PLUME, player.getX(), player.getY() + 0.15, player.getZ(), 0, 0, 0);
//                        }
                    }
                }
                bdubsPlayer.lifesizebdubs$setLastLockTicks(0);
            }
        }
    }

    @SubscribeEvent
    public void tickLastLockingShoulderEntity(EntityTickEvent.Post event) {
        if(event.getEntity() instanceof Player player) {
            LifeSizeBdubsPlayer bdubsPlayer = (LifeSizeBdubsPlayer) player;
            bdubsPlayer.lifesizebdubs$setLastLockTicks(bdubsPlayer.lifesizebdubs$lastLockTicks() + 1);
        }
    }

    public void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(BDUBS_ENTITY.get(), PathfinderMob.createMobAttributes().build());
    }

    private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String name, EntityType.EntityFactory<T> entity, float width, float height) {
        return ENTITIES.register(name, () -> EntityType.Builder.of(entity, MobCategory.CREATURE).sized(width, height).build(name));
    }
}
