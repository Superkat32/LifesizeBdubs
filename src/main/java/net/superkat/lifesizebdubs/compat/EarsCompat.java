package net.superkat.lifesizebdubs.compat;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.ears.NativeImageAdapter;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeaturesParser;
import com.unascribed.ears.common.render.AbstractEarsRenderDelegate;
import com.unascribed.ears.common.util.EarsStorage;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.superkat.lifesizebdubs.data.BdubsVariant;
import net.superkat.lifesizebdubs.data.DefaultBdubsVariants;
import net.superkat.lifesizebdubs.entity.BdubsEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import java.util.Map;

//remove the alpha on a skin texture when being used on a bdubs variant
public class EarsCompat {

    public static Map<BdubsVariant, EarsFeatures> earsFeatures = Maps.newHashMap();

    //TODO - different class for skin patching
    //TODO - check for Ears installed, if so do this here
    //TODO - use IndirectEarsRenderDelegate and parsed EarsFeatures to render ears feature on Bdubs entity

    public static void patchBdubsTexture(NativeImage image) {
//        for (EarsCommon.Rectangle rect : EarsCommon.FORCED_OPAQUE_REGIONS) {
//            setNoAlpha(image, rect.x1, rect.y1, rect.x2, rect.y2);
//        }
//        EarsStorage.put(image, EarsStorage.Key.ALFALFA, EarsCommon.preprocessSkin(new NativeImageAdapter(image)));
//        EarsCommon.carefullyStripAlpha((_x1, _y1, _x2, _y2) -> setNoAlpha(image, _x1, _y1, _x2, _y2), image.getHeight() != 32);

        EarsStorage.put(image, EarsStorage.Key.ALFALFA, EarsCommon.preprocessSkin(new NativeImageAdapter(image)));
        EarsCommon.carefullyStripAlpha((x1, y1, x2, y2) -> BdubsTexturePatcher.setNoAlpha(image, x1, y1, x2, y2), image.getHeight() != 32);
        EarsFeatures features = EarsFeaturesParser.detect(new NativeImageAdapter(image), EarsStorage.get(image, EarsStorage.Key.ALFALFA), data -> new NativeImageAdapter(NativeImage.read(AbstractEarsRenderDelegate.toNativeBuffer(data))));
        earsFeatures.put(BdubsVariant.DEFAULT, features);
    }

    @Nullable
    public static EarsFeatures getEarsFeatures(GeoEntity entity) {
//        BdubsVariant variant = ((BdubsEntity) entity).getVariant();
        return earsFeatures.get(BdubsVariant.DEFAULT);
    }

    public static void renderEarsFeaturesForVariant(PoseStack poseStack, BdubsEntity animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        EarsFeatures features = getEarsFeatures()
    }



}
