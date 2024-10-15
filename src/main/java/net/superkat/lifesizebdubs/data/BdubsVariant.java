package net.superkat.lifesizebdubs.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.superkat.lifesizebdubs.LifeSizeBdubs;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public record BdubsVariant(String name, ResourceLocation texture, ItemStack item) {
    public static final Codec<BdubsVariant> DIRECT_CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.STRING.fieldOf("name").forGetter(BdubsVariant::name),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(BdubsVariant::texture),
                ItemStack.SIMPLE_ITEM_CODEC.fieldOf("item").forGetter(BdubsVariant::item)
        ).apply(instance, BdubsVariant::new);
    });
    //even though this isn't used, I believe it is needed?
    public static final Codec<Holder<BdubsVariant>> CODEC = RegistryFileCodec.create(LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY, DIRECT_CODEC);

    public static BdubsVariant DEFAULT = new BdubsVariant(
            "bdubs",
            ResourceLocation.fromNamespaceAndPath(LifeSizeBdubs.MODID, "textures/bdubs/bdubs.png"),
            Items.CLOCK.getDefaultInstance()
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
}
