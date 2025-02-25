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

//    public static final RegistryKey<BdubsVariant> PEARL = of("pearl");
//    public static final RegistryKey<BdubsVariant> GEM = of("gem");
//    public static final RegistryKey<BdubsVariant> GEM_SAILOR = of("sailor_gem");
//    public static final RegistryKey<BdubsVariant> JOEL = of("joel");
//    public static final RegistryKey<BdubsVariant> TANGO = of("tango");
//    public static final RegistryKey<BdubsVariant> IMPULSE = of("impulse");
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

        //TODO - imposter tnt
        registerable.register(BDUBS_TNT, new BdubsBuilder(
                Text.translatable("lifesizebdubs.variant.tnt"),
                Identifier.of(MOD_ID, "textures/bdubs/tntbdubs.png"),
                Items.TNT.getDefaultStack())
                .setTimedMessages(List.of(Pair.of(Text.of("AhhhAhhhh! Time to swheep!!!"), 12500)))
                .build()
        );

        registerable.register(SCAR, new BdubsBuilder(
                Text.translatable("lifesizebdubs.variant.scar"),
                Identifier.of(MOD_ID, "textures/bdubs/scar.png"),
                Items.ENCHANTING_TABLE.getDefaultStack())
                .build()
        );

        //TODO - imposter egg
        registerable.register(GRIAN, new BdubsBuilder(
                Text.translatable("lifesizebdubs.variant.grian"),
                Identifier.of(MOD_ID, "textures/bdubs/grian.png"),
                Items.EGG.getDefaultStack())
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
    }

    public static RegistryKey<BdubsVariant> of(String path) {
        return RegistryKey.of(LifeSizeBdubs.BDUBS_VARIANT_KEY, Identifier.of(MOD_ID, path));
    }

}
