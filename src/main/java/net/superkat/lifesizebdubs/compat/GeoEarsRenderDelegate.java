package net.superkat.lifesizebdubs.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.render.AbstractEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import com.unascribed.ears.common.util.Decider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.superkat.lifesizebdubs.entity.BdubsEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;


//public abstract class GeoEarsRenderDelegate implements EarsRenderDelegate {
public abstract class GeoEarsRenderDelegate<TMatrixStack, TVertexConsumerProvider, TVertexConsumer, TPeer, TModelPart> extends AbstractEarsRenderDelegate<TPeer, TModelPart> {
//    protected TMatrixStack matrices;
//    protected TVertexConsumerProvider vertexConsumerProvider;
//    protected TVertexConsumer vertexConsumer;
//    protected int light;
//    protected int overlay;
//
//    public void render(TMatrixStack matrices, TVertexConsumerProvider vertexConsumers, TPeer peer, int light, int overlay) {
//        this.render(matrices, vertexConsumers, peer, light, overlay, null);
//    }
//
//    public void render(TMatrixStack matrices, TVertexConsumerProvider vertexConsumers, TPeer peer, int light, int overlay, EarsRenderDelegate.BodyPart permittedBodyPart) {
//        this.matrices = matrices;
//        this.vertexConsumerProvider = vertexConsumers;
//        this.peer = peer;
//        this.permittedBodyPart = permittedBodyPart;
//        this.feat = this.getEarsFeatures();
//        this.vertexConsumer = this.getVertexConsumer(TexSource.SKIN):
//    }
//
//    public static GeoEarsRenderDelegate<PoseStack, MultiBufferSource, VertexConsumer, GeoEntity, GeoBone> createDelegate() {
//        return new GeoEarsRenderDelegate<>() {
//            @Override
//            protected EarsFeatures getEarsFeatures() {
//                return null;
//            }
//
//            @Override
//            protected void setUpRenderState() {
//
//            }
//
//            @Override
//            protected void tearDownRenderState() {
//
//            }
//
//            @Override
//            protected void pushMatrix() {
//
//            }
//
//            @Override
//            protected void popMatrix() {
//
//            }
//
//            @Override
//            protected boolean isVisible(GeoBone geoBone) {
//                return false;
//            }
//
//            @Override
//            protected Decider<BodyPart, GeoBone> decideModelPart(Decider<BodyPart, GeoBone> decider) {
//                return null;
//            }
//
//            @Override
//            protected void doAnchorTo(BodyPart bodyPart, GeoBone geoBone) {
//
//            }
//
//            @Override
//            protected void doTranslate(float v, float v1, float v2) {
//
//            }
//
//            @Override
//            protected void doRotate(float v, float v1, float v2, float v3) {
//
//            }
//
//            @Override
//            protected void doScale(float v, float v1, float v2) {
//
//            }
//
//            @Override
//            protected void beginQuad() {
//
//            }
//
//            @Override
//            protected void addVertex(float v, float v1, int i, float v2, float v3, float v4, float v5, float v6, float v7, float v8, float v9, float v10) {
//
//            }
//
//            @Override
//            protected void drawQuad() {
//
//            }
//
//            @Override
//            protected void doRenderDebugDot(float v, float v1, float v2, float v3) {
//
//            }
//
//            @Override
//            protected void doBindSkin() {
//
//            }
//
//            @Override
//            protected void doBindAux(TexSource texSource, byte[] bytes) {
//
//            }
//
//            @Override
//            public float getTime() {
//                return 0;
//            }
//
//            @Override
//            public float getLimbSwing() {
//                return 0;
//            }
//
//            @Override
//            public float getHorizontalSpeed() {
//                return 0;
//            }
//
//            @Override
//            public float getStride() {
//                return 0;
//            }
//
//            @Override
//            public float getBodyYaw() {
//                return 0;
//            }
//
//            @Override
//            public double getX() {
//                return 0;
//            }
//
//            @Override
//            public double getY() {
//                return 0;
//            }
//
//            @Override
//            public double getZ() {
//                return 0;
//            }
//
//            @Override
//            public double getCapeX() {
//                return 0;
//            }
//
//            @Override
//            public double getCapeY() {
//                return 0;
//            }
//
//            @Override
//            public double getCapeZ() {
//                return 0;
//            }
//
//            @Override
//            public boolean isSlim() {
//                return false;
//            }
//
//            @Override
//            public boolean isFlying() {
//                return false;
//            }
//
//            @Override
//            public boolean isGliding() {
//                return false;
//            }
//
//            @Override
//            public boolean isWearingElytra() {
//                return false;
//            }
//
//            @Override
//            public boolean isWearingChestplate() {
//                return false;
//            }
//
//            @Override
//            public boolean isWearingBoots() {
//                return false;
//            }
//
//            @Override
//            public boolean isJacketEnabled() {
//                return false;
//            }
//        };
//
//    }
//    protected GeoEntity peer;
//    protected GeoRenderer<?> renderer;
//    protected GeoModel<?> model;
//    protected EarsFeatures feat;
//    protected PoseStack matrices;
//
//    public GeoEarsRenderDelegate(GeoEntity peer, GeoRenderer<?> renderer, GeoModel<?> model) {
//        this.peer = peer;
//        this.renderer = renderer;
//        this.model = model;
//    }
//
//    @Override
//    public GeoEntity getPeer() {
//        return peer;
//    }
//
//    public EarsFeatures getEarsFeatures() {
//        return feat;
//    }
//
//
//
//    @Override
//    public void setUp() {
//
//    }
//
//    @Override
//    public void push() {
//        this.matrices.pushPose();
//    }
//
//    @Override
//    public void anchorTo(BodyPart bodyPart) {
//
//    }
//
//    @Override
//    public boolean isSlim() {
//        return true;
//    }
}
