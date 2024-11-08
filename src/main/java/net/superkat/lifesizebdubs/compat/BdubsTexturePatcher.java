package net.superkat.lifesizebdubs.compat;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.superkat.lifesizebdubs.LifeSizeBdubsClient;

public class BdubsTexturePatcher {

    public static void patchBdubsTexture(NativeImage image) {
        if(LifeSizeBdubsClient.earsLoaded()) {
            EarsCompat.patchBdubsTexture(image);
        } else {
            setNoAlpha(image, 0, 0, 32, 16);
            setNoAlpha(image, 0, 16, 64, 32);
            setNoAlpha(image, 16, 48, 48, 64);
        }
    }

    /**
     * Sets the specified pixels to remove alpha.
     * Copy-pasted from {@link HttpTexture#setNoAlpha(NativeImage, int, int, int, int)}
     */
    public static void setNoAlpha(NativeImage image, int x, int y, int width, int height) {
        for (int i = x; i < width; i++) {
            for (int j = y; j < height; j++) {
                image.setPixelRGBA(i, j, image.getPixelRGBA(i, j) | 0xFF000000);
            }
        }
    }

}
