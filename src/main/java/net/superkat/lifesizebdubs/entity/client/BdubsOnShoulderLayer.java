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
import net.superkat.lifesizebdubs.data.BdubsVariant;
import net.superkat.lifesizebdubs.entity.BdubsEntity;

public class BdubsOnShoulderLayer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {
    private BdubsEntity imposterBdubsLeft = null;
    private BdubsEntity imposterBdubsRight = null;
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
                .ifPresentOrElse(entityType -> {
                    BdubsEntity imposterBdubs = leftShoulder ? imposterBdubsLeft : imposterBdubsRight;
                    poseStack.pushPose();
                    BdubsVariant bdubsVariant = BdubsVariant.getVariantFromCompoundTag(compoundTag, livingEntity.registryAccess());
                    if(bdubsVariant == null) {
                        bdubsVariant = BdubsVariant.DEFAULT;
                    }
                    if(imposterBdubs == null) {
                        imposterBdubs = (BdubsEntity) EntityType.create(compoundTag, livingEntity.level()).get();
                        imposterBdubs.setVariant(bdubsVariant);
                        imposterBdubs.setOnShoulder(true, livingEntity);
                    }

                    BdubsEntityRenderer renderer = (BdubsEntityRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(imposterBdubs);

                    poseStack.mulPose(Axis.YP.rotationDegrees(180f));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
                    poseStack.translate(leftShoulder ? 0.4F : -0.4F, livingEntity.isCrouching() ? -0.2F : 0f, 0.0F);

                    renderer.defaultRender(poseStack, imposterBdubs, buffer, null, null, netHeadYaw, livingEntity.tickCount, packedLight);

                    poseStack.popPose();

                    //cache entity I guess maybe perhaps perchance
                    //cursed if else statements but it works?
                    if(leftShoulder) imposterBdubsLeft = imposterBdubs;
                    else imposterBdubsRight = imposterBdubs;
                }, () -> {
                    if(leftShoulder) imposterBdubsLeft = null;
                    else imposterBdubsRight = null;
                });
    }
}
