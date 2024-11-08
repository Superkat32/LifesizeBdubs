package net.superkat.lifesizebdubs.compat;

import com.mojang.blaze3d.platform.NativeImage;
import com.unascribed.ears.NativeImageAdapter;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.util.EarsStorage;
import net.minecraft.resources.ResourceLocation;

public class EarsCompat {

    //Check bindings - resource location "location()" refers to registry key instead of texture()?
    //flip matrices upside down 180 degrees instead of translating up by 5 or whatever

    public static void patchBdubsTexture(NativeImage image, ResourceLocation location) {
        // I think this is important but idk
        EarsStorage.put(image, EarsStorage.Key.ALFALFA, EarsCommon.preprocessSkin(new NativeImageAdapter(image)));
        EarsCommon.carefullyStripAlpha((x1, y1, x2, y2) -> BdubsTexturePatcher.setNoAlpha(image, x1, y1, x2, y2), image.getHeight() != 32);

        //location would be used to map variant to EarsFeatures, but because I'm not fully supporting Ears
        //because I couldn't get it working, there is no need right now.
    }

}
