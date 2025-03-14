package net.superkat.lifesizebdubs.entity.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.RotationAxis;
import net.superkat.lifesizebdubs.LifeSizeBdubs;
import net.superkat.lifesizebdubs.entity.BdubsEntity;
import net.superkat.lifesizebdubs.entity.BdubsShoulderHandler;

public class ShoulderBdubsRenderer<T extends PlayerEntity> extends FeatureRenderer<T, PlayerEntityModel<T>> {

    public ShoulderBdubsRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        renderMiniBdubs(matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch, true);
        renderMiniBdubs(matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch, false);
    }

    private void renderMiniBdubs(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, boolean leftShoulder) {
        NbtCompound nbt = leftShoulder ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
        EntityType.get(nbt.getString("id")).filter(entityType -> entityType == LifeSizeBdubs.BDUBS_ENTITY).ifPresent(entityType -> {
            matrices.push();
            BdubsEntity imposterBdubs = BdubsShoulderHandler.getImposter(player, leftShoulder);
            if(imposterBdubs != null) {
                BdubsEntityRenderer renderer = (BdubsEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(imposterBdubs);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f)); //flip upside to rightside up
                matrices.translate(leftShoulder ? 0.425F : -0.425F, player.isSneaking() ? -0.35F : -0.15f, 0.07F);

                if(imposterBdubs.getSugarTicks() > 0) {
                    double ticks = player.age;
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) (ticks * -75f)));
                }

                renderer.defaultRender(matrices, imposterBdubs, vertexConsumers, null, null, headYaw, player.age, light);
            }
            matrices.pop();
        });

    }
}
