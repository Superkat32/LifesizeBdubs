package net.superkat.lifesizebdubs.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.entity.player.PlayerEntity;
import net.superkat.lifesizebdubs.LifeSizeBdubs;
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

        lastLockTicks++;

        //2 + -x = 1 //x = 5
        //2 - x = 1
        //2 + -(5) = 1
        //2 + (-1) * (5) = 1
    }

    @WrapWithCondition(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropShoulderEntities()V"))
    private boolean lifesizebdubs$shouldPreventRemovingShoulderEntities(PlayerEntity player) {
        return !lifesizebdubs$shoulderEntitiesLocked();
    }

    @WrapWithCondition(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropShoulderEntities()V"))
    private boolean lifesizebdubs$preventRemovingShoulderEntitiesAfterGettingAbsolutelyDestroyedAndOrQuitePossiblySmackedInTheFaceByAMaceOrByMumbo(PlayerEntity player) {
        return !lifesizebdubs$shoulderEntitiesLocked();
    }

    private boolean lifesizebdubs$shoulderEntitiesLocked() {
        return ((PlayerEntity)(Object)this).getAttachedOrCreate(LifeSizeBdubs.LOCKED_SHOULDER_ENTITY_ATTACHMENT, () -> false).booleanValue();
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
