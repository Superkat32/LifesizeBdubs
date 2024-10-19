package net.superkat.lifesizebdubs.mixin;

import net.minecraft.world.entity.player.Player;
import net.superkat.lifesizebdubs.entity.BdubsShoulderHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void lifesizebdubs$tickShoulderBdubs(CallbackInfo ci) {
        Player self = (Player)(Object)this;
        BdubsShoulderHandler.tickImposterBdubs(self);

        //2 + -x = 1 //x = 5
        //2 - x = 1
        //2 + -(5) = 1
        //2 + (-1) * (5) = 1
    }
}
