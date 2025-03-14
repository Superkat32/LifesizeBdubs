package net.superkat.lifesizebdubs.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.superkat.lifesizebdubs.duck.LifeSizeBdubsPlayer;
import net.superkat.lifesizebdubs.entity.BdubsShoulderHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements LifeSizeBdubsPlayer {

    @Unique
    public int lastLockTicks = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void lifesizebdubs$tickShoulderBdubs(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        BdubsShoulderHandler.tickImposterBdubs(self);

        //2 + -x = 1 //x = 5
        //2 - x = 1
        //2 + -(5) = 1
        //2 + (-1) * (5) = 1
    }



    @Override
    public int lifesizebdubs$lastLockTicks() {
        return this.lastLockTicks;
    }

    @Override
    public void lifesizebdubs$setLastLockTicks(int ticks) {
        this.lastLockTicks = ticks;
    }
}
