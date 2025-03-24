package net.superkat.lifesizebdubs.data;

import com.mojang.datafixers.util.Pair;
import net.minecraft.item.Items;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.superkat.lifesizebdubs.LifeSizeBdubs;

import java.util.List;

import static net.superkat.lifesizebdubs.LifeSizeBdubs.MOD_ID;

public class BdubsVariants {
    public static final RegistryKey<BdubsVariant> BDUBS = of("bdubs");
    public static final RegistryKey<BdubsVariant> BDUBS_MOSSY = of("mossy_bdubs");
    public static final RegistryKey<BdubsVariant> BDUBS_TNT = of("tnt_bdubs");

    public static final RegistryKey<BdubsVariant> SCAR = of("scar");
    public static final RegistryKey<BdubsVariant> GRIAN = of("grian");
    public static final RegistryKey<BdubsVariant> SKIZZ = of("skizz");
    public static final RegistryKey<BdubsVariant> ETHO = of("etho");
    public static final RegistryKey<BdubsVariant> TIMMY = of("timmy");

    public static final RegistryKey<BdubsVariant> GEM = of("gem");
    public static final RegistryKey<BdubsVariant> JOEL = of("joel");
    public static final RegistryKey<BdubsVariant> TANGO = of("tango");
    public static final RegistryKey<BdubsVariant> PEARL = of("pearl");
    public static final RegistryKey<BdubsVariant> IMPULSE = of("impulse");
//
//    public static final RegistryKey<BdubsVariant> TEST = of("booga");

    public static void bootstrap(Registerable<BdubsVariant> registerable) {
        registerable.register(BDUBS, BdubsVariant.DEFAULT);

        registerable.register(BDUBS_MOSSY, new BdubsBuilder(
                Text.translatable("lifesizebdubs.variant.mossy"),
                Identifier.of(MOD_ID, "textures/bdubs/mossybdubs.png"),
                Items.MOSS_BLOCK.getDefaultStack())
                .setTimedMessages(List.of(Pair.of(Text.of("Uh oh! Time to swheep!"), 12500)))
                .build()
        );

        registerable.register(BDUBS_TNT, new BdubsBuilder(
                Text.translatable("lifesizebdubs.variant.tnt"),
                Identifier.of(MOD_ID, "textures/bdubs/tntbdubs.png"),
                Items.TNT.getDefaultStack())
                .setAltItem(LifeSizeBdubs.IMPOSTER_TNT.getDefaultStack())
                .setTimedMessages(List.of(Pair.of(Text.of("AhhhAhhhh! Time to swheep!!!"), 12500)))
                .build()
        );

        registerable.register(SCAR, new BdubsBuilder(
                Text.translatable("lifesizebdubs.variant.scar"),
                Identifier.of(MOD_ID, "textures/bdubs/scar.png"),
                Items.ENCHANTING_TABLE.getDefaultStack())
                .build()
        );

        registerable.register(GRIAN, new BdubsBuilder(
                Text.translatable("lifesizebdubs.variant.grian"),
                Identifier.of(MOD_ID, "textures/bdubs/grian.png"),
                Items.EGG.getDefaultStack())
                .setAltItem(LifeSizeBdubs.IMPOSTER_EGG.getDefaultStack())
                .build()
        );

        registerable.register(SKIZZ, new BdubsBuilder(
                Text.translatable("lifesizebdubs.variant.skizz"),
                Identifier.of(MOD_ID, "textures/bdubs/skizz.png"),
                Items.SNOWBALL.getDefaultStack())
                .build()
        );

        registerable.register(ETHO, new BdubsBuilder(
                Text.translatable("lifesizebdubs.variant.etho"),
                Identifier.of(MOD_ID, "textures/bdubs/etho.png"),
                Items.LADDER.getDefaultStack())
                .build()
        );

        registerable.register(TIMMY, new BdubsBuilder(
                Text.translatable("lifesizebdubs.variant.timmy"),
                Identifier.of(MOD_ID, "textures/bdubs/timmy.png"),
                Items.SADDLE.getDefaultStack())
                .setMessages(DefaultBdubsMessages.stringToText(List.of("I'm a toy...", "I'm not a toy!")))
                .build()
        );

        registerable.register(GEM, new BdubsBuilder(
                Text.translatable("lifesizebdubs.variant.gem"),
                Identifier.of(MOD_ID, "textures/bdubs/gem.png"),
                Items.HEART_OF_THE_SEA.getDefaultStack())
                .build()
        );

        registerable.register(JOEL, new BdubsBuilder(
                Text.translatable("lifesizebdubs.variant.joel"),
                Identifier.of(MOD_ID, "textures/bdubs/joel.png"),
                Items.VERDANT_FROGLIGHT.getDefaultStack())
                .build()
        );

        registerable.register(TANGO, new BdubsBuilder(
                Text.translatable("lifesizebdubs.variant.tango"),
                Identifier.of(MOD_ID, "textures/bdubs/tango.png"),
                Items.REDSTONE_BLOCK.getDefaultStack())
                .setMessages(DefaultBdubsMessages.stringToText(DefaultBdubsMessages.TANGO_DECKED_OUT_MESSAGES))
                .build()
        );

        registerable.register(PEARL, new BdubsBuilder(
                Text.translatable("lifesizebdubs.variant.pearl"),
                Identifier.of(MOD_ID, "textures/bdubs/pearl.png"),
                Items.SEA_PICKLE.getDefaultStack())
                .setAltItem(Items.PEARLESCENT_FROGLIGHT.getDefaultStack())
                .build()
        );

        registerable.register(IMPULSE, new BdubsBuilder(
                Text.translatable("lifesizebdubs.variant.impulse"),
                Identifier.of(MOD_ID, "textures/bdubs/impulse.png"),
                Items.REDSTONE.getDefaultStack())
                .build()
        );

//        debugBootstrap(registerable);
    }

