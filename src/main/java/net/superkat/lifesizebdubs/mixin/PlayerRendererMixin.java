package net.superkat.lifesizebdubs.mixin;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.superkat.lifesizebdubs.entity.client.BdubsOnShoulderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRendererMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void lifesizebdubs$injectLifesizeBdubsRenderLayer(EntityRendererProvider.Context context, boolean useSlimModel, CallbackInfo ci) {
        //doesn't seem like NeoForge has a register render layer event sadly
        this.addLayer(new BdubsOnShoulderLayer<>((PlayerRenderer)(Object) this));
    }

}
