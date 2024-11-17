package net.superkat.lifesizebdubs.data;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;
import java.util.Optional;

import static net.superkat.lifesizebdubs.LifeSizeBdubs.MODID;
import static net.superkat.lifesizebdubs.LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY;

//different class to keep things organized
public class DefaultBdubsVariants {

    public static final ResourceKey<BdubsVariant> BDUBS_DEFAULT_VARIANT = createBdubsKey("bdubs");
    public static final ResourceKey<BdubsVariant> BDUBS_MOSSY_VARIANT = createBdubsKey("mossy_bdubs");
    public static final ResourceKey<BdubsVariant> BDUBS_TNT_VARIANT = createBdubsKey("tnt_bdubs");

    public static final ResourceKey<BdubsVariant> SCAR_VARIANT = createBdubsKey("scar");
    public static final ResourceKey<BdubsVariant> GRIAN_VARIANT = createBdubsKey("grian");
    public static final ResourceKey<BdubsVariant> SKIZZ_VARIANT = createBdubsKey("skizz");
    public static final ResourceKey<BdubsVariant> ETHO_VARIANT = createBdubsKey("etho");
    public static final ResourceKey<BdubsVariant> TIMMY_VARIANT = createBdubsKey("timmy");

    public static final ResourceKey<BdubsVariant> TEST_VARIANT = createBdubsKey("booga");

    public static void bootstrap(BootstrapContext<BdubsVariant> bootstrap) {
        bootstrap.register(BDUBS_DEFAULT_VARIANT, BdubsVariant.DEFAULT);

        bootstrap.register(BDUBS_MOSSY_VARIANT, new BdubsVariant(
                Component.translatable("lifesizebdubs.variant.mossy"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/bdubs/mossybdubs.png"),
                Items.MOSS_BLOCK.getDefaultInstance(),
                List.of(),
                Optional.of(List.of((Pair.of("Uh oh! Time to swheep!", 12500))))
        ));

        bootstrap.register(BDUBS_TNT_VARIANT, new BdubsVariant(
                Component.translatable("lifesizebdubs.variant.tnt"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/bdubs/tntbdubs.png"),
                Items.TNT.getDefaultInstance(),
                List.of(),
                Optional.of(List.of((Pair.of("Ahhhh! Time to swheep!!!", 12500))))
        ));

        bootstrap.register(SCAR_VARIANT, new BdubsVariant(
                Component.translatable("lifesizebdubs.variant.scar"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/bdubs/scar.png"),
                Items.ENCHANTING_TABLE.getDefaultInstance()
        ));

        bootstrap.register(GRIAN_VARIANT, new BdubsVariant(
                Component.translatable("lifesizebdubs.variant.grian"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/bdubs/grian.png"),
                Items.EGG.getDefaultInstance()
        ));

        bootstrap.register(SKIZZ_VARIANT, new BdubsVariant(
                Component.translatable("lifesizebdubs.variant.skizz"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/bdubs/skizz.png"),
                Items.SNOWBALL.getDefaultInstance()
        ));

        bootstrap.register(ETHO_VARIANT, new BdubsVariant(
                Component.translatable("lifesizebdubs.variant.etho"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/bdubs/etho.png"),
                Items.LADDER.getDefaultInstance()
        ));

        bootstrap.register(TIMMY_VARIANT, new BdubsVariant(
                Component.translatable("lifesizebdubs.variant.timmy"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/bdubs/timmy.png"),
                Items.SADDLE.getDefaultInstance(),
                List.of("I'm a toy...", "I'm not a toy!")
        ));

        bootstrap.register(TEST_VARIANT, new BdubsVariant(
                Component.literal("Mini Booga"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/bdubs/booga.png"),
                Items.COMMAND_BLOCK.getDefaultInstance(),
                List.of("Booga message 1", "Booga message 2 electric boogaloo"),
                Optional.of(List.of(Pair.of("Timed booga!", 12500)))
        ));
    }

    public static ResourceKey<BdubsVariant> createBdubsKey(String path) {
        return ResourceKey.create(BDUBS_VARIANT_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(MODID, path));
    }

}
