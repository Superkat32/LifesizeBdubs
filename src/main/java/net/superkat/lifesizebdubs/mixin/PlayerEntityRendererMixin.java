package net.superkat.lifesizebdubs.mixin;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.superkat.lifesizebdubs.entity.client.ShoulderBdubsRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRendererMixin{

	@Inject(method = "<init>", at = @At("TAIL"))
	private void lifesizebdubs$injectLifesizeBdubsShouldRenderLayer(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
		this.addFeature(new ShoulderBdubsRenderer<>((PlayerEntityRenderer) (Object) this));
	}

}