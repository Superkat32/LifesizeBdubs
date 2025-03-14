package net.superkat.lifesizebdubs;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.superkat.lifesizebdubs.data.BdubsVariant;
import net.superkat.lifesizebdubs.duck.LifeSizeBdubsPlayer;
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

	public static final Item LIFESIZEBDUBS_SPAWN_EGG = registerItem(new SpawnEggItem(BDUBS_ENTITY, 0xffffff, 0xffffff, new Item.Settings()), "lifesizebdubs_spawn_egg");

	public static final AttachmentType<Boolean> LOCKED_SHOULDER_ENTITY_ATTACHMENT = AttachmentRegistry.createPersistent(
			Identifier.of(MOD_ID, "locked_shoulder_entities"), Codec.BOOL
	);

	@Override
	public void onInitialize() {
		FabricDefaultAttributeRegistry.register(BDUBS_ENTITY, BdubsEntity.createMobAttributes());

		DynamicRegistries.registerSynced(BDUBS_VARIANT_KEY, BdubsVariant.CODEC);

		BdubsServerPayloadHandler.register();

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(itemGroup -> {
			itemGroup.add(LIFESIZEBDUBS_SPAWN_EGG);
		});

		UseItemCallback.EVENT.register((player, world, hand) -> {
			if(!player.getStackInHand(hand).isOf(Items.SLIME_BALL)) return TypedActionResult.pass(ItemStack.EMPTY);
			if(player.getShoulderEntityLeft().isEmpty() && player.getShoulderEntityRight().isEmpty()) return TypedActionResult.pass(ItemStack.EMPTY);

			LifeSizeBdubsPlayer bdubsPlayer = (LifeSizeBdubsPlayer) player;
			if(bdubsPlayer.lifesizebdubs$lastLockTicks() <= 20) return TypedActionResult.pass(ItemStack.EMPTY);
			boolean locked = player.getAttachedOrCreate(LOCKED_SHOULDER_ENTITY_ATTACHMENT, () -> false).booleanValue();
			boolean newLocked = !locked;
			player.setAttached(LOCKED_SHOULDER_ENTITY_ATTACHMENT, newLocked);
			bdubsPlayer.lifesizebdubs$setLastLockTicks(0);

			if(world.isClient()) return TypedActionResult.pass(ItemStack.EMPTY);
			if(newLocked) {
				player.sendMessage(Text.translatable("lifesizebdubs.entityshoulderlocked").formatted(Formatting.ITALIC), true);
				player.playSoundToPlayer(SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.NEUTRAL, 1f, 1f);
				((ServerWorld) player.getWorld()).spawnParticles(ParticleTypes.ITEM_SLIME, player.getX(), player.getY() + 0.15, player.getZ(), 7, 0, 0,0, 0);
			} else {
				player.sendMessage(Text.translatable("lifesizebdubs.entityshoulderunlocked").formatted(Formatting.ITALIC), true);
				player.playSoundToPlayer(SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.NEUTRAL, 1f, 1f);
				((ServerWorld) player.getWorld()).spawnParticles(ParticleTypes.DUST_PLUME, player.getX(), player.getY() + 0.15, player.getZ(), 7, 0, 0,0, 0);
			}
			return TypedActionResult.pass(ItemStack.EMPTY);
		});
	}

	public static <T extends Item> T registerItem(T item, String id) {
		Identifier itemId = Identifier.of(MOD_ID, id);
        return Registry.register(Registries.ITEM, itemId, item);
	}
}