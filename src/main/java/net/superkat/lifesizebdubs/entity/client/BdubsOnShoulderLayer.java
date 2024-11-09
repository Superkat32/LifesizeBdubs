package net.superkat.lifesizebdubs.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.superkat.lifesizebdubs.LifeSizeBdubs;
import net.superkat.lifesizebdubs.entity.BdubsEntity;
import net.superkat.lifesizebdubs.entity.BdubsShoulderHandler;

public class BdubsOnShoulderLayer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {
//    private BdubsEntity imposterBdubsLeft = null;
//    private BdubsEntity imposterBdubsRight = null;
    public BdubsOnShoulderLayer(RenderLayerParent<T, PlayerModel<T>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
         this.renderMiniBdubs(poseStack, bufferSource, packedLight, livingEntity, limbSwing, limbSwingAmount, netHeadYaw, headPitch, true);
         this.renderMiniBdubs(poseStack, bufferSource, packedLight, livingEntity, limbSwing, limbSwingAmount, netHeadYaw, headPitch, false);
    }

    //haha
    private void renderMiniBdubs(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch, boolean leftShoulder) {
        CompoundTag compoundTag = leftShoulder ? livingEntity.getShoulderEntityLeft() : livingEntity.getShoulderEntityRight();
        EntityType.byString(compoundTag.getString("id"))
                .filter(entityType -> entityType == LifeSizeBdubs.BDUBS_ENTITY.get())
                .ifPresent(entityType -> {
                    poseStack.pushPose();
                    BdubsEntity imposterBdubs = BdubsShoulderHandler.getImposterBdubs(livingEntity, leftShoulder);
//                    BdubsEntity imposterBdubs = BdubsShoulderHandler.getImposterBdubs(leftShoulder);
                    if(imposterBdubs != null) {
                        BdubsEntityRenderer renderer = (BdubsEntityRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(imposterBdubs);
                        if(renderer.getAnimatable() == null) return; //idk

                        poseStack.mulPose(Axis.YP.rotationDegrees(180f));
                        poseStack.mulPose(Axis.ZP.rotationDegrees(180f)); //flip upside to rightside up
                        poseStack.translate(leftShoulder ? 0.425F : -0.425F, livingEntity.isCrouching() ? -0.35F : -0.15f, 0.07F);

                        if(imposterBdubs.getSugarTicks() > 0) {
                            double ticks = (double) livingEntity.level().getDayTime();
                            poseStack.mulPose(Axis.YP.rotationDegrees((float) (ticks * -75f)));
                        }

                        renderer.defaultRender(poseStack, imposterBdubs, buffer, null, null, netHeadYaw, livingEntity.tickCount, packedLight);
                    }

                    poseStack.popPose();
                });
    }
}
