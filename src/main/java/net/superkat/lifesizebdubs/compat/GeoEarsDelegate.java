package net.superkat.lifesizebdubs.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.render.IndirectEarsRenderDelegate;
import com.unascribed.ears.common.util.Decider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.superkat.lifesizebdubs.data.BdubsVariant;
import net.superkat.lifesizebdubs.data.DefaultBdubsVariants;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public abstract class GeoEarsDelegate extends IndirectEarsRenderDelegate<PoseStack, MultiBufferSource, VertexConsumer, GeoEntity, GeoBone> {
    protected GeoEntity entity;
    protected GeoModel<?> model;
    public GeoEarsDelegate(GeoEntity entity, GeoModel<?> model) {
        this.entity = entity;
        this.model = model;
    }

    @Override
    protected VertexConsumer getVertexConsumer(TexSource src) {
//        this.armorR = this.armorG = this.armorB = this.armorA = 1.0F;
        ResourceLocation id = DefaultBdubsVariants.TEST_VARIANT.location();
//        ResourceLocation id = ((AbstractClientPlayer)this.peer).getSkin().texture();
//        if (src != TexSource.SKIN) {
//            id = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), src.addSuffix(id.getPath()));
//        }

        return ((MultiBufferSource)this.vcp).getBuffer(RenderType.entityTranslucentCull(id));
    }

    @Override
    protected void commitQuads() {
        if (this.vcp instanceof MultiBufferSource.BufferSource) {
            ((MultiBufferSource.BufferSource)this.vcp).endLastBatch();
        }
    }

    @Override
    protected void doUploadAux(TexSource texSource, byte[] bytes) {

    }

    @Override
    protected EarsFeatures getEarsFeatures() {
        return EarsCompat.getEarsFeatures(this.entity);
    }

    @Override
    protected void pushMatrix() {
        this.matrices.pushPose();
    }

    @Override
    protected void popMatrix() {
        this.matrices.popPose();
    }

    @Override
    protected boolean isVisible(GeoBone geoBone) {
        return !geoBone.isHidden();
    }

    @Override
    protected Decider<BodyPart, GeoBone> decideModelPart(Decider<BodyPart, GeoBone> decider) {
        return decider.map(BodyPart.HEAD, model.getBone("Head").get());
    }

    @Override
    protected void doAnchorTo(BodyPart bodyPart, GeoBone geoBone) {
        this.matrices.translate(geoBone.getPosX(), geoBone.getPosY() + 3, geoBone.getPosZ() + 2);
        if(geoBone.getRotX() != 0.0f || geoBone.getRotY() != 0.0f || geoBone.getRotZ() != 0.0f) {
            this.matrices.mulPose(new Quaternionf().rotationZYX(geoBone.getRotZ(), geoBone.getRotY(), geoBone.getRotX()));
        }
        if(geoBone.getScaleX() != 1.0f || geoBone.getScaleY() != 1.0f || geoBone.getScaleZ() != 1.0f) {
            this.matrices.scale(geoBone.getScaleX(), geoBone.getScaleY(), geoBone.getScaleZ());
        }

        this.matrices.scale(0.0625F, 0.0625F, 0.0625F);
    }

    @Override
    protected void doTranslate(float x, float y, float z) {
        this.matrices.translate(x, y, z);
    }

    protected void doRotate(float ang, float x, float y, float z) {
        this.matrices.mulPose(new Quaternionf(new AxisAngle4f(ang * 0.017453292F, x, y, z)));
    }

    protected void doScale(float x, float y, float z) {
        this.matrices.scale(x, y, z);
    }

    @Override
    protected void addVertex(float x, float y, int z, float r, float g, float b, float a, float u, float v, float nX, float nY, float nZ) {
        Matrix4f mm = this.matrices.last().pose();
        this.vc.addVertex(mm, x, y, (float)z).setColor(r, g, b, a).setUv(u, v).setOverlay(this.overlay).setLight(this.emissive ? LightTexture.pack(15, 15) : this.light);
        if (this.emissive) {
            this.vc.setNormal(nX, nY, nZ);
        } else {
            this.vc.setNormal(((PoseStack)this.matrices).last(), nX, nY, nZ);
        }
    }

    @Override
    protected void doRenderDebugDot(float v, float v1, float v2, float v3) {

    }

    @Override
    public float getTime() {
        return (float)((LivingEntity)this.peer).tickCount + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
    }

    @Override
    public float getLimbSwing() {
        return ((LivingEntity)this.peer).walkAnimation.speed(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
    }

    @Override
    public float getHorizontalSpeed() {
        return EarsCommon.lerpDelta(((LivingEntity)this.peer).walkDistO, ((LivingEntity)this.peer).walkDist, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
    }

    @Override
    public float getStride() {
        return 0;
    }

    @Override
    public float getBodyYaw() {
        return EarsCommon.lerpDelta(((LivingEntity)this.peer).yBodyRotO, ((LivingEntity)this.peer).yBodyRot, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
    }

    @Override
    public double getX() {
        return EarsCommon.lerpDelta(((LivingEntity)this.peer).xOld, ((LivingEntity)this.peer).getX(), Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
    }

    @Override
    public double getY() {
        return EarsCommon.lerpDelta(((LivingEntity)this.peer).yOld, ((LivingEntity)this.peer).getY(), Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
    }

    @Override
    public double getZ() {
        return EarsCommon.lerpDelta(((LivingEntity)this.peer).zOld, ((LivingEntity)this.peer).getZ(), Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
    }

    @Override
    public double getCapeX() {
        return 0;
    }

    @Override
    public double getCapeY() {
        return 0;
    }

    @Override
    public double getCapeZ() {
        return 0;
    }

    @Override
    public boolean isSlim() {
        return true;
    }

    @Override
    public boolean isFlying() {
        return false;
    }

    @Override
    public boolean isGliding() {
        return false;
    }

    @Override
    public boolean isWearingElytra() {
        return false;
    }

    @Override
    public boolean isWearingChestplate() {
        return false;
    }

    @Override
    public boolean isWearingBoots() {
        return false;
    }

    @Override
    public boolean isJacketEnabled() {
        return false;
    }
}
