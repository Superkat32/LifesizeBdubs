package net.superkat.lifesizebdubs.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.superkat.lifesizebdubs.LifeSizeBdubs;
import net.superkat.lifesizebdubs.data.BdubsVariant;
import net.superkat.lifesizebdubs.entity.BdubsEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BdubsEntityRenderer extends GeoEntityRenderer<BdubsEntity> {
    public BdubsEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BdubsEntityModel());
        this.withScale(0.25f);
    }

    @Override
    public ResourceLocation getTextureLocation(BdubsEntity animatable) {
        BdubsVariant variant = animatable.getVariant().value();
        return variant.texture();
    }

    public static class BdubsEntityModel extends DefaultedEntityGeoModel<BdubsEntity> {
        public BdubsEntityModel() {
            super(ResourceLocation.fromNamespaceAndPath(LifeSizeBdubs.MODID, "lifesizebdubs"), true);
            withAltTexture(ResourceLocation.withDefaultNamespace("textures/item/spyglass.png"));
        }
    }
}
