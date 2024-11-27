package net.superkat.lifesizebdubs.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.player.Player;
import net.superkat.lifesizebdubs.LifeSizeBdubs;
import net.superkat.lifesizebdubs.duck.LifeSizeBdubsPlayer;
import net.superkat.lifesizebdubs.entity.BdubsShoulderHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin implements LifeSizeBdubsPlayer {
//    @Unique
//    public boolean lockedShoulderEntities = false;
    @Unique
    public int lastLockTicks = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void lifesizebdubs$tickShoulderBdubs(CallbackInfo ci) {
        Player self = (Player)(Object)this;
        BdubsShoulderHandler.tickImposterBdubs(self);

        //2 + -x = 1 //x = 5
        //2 - x = 1
        //2 + -(5) = 1
        //2 + (-1) * (5) = 1
    }

    @WrapWithCondition(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;removeEntitiesOnShoulder()V"))
    private boolean lifesizebdubs$shouldPreventRemovingShoulderEntities(Player instance) {
        return !lifesizebdubs$lockedShoulderEntities();
    }

    //it's midnight my names aren't the greatest - actually that's a lie this is fantastic
    @WrapWithCondition(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;removeEntitiesOnShoulder()V"))
    private boolean lifesizebdubs$preventRemovingShoulderEntitiesAfterGettingAbsolutelyDestroyedAndOrQuitePossiblySmackedInTheFaceByAMaceOrByMumbo(Player instance) {
        return !lifesizebdubs$lockedShoulderEntities();
    }

    @Unique
    private boolean lifesizebdubs$lockedShoulderEntities() {
        return ((Player)(Object)this).getData(LifeSizeBdubs.LOCKED_SHOULDER_ENTITIES);
    }

//    @Override
//    public boolean lifesizebdubs$lockedShoulderEntity() {
//        return lockedShoulderEntities;
//    }
//
//    @Override
//    public void lifesizebdubs$setLockedShoulderEntity(boolean locked) {
//        this.lockedShoulderEntities = locked;
//    }

    @Override
    public int lifesizebdubs$lastLockTicks() {
        return lastLockTicks;
    }

    @Override
    public void lifesizebdubs$setLastLockTicks(int ticks) {
        this.lastLockTicks = ticks;
    }
}
