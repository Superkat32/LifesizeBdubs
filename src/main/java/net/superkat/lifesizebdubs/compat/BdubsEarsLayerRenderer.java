package net.superkat.lifesizebdubs.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.render.AbstractDetachedEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.render.IndirectEarsRenderDelegate;
import com.unascribed.ears.common.util.Decider;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.superkat.lifesizebdubs.LifeSizeBdubsClient;
import net.superkat.lifesizebdubs.entity.BdubsEntity;
import net.superkat.lifesizebdubs.entity.client.BdubsEntityRenderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class BdubsEarsLayerRenderer extends GeoRenderLayer<BdubsEntity> {

//    public GeoEarsRenderDelegate delegate = new GeoEarsRenderDelegate(this.getRenderer().getAnimatable(), this.getRenderer(), this);
    public GeoEarsDelegate delegate = new GeoEarsDelegate(this.renderer.getAnimatable(), this.renderer.getGeoModel()) {
        @Override
        protected VertexConsumer getVertexConsumer(TexSource src) {
            return super.getVertexConsumer(src);
        }
    };

    public BdubsEarsLayerRenderer(GeoRenderer<BdubsEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    //
//    @Override
//    public void render(PoseStack poseStack, BdubsEntity animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
//        delegate.render(poseStack, bufferSource, animatable, packedLight, LivingEntityRenderer.getOverlayCoords(animatable, 0f));
//    }


    @Override
    public void render(PoseStack poseStack, BdubsEntity animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if(LifeSizeBdubsClient.earsLoaded()) {
//            poseStack.mulPose(Axis.YP.rotationDegrees(180f));
//            poseStack.translate(0.25f, 3.05f, -0.35f);
//            poseStack.mulPose(Axis.ZP.rotationDegrees(180f)); //flip upside to rightside up
//            poseStack.rotateAround(Axis.ZP.rotationDegrees(180f), 0f, 0f, 0f);
//            poseStack.translate(0, 0.25f * 0.5f, 0);
            poseStack.pushPose();
//            poseStack.mulPose(Axis.XP.rotationDegrees((System.currentTimeMillis() % 5000) / 5000f * 360f)); //flip upside to rightside up
//            poseStack.mulPose(new Quaternionf().rotateZYX(0, (float) Math.toRadians(-animatable.yBodyRot), (float) Math.toRadians(180f)));
//            poseStack.translate(0, 1, 0);
            delegate.render(poseStack, bufferSource, animatable, packedLight, LivingEntityRenderer.getOverlayCoords(animatable, 0f));
            poseStack.popPose();
        }
    }
}
