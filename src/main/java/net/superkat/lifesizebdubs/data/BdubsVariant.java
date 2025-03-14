package net.superkat.lifesizebdubs.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.superkat.lifesizebdubs.LifeSizeBdubs;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class BdubsVariant {

    public static final Codec<BdubsVariant> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    TextCodecs.CODEC.fieldOf("name").forGetter(bdubs -> bdubs.name),
                    Identifier.CODEC.fieldOf("texture").forGetter(bdubs -> bdubs.texture),
                    ItemStack.CODEC.fieldOf("item").forGetter(bdubs -> bdubs.item),
                    ItemStack.CODEC.optionalFieldOf("alt_item").forGetter(bdubs -> bdubs.altItem),
                    TextCodecs.CODEC.listOf().optionalFieldOf("messages").forGetter(bdubs -> bdubs.messages),
                    Codec.mapPair(
                            TextCodecs.CODEC.fieldOf("msg"),
                            Codec.intRange(0, 24000).fieldOf("time")
                    ).codec().listOf().optionalFieldOf("timed_messages").forGetter(bdubs -> bdubs.timedMessages)
            ).apply(instance, BdubsVariant::new)
    );

    //Doesn't get used, but still required(I think)
    public static final Codec<RegistryEntry<BdubsVariant>> ENTRY_CODEC = RegistryElementCodec.of(LifeSizeBdubs.BDUBS_VARIANT_KEY, CODEC);

    public final Text name;
    public final Identifier texture;
    public final ItemStack item;

    public final Optional<ItemStack> altItem;

    public final Optional<List<Text>> messages;
    public final Optional<List<Pair<Text, Integer>>> timedMessages;

    public BdubsVariant(Text name, Identifier texture, ItemStack item, Optional<ItemStack> altItem, Optional<List<Text>> messages, Optional<List<Pair<Text, Integer>>> timedMessages) {
        this.name = name;
        this.texture = texture;
        this.item = item;
        this.altItem = altItem;
        this.messages = messages;
        this.timedMessages = timedMessages;
    }

    public static BdubsVariant DEFAULT = new BdubsBuilder(
            Text.translatable("lifesizebdubs.variant.bdubs"),
            Identifier.of(LifeSizeBdubs.MOD_ID, "textures/bdubs/bdubs.png"),
            Items.CLOCK.getDefaultStack())
            .setMessages(DefaultBdubsMessages.stringToText(DefaultBdubsMessages.DEFAULT_BDUBS_VARIANT_MESSAGES))
            .setTimedMessages(List.of(Pair.of(Text.of("Time to swheep!"), 12500)))
            .build();

    public static BdubsVariant fromId(DynamicRegistryManager registryManager, int id) {
        Optional<RegistryEntry.Reference<BdubsVariant>> holder = registryManager.get(LifeSizeBdubs.BDUBS_VARIANT_KEY).getEntry(id);
        return holder.map(RegistryEntry.Reference::value).orElseGet(() -> DEFAULT);
    }

    public static int toId(DynamicRegistryManager registryManager, BdubsVariant variant) {
        return registryManager.get(LifeSizeBdubs.BDUBS_VARIANT_KEY).getRawId(variant);
    }

    public static BdubsVariant fromNbt(DynamicRegistryManager registryManager, NbtCompound nbt) {
        AtomicReference<BdubsVariant> returnVariant = new AtomicReference<>();
        Optional.ofNullable(Identifier.tryParse(nbt.getString("variant")))
                .map(id -> RegistryKey.of(LifeSizeBdubs.BDUBS_VARIANT_KEY, id))
                .flatMap(variantKey -> registryManager.get(LifeSizeBdubs.BDUBS_VARIANT_KEY).getEntry(variantKey))
                .ifPresent(bdubsVariantReference -> returnVariant.set(bdubsVariantReference.value()));
        return returnVariant.get();
    }

    public static Set<BdubsVariant> variantsWithItem(DynamicRegistryManager registryManager, ItemStack item) {
        Registry<BdubsVariant> registry = registryManager.get(LifeSizeBdubs.BDUBS_VARIANT_KEY);
        return registry.stream().toList().stream()
                .filter(variant ->
                        variant.getItem().isOf(item.getItem())
                                || (variant.getAltItem().isPresent() && variant.getAltItem().get().isOf(item.getItem()))
                ).collect(Collectors.toSet());
    }

    //i love intellij
    public Text getName() {
        return name;
    }

    public Identifier getTexture() {
        return texture;
    }

    public ItemStack getItem() {
        return item;
    }

    public Optional<ItemStack> getAltItem() {
        return altItem;
    }

    public Optional<List<Text>> getMessages() {
        return messages;
    }

    public Optional<List<Pair<Text, Integer>>> getTimedMessages() {
        return timedMessages;
    }
}

