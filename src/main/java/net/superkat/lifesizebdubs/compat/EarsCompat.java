package net.superkat.lifesizebdubs.compat;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.NativeImage;
import com.unascribed.ears.NativeImageAdapter;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesParser;
import com.unascribed.ears.common.render.AbstractEarsRenderDelegate;
import com.unascribed.ears.common.util.EarsStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.superkat.lifesizebdubs.data.BdubsVariant;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class EarsCompat {

    public static Map<BdubsVariant, EarsFeatures> earsFeatures = Maps.newHashMap();

    //Check bindings - resource location "location()" refers to registry key instead of texture()?
    //flip matrices upside down 180 degrees instead of translating up by 5 or whatever

    public static void patchBdubsTexture(NativeImage image, ResourceLocation location) {
        // I think this is important but idk
        EarsStorage.put(image, EarsStorage.Key.ALFALFA, EarsCommon.preprocessSkin(new NativeImageAdapter(image)));
        EarsCommon.carefullyStripAlpha((x1, y1, x2, y2) -> BdubsTexturePatcher.setNoAlpha(image, x1, y1, x2, y2), image.getHeight() != 32);
        EarsFeatures features = EarsFeaturesParser.detect(new NativeImageAdapter(image), EarsStorage.get(image, EarsStorage.Key.ALFALFA), data -> new NativeImageAdapter(NativeImage.read(AbstractEarsRenderDelegate.toNativeBuffer(data))));
        RegistryAccess registryAccess = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.registryAccess() : null;
        if(registryAccess != null) {
            List<BdubsVariant> variants = BdubsVariant.getVariantsFromTextureLocation(location, registryAccess);
            if(variants.isEmpty()) return;
            variants.forEach(variant -> earsFeatures.put(variant, features));
        }

        //location would be used to map variant to EarsFeatures, but because I'm not fully supporting Ears
        //because I couldn't get it working, there is no need right now.
    }

    @Nullable
    public static EarsFeatures getEarsFeatures(BdubsVariant variant) {
        return earsFeatures.get(variant);
    }

}
