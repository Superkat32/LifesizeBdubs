package net.superkat.lifesizebdubs.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.superkat.lifesizebdubs.LifeSizeBdubs;
import net.superkat.lifesizebdubs.compat.BdubsEarsLayerRenderer;
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

        addRenderLayer(new BdubsEarsLayerRenderer(this));
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
        public @Nullable RenderType getRenderType(BdubsEntity animatable, ResourceLocation texture) {
            return RenderType.entityTranslucent(texture);
        }

        @Override
        public void setCustomAnimations(BdubsEntity animatable, long instanceId, AnimationState<BdubsEntity> animationState) {
            //allows for head movement from animation and entity values(pitch, yaw) to work together(added I believe)
            GeoBone head = getAnimationProcessor().getBone("Head");

            if (head != null) {
                EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

                head.setRotX(head.getRotX() + entityData.headPitch() * Mth.DEG_TO_RAD);
                head.setRotY(head.getRotY() + entityData.netHeadYaw() * Mth.DEG_TO_RAD);

                if(animatable.onShoulder) {
                    head.setRotX(head.getRotX() / 2 - animatable.shoulderRidingPlayer.getXRot() * Mth.DEG_TO_RAD);
                    float netHeadYaw = animatable.shoulderRidingPlayer.getYHeadRot() - animatable.shoulderRidingPlayer.yBodyRot;
                    head.setRotY(head.getRotY() / 2 - netHeadYaw * Mth.DEG_TO_RAD);
                }
            }
            setLegRotation(animatable, instanceId, animationState);
        }

        //leg movement handled by code so it can be added ontop of normal leg movement from animations + i'm lazy to recreate all the animations for sitting
        private void setLegRotation(BdubsEntity bdubs, long instanceId, AnimationState<BdubsEntity> animState) {
            GeoBone leftLeg = getAnimationProcessor().getBone("LeftLeg");
            GeoBone rightLeg = getAnimationProcessor().getBone("RightLeg");
            float leftLegRotX = leftLeg.getRotX();
            float rightLegRotX = rightLeg.getRotX();
            float limbSwing = animState.getLimbSwing();
            float limbSwingAmount = animState.getLimbSwingAmount();

            if(bdubs.onShoulder) {
                float sittingRot = (float) Math.toRadians(75);
                float ticks = bdubs.shoulderRidingPlayer.level().getDayTime();
                float extraRot = (float) Math.cos((double) ticks / 5f) / 4f;
                leftLeg.setRotX((float) (leftLegRotX / 2f + sittingRot + extraRot));
                rightLeg.setRotX((float) (rightLegRotX / 2f + sittingRot - extraRot));
            } else {
                //magic numbers from HumanoidModel#setupAnim without multiplying by 1.4f to move less because bdubs has tiny legs lol
                leftLeg.setRotX((float) (leftLegRotX + (Math.cos(limbSwing * 0.6662f) * limbSwingAmount)));
                rightLeg.setRotX((float) (rightLegRotX + (Math.cos(limbSwing * 0.6662f + Math.PI) * limbSwingAmount)));
            }
        }
    }
}
