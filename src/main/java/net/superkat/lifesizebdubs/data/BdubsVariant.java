package net.superkat.lifesizebdubs.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.superkat.lifesizebdubs.LifeSizeBdubs;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public record BdubsVariant(Component name, ResourceLocation texture, ItemStack item, Optional<List<String>> messages, Optional<List<Pair<String, Integer>>> timedMessages) {

    public BdubsVariant(Component name, ResourceLocation texture, ItemStack item) {
        this(name, texture, item, Optional.empty(), Optional.empty());
    }

    public BdubsVariant(Component name, ResourceLocation texture, ItemStack item, List<String> messages) {
        this(name, texture, item, Optional.of(messages), Optional.empty());
    }

    public BdubsVariant(Component name, ResourceLocation texture, ItemStack item, Optional<List<String>> messages) {
        this(name, texture, item, messages, Optional.empty());
    }

    public BdubsVariant(Component name, ResourceLocation texture, ItemStack item, List<String> messages, Optional<List<Pair<String, Integer>>> timedMessages) {
        this(name, texture, item, Optional.of(messages), timedMessages);
    }

    public static final Codec<BdubsVariant> DIRECT_CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                ComponentSerialization.CODEC.fieldOf("name").forGetter(BdubsVariant::name),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(BdubsVariant::texture),
                ItemStack.SIMPLE_ITEM_CODEC.fieldOf("item").forGetter(BdubsVariant::item),
                Codec.STRING.listOf().optionalFieldOf("messages").forGetter(BdubsVariant::messages),
                Codec.mapPair(
                        Codec.STRING.fieldOf("msg"),
                        Codec.intRange(0, 24000).fieldOf("time")
                ).codec().listOf().optionalFieldOf("timed_messages").forGetter(BdubsVariant::timedMessages)
        ).apply(instance, BdubsVariant::new);
    });
    //even though this isn't used, I believe it is needed?
    public static final Codec<Holder<BdubsVariant>> CODEC = RegistryFileCodec.create(LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY, DIRECT_CODEC);

    public static BdubsVariant DEFAULT = new BdubsVariant(
            Component.translatable("lifesizebdubs.variant.bdubs"),
            ResourceLocation.fromNamespaceAndPath(LifeSizeBdubs.MODID, "textures/bdubs/bdubs.png"),
            Items.CLOCK.getDefaultInstance(),
            Optional.of(DefaultBdubsMessages.DEFAULT_BDUBS_VARIANT_MESSAGES),
            Optional.of(List.of((Pair.of("Time to swheep!", 12500))))
    );

    public static BdubsVariant getVariantFromInt(int integer, RegistryAccess registryAccess) {
        Optional<Holder.Reference<BdubsVariant>> holder = registryAccess.registryOrThrow(LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY).getHolder(integer);
        return holder.map(Holder.Reference::value).orElseGet(() -> DEFAULT);
    }

    public static int getIntFromVariant(BdubsVariant variant, RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY).getId(variant);
    }

    @Nullable
    public static BdubsVariant getVariantFromItem(ItemStack item, RegistryAccess registryAccess) {
        BdubsVariant returnVariant = null;
        Registry<BdubsVariant> registry = registryAccess.registryOrThrow(LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY);
        for (BdubsVariant variant : registry.stream().toList()) {
            if(variant.item.is(item.getItem())) {
                returnVariant = variant;
                break;
            }
        }
        return returnVariant;
    }

    @Nullable
    public static BdubsVariant getVariantFromCompoundTag(CompoundTag compound, RegistryAccess registryAccess) {
        AtomicReference<BdubsVariant> returnVariant = new AtomicReference<>();
        Optional.ofNullable(ResourceLocation.tryParse(compound.getString("variant")))
                .map(resourceLocation -> ResourceKey.create(LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY, resourceLocation))
                .flatMap(bdubsVariantResourceKey -> registryAccess.registryOrThrow(LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY).getHolder((ResourceKey<BdubsVariant>) bdubsVariantResourceKey))
                .ifPresent(bdubsVariantReference -> {
                    returnVariant.set(bdubsVariantReference.value());
                });
        return returnVariant.get();
    }

    public static List<BdubsVariant> getVariantsFromTextureLocation(ResourceLocation location, RegistryAccess registryAccess) {
        Registry<BdubsVariant> registry = registryAccess.registryOrThrow(LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY);
        List<BdubsVariant> variants = new ArrayList<>();
        for (BdubsVariant variant : registry.stream().toList()) {
            if(variant.texture.equals(location)) {
                variants.add(variant);
            }
        }
        return variants;
    }
}
