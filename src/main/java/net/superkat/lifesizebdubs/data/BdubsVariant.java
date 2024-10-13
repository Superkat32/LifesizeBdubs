package net.superkat.lifesizebdubs.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.superkat.lifesizebdubs.LifeSizeBdubs;

import java.util.Optional;

public record BdubsVariant(ResourceLocation texture, String name) {
    public static final Codec<BdubsVariant> DIRECT_CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(BdubsVariant::texture),
                Codec.STRING.fieldOf("name").forGetter(BdubsVariant::name)).apply(instance, BdubsVariant::new);
    });
    public static final Codec<Holder<BdubsVariant>> CODEC = RegistryFileCodec.create(LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY, DIRECT_CODEC);
    public static final StreamCodec<ByteBuf, BdubsVariant> DIRECT_STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, BdubsVariant::texture, ByteBufCodecs.STRING_UTF8, BdubsVariant::name, BdubsVariant::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<BdubsVariant>> STREAM_CODEC = ByteBufCodecs.holder(LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY, DIRECT_STREAM_CODEC);

    public static BdubsVariant DEFAULT = new BdubsVariant(ResourceLocation.fromNamespaceAndPath(LifeSizeBdubs.MODID, "textures/bdubs/bdubs.png"), "bdubs");

    public static BdubsVariant getVariantFromInt(int integer, RegistryAccess registryAccess) {
        Optional<Holder.Reference<BdubsVariant>> holder = registryAccess.registryOrThrow(LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY).getHolder(integer);
        if(holder.isPresent()) {
            return holder.get().value();
        }
        //TODO - default return
        return DEFAULT;
    }

    public static int getIntFromVariant(BdubsVariant variant, RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY).getId(variant);
    }
}
