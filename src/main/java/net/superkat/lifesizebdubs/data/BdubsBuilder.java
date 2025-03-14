package net.superkat.lifesizebdubs.data;

import com.mojang.datafixers.util.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

//i love intellij - it auto generated pretty much all of this!
//Build A Bdubs!
public class BdubsBuilder {
    private final Text name;
    private final Identifier texture;
    private final ItemStack item;
    private Optional<ItemStack> altItem = empty();
    private Optional<List<Text>> messages = empty();
    private Optional<List<Pair<Text, Integer>>> timedMessages = empty();

    public BdubsBuilder(String name, Identifier texture, ItemStack item) {
        this(Text.of(name), texture, item);
    }

    public BdubsBuilder(Text name, Identifier texture, ItemStack item) {
        this.name = name;
        this.texture = texture;
        this.item = item;
    }

    public BdubsBuilder setAltItem(ItemStack altItem) {
        this.altItem = of(altItem);
        return this;
    }

    public BdubsBuilder setMessages(List<Text> messages) {
        this.messages = of(messages);
        return this;
    }

    public BdubsBuilder setTimedMessages(List<Pair<Text, Integer>> timedMessages) {
        this.timedMessages = of(timedMessages);
        return this;
    }

    public BdubsVariant build() {
        return new BdubsVariant(name, texture, item, altItem, messages, timedMessages);
    }

    private static<T> Optional<T> empty() { //easier to type faster
        return Optional.empty();
    }

    private static<T> Optional<T> of(T value) { //easier to type faster
        return Optional.of(value);
    }
}