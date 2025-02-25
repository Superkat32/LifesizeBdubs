package net.superkat.lifesizebdubs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.superkat.lifesizebdubs.data.BdubsVariant;
import net.superkat.lifesizebdubs.entity.BdubsEntity;
import net.superkat.lifesizebdubs.network.BdubsServerPayloadHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifeSizeBdubs implements ModInitializer {
	public static final String MOD_ID = "lifesizebdubs";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final RegistryKey<Registry<BdubsVariant>> BDUBS_VARIANT_KEY = RegistryKey.ofRegistry(Identifier.of(MOD_ID, "bdubs_variant"));

	public static final EntityType<BdubsEntity> BDUBS_ENTITY = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of(MOD_ID, "bdubsentity"),
			EntityType.Builder.create(BdubsEntity::new, SpawnGroup.CREATURE).dimensions(0.5f, 0.5f).build()
	);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		FabricDefaultAttributeRegistry.register(BDUBS_ENTITY, BdubsEntity.createMobAttributes());

		DynamicRegistries.registerSynced(BDUBS_VARIANT_KEY, BdubsVariant.CODEC);

		BdubsServerPayloadHandler.register();
	}
}