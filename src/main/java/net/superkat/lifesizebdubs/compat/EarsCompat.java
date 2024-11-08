package net.superkat.lifesizebdubs.compat;

import com.mojang.blaze3d.platform.NativeImage;
import com.unascribed.ears.NativeImageAdapter;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.util.EarsStorage;
import net.minecraft.resources.ResourceLocation;

//remove the alpha on a skin texture when being used on a bdubs variant
public class EarsCompat {

//    public static Map<BdubsVariant, EarsFeatures> earsFeatures = Maps.newHashMap();

    //TODO - different class for skin patching
    //TODO - check for Ears installed, if so do this here
    //TODO - use IndirectEarsRenderDelegate and parsed EarsFeatures to render ears feature on Bdubs entity

    public static void patchBdubsTexture(NativeImage image, ResourceLocation location) {
//        for (EarsCommon.Rectangle rect : EarsCommon.FORCED_OPAQUE_REGIONS) {
//            setNoAlpha(image, rect.x1, rect.y1, rect.x2, rect.y2);
//        }
//        EarsStorage.put(image, EarsStorage.Key.ALFALFA, EarsCommon.preprocessSkin(new NativeImageAdapter(image)));
//        EarsCommon.carefullyStripAlpha((_x1, _y1, _x2, _y2) -> setNoAlpha(image, _x1, _y1, _x2, _y2), image.getHeight() != 32);

        EarsStorage.put(image, EarsStorage.Key.ALFALFA, EarsCommon.preprocessSkin(new NativeImageAdapter(image)));
        EarsCommon.carefullyStripAlpha((x1, y1, x2, y2) -> BdubsTexturePatcher.setNoAlpha(image, x1, y1, x2, y2), image.getHeight() != 32);
//        EarsFeatures features = EarsFeaturesParser.detect(new NativeImageAdapter(image), EarsStorage.get(image, EarsStorage.Key.ALFALFA), data -> new NativeImageAdapter(NativeImage.read(AbstractEarsRenderDelegate.toNativeBuffer(data))));
//        RegistryAccess registryAccess = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.registryAccess() : null;
//        if(registryAccess != null) {
//            List<BdubsVariant> variants = BdubsVariant.getVariantsFromTextureLocation(location, registryAccess);
//            if(variants.isEmpty()) return;
//            variants.forEach(variant -> earsFeatures.put(variant, features));
//        }
    }

//    @Nullable
//    public static EarsFeatures getEarsFeatures(BdubsVariant variant) {
//        return earsFeatures.get(variant);
//    }
//
//    public static void renderEarsFeaturesForVariant(BdubsEntityRenderer renderer, PoseStack poseStack, BdubsEntity animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
//        EarsFeatures features = getEarsFeatures(animatable.getVariant());
//        if(features != null) {
//
//        }
//    }

}
