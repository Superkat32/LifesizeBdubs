package net.superkat.lifesizebdubs.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.superkat.lifesizebdubs.compat.BdubsTexturePatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleTexture.class)
public class SimpleTextureMixin {

    @Shadow @Final protected ResourceLocation location;

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;isOnRenderThreadOrInit()Z"))
    public void lifesizebdubs$textureTransparencyFix(ResourceManager resourceManager, CallbackInfo ci, @Local NativeImage nativeImage) {
        if(location.getPath().startsWith("textures/bdubs")) {
//            EarsCompat.fixTextureAlpha(nativeImage);
            BdubsTexturePatcher.patchBdubsTexture(nativeImage);
        }
    }

}