    public static RegistryKey<BdubsVariant> of(String path) {
        return RegistryKey.of(LifeSizeBdubs.BDUBS_VARIANT_KEY, Identifier.of(MOD_ID, path));
    }

    public static final RegistryKey<BdubsVariant> STEVE = of("steve");
    public static final RegistryKey<BdubsVariant> ALEX = of("alex");
    public static final RegistryKey<BdubsVariant> ARI = of("ari");
    public static final RegistryKey<BdubsVariant> EFE = of("efe");
    public static final RegistryKey<BdubsVariant> KAI = of("kai");
    public static final RegistryKey<BdubsVariant> MAKENA = of("makena");
    public static final RegistryKey<BdubsVariant> NOOR = of("noor");
    public static final RegistryKey<BdubsVariant> SUNNY = of("sunny");
    public static final RegistryKey<BdubsVariant> ZURI = of("zuri");

    public static void debugBootstrap(Registerable<BdubsVariant> registerable) {
        registerable.register(STEVE, new BdubsBuilder(
                Text.of("Steve"),
                Identifier.ofVanilla("textures/entity/player/slim/steve.png"),
                Items.GOLDEN_APPLE.getDefaultStack())
                .build()
        );

        registerable.register(ALEX, new BdubsBuilder(
                Text.of("Alex"),
                Identifier.ofVanilla("textures/entity/player/slim/alex.png"),
                Items.GOLDEN_APPLE.getDefaultStack())
                .setAltItem(Items.DIAMOND_BOOTS.getDefaultStack())
                .build()
        );

        registerable.register(ARI, new BdubsBuilder(
                Text.of("Ari"),
                Identifier.ofVanilla("textures/entity/player/slim/ari.png"),
                Items.DIAMOND_BOOTS.getDefaultStack())
                .build()
        );

        registerable.register(EFE, new BdubsBuilder(
                Text.of("Efe"),
                Identifier.ofVanilla("textures/entity/player/slim/efe.png"),
                Items.SUGAR.getDefaultStack())
                .setAltItem(Items.AMETHYST_SHARD.getDefaultStack())
                .build()
        );

        registerable.register(KAI, new BdubsBuilder(
                Text.of("Kai"),
                Identifier.ofVanilla("textures/entity/player/slim/kai.png"),
                Items.AMETHYST_SHARD.getDefaultStack())
                .setAltItem(Items.SUGAR.getDefaultStack())
                .build()
        );

        registerable.register(MAKENA, new BdubsBuilder(
                Text.of("Makena"),
                Identifier.ofVanilla("textures/entity/player/slim/makena.png"),
                Items.AMETHYST_SHARD.getDefaultStack())
                .setAltItem(Items.DIAMOND.getDefaultStack())
                .build()
        );

        registerable.register(NOOR, new BdubsBuilder(
                Text.of("Noor"),
                Identifier.ofVanilla("textures/entity/player/slim/noor.png"),
                Items.DIAMOND.getDefaultStack())
                .setAltItem(Items.SUGAR.getDefaultStack())
                .build()
        );

        registerable.register(SUNNY, new BdubsBuilder(
                Text.of("Sunny"),
                Identifier.ofVanilla("textures/entity/player/slim/sunny.png"),
                Items.SUGAR.getDefaultStack())
                .setAltItem(Items.DIAMOND.getDefaultStack())
                .build()
        );

        registerable.register(ZURI, new BdubsBuilder(
                Text.of("Zuri"),
                Identifier.ofVanilla("textures/entity/player/slim/zuri.png"),
                Items.DIAMOND.getDefaultStack())
                .build()
        );
    }

}
