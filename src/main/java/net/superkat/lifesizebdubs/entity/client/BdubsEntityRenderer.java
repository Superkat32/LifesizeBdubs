package net.superkat.lifesizebdubs.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.superkat.lifesizebdubs.LifeSizeBdubs;
import net.superkat.lifesizebdubs.data.BdubsVariant;
import net.superkat.lifesizebdubs.entity.BdubsEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class BdubsEntityRenderer extends GeoEntityRenderer<BdubsEntity> {
    public BdubsEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BdubsEntityModel());
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Override
            protected @Nullable ItemStack getStackForBone(GeoBone bone, BdubsEntity animatable) {
                if(bone.getName().equals("item")) {
                    return animatable.getMainHandItem();
                }
                return null;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, BdubsEntity animatable) {
                return ItemDisplayContext.GROUND;
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, BdubsEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-180f));
                poseStack.translate(0f, 0f, -0.05f);
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
        this.withScale(0.25f);
    }

    @Override
    public ResourceLocation getTextureLocation(BdubsEntity animatable) {
        BdubsVariant variant = animatable.getVariant();
        return variant.texture();
    }

    public static class BdubsEntityModel extends DefaultedEntityGeoModel<BdubsEntity> {
        public BdubsEntityModel() {
            super(ResourceLocation.fromNamespaceAndPath(LifeSizeBdubs.MODID, "lifesizebdubs"), true);
            withAltTexture(ResourceLocation.withDefaultNamespace("textures/item/spyglass.png"));
        }

        @Override
        public void setCustomAnimations(BdubsEntity animatable, long instanceId, AnimationState<BdubsEntity> animationState) {
            //allows for head movement from animation and entity values(pitch, yaw) to work together(added I believe)
            GeoBone head = getAnimationProcessor().getBone("Head");

            if (head != null) {
                EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

                head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
                head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);

                if(animatable.onShoulder) {
                    head.setRotX(head.getRotX() - animatable.shoulderRidingPlayer.getXRot() * Mth.DEG_TO_RAD);
                    float netHeadYaw = animatable.shoulderRidingPlayer.getYHeadRot() - animatable.shoulderRidingPlayer.yBodyRot;
                    head.setRotY(head.getRotY() - netHeadYaw * Mth.DEG_TO_RAD);
                }
            }
        }
    }
}
