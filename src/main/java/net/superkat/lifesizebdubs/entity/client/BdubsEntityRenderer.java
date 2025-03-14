package net.superkat.lifesizebdubs.entity.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
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
    public BdubsEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BdubsEntityModel());

        this.addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Override
            protected @Nullable ItemStack getStackForBone(GeoBone bone, BdubsEntity animatable) {
                if(bone.getName().equals("item")) return animatable.getMainHandStack();
                return null;
            }

            @Override
            protected ModelTransformationMode getTransformTypeForStack(GeoBone bone, ItemStack stack, BdubsEntity animatable) {
                return ModelTransformationMode.GROUND;
            }

            @Override
            protected void renderStackForBone(MatrixStack poseStack, GeoBone bone, ItemStack stack, BdubsEntity animatable, VertexConsumerProvider bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-180f));
                poseStack.translate(0, 0, -0.05f);
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });

        this.withScale(0.25f);
    }

    @Override
    public Identifier getTextureLocation(BdubsEntity animatable) {
        ProfileComponent profileComponent = animatable.getProfile();
        if(profileComponent != null) {
            PlayerSkinProvider skinProvider = MinecraftClient.getInstance().getSkinProvider();
            return skinProvider.getSkinTextures(profileComponent.gameProfile()).texture();
        } else {
            BdubsVariant variant = animatable.getVariant();
            return variant.getTexture();
        }

    }

    public static class BdubsEntityModel extends DefaultedEntityGeoModel<BdubsEntity> {
        public BdubsEntityModel() {
            super(Identifier.of(LifeSizeBdubs.MOD_ID, "lifesizebdubs"), true);
            withAltTexture(Identifier.ofVanilla("player/slim/steve")); //fallback texture
        }

        @Override
        public @Nullable RenderLayer getRenderType(BdubsEntity animatable, Identifier texture) {
            return RenderLayer.getEntityTranslucent(texture);
        }

        @Override
        public void setCustomAnimations(BdubsEntity animatable, long instanceId, AnimationState<BdubsEntity> animationState) {
            //allows for head movement from animation and entity values(pitch, yaw) to work together(added I believe)
            GeoBone head = getAnimationProcessor().getBone("Head");

            if (head != null) {
                if(animatable.isDead()) return;
                EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

                head.setRotX(head.getRotX() + entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
                head.setRotY(head.getRotY() + entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);

                if(animatable.onShoulder) {
                    head.setRotX(head.getRotX() / 2 - animatable.shoulderRidingPlayer.getPitch() * MathHelper.RADIANS_PER_DEGREE);
                    float netHeadYaw = animatable.shoulderRidingPlayer.getHeadYaw() - animatable.shoulderRidingPlayer.getBodyYaw();
                    head.setRotY(head.getRotY() / 2 - netHeadYaw * MathHelper.RADIANS_PER_DEGREE);
                }
            }
            setLegRotation(animatable, instanceId, animationState);
        }

        private void setLegRotation(BdubsEntity bdubs, long instanceId, AnimationState<BdubsEntity> animState) {
            GeoBone leftLeg = getAnimationProcessor().getBone("LeftLeg");
            GeoBone rightLeg = getAnimationProcessor().getBone("RightLeg");
            float leftLegRotX = leftLeg.getRotX();
            float rightLegRotX = rightLeg.getRotX();
            float limbSwing = animState.getLimbSwing();
            float limbSwingAmount = animState.getLimbSwingAmount();

            if(bdubs.onShoulder) {
                float sittingRot = (float) Math.toRadians(75);
                float ticks = bdubs.shoulderRidingPlayer.age;
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
