package net.superkat.lifesizebdubs;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.JsonKeySortOrderCallback;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryWrapper;
import net.superkat.lifesizebdubs.data.BdubsVariants;

import java.util.concurrent.CompletableFuture;

public class LifeSizeBdubsDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(BdubsBuilderProvider::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		registryBuilder.addRegistry(LifeSizeBdubs.BDUBS_VARIANT_KEY, BdubsVariants::bootstrap);
	}

	//YESSSSSS IT MAKES ME SO HAPPY
	@Override
	public void addJsonKeySortOrders(JsonKeySortOrderCallback callback) {
		callback.add("name", 1);
		callback.add("texture", 2);
		callback.add("item", 3);
		callback.add("alt_item", 4);
		callback.add("messages", 6);
		callback.add("timed_messages", 7);
	}

	public static class BdubsBuilderProvider extends FabricDynamicRegistryProvider {

		public BdubsBuilderProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
			entries.addAll(registries.getWrapperOrThrow(LifeSizeBdubs.BDUBS_VARIANT_KEY));
		}

		@Override
		public String getName() {
			return "Bdubs Variants";
		}
	}

}
