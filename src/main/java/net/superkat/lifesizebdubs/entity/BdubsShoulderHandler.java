package net.superkat.lifesizebdubs.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.superkat.lifesizebdubs.LifeSizeBdubs;
import net.superkat.lifesizebdubs.data.BdubsVariant;
import org.jetbrains.annotations.Nullable;

public class BdubsShoulderHandler {
    public static BdubsEntity imposterBdubsLeft = null;
    public static BdubsEntity imposterBdubsRight = null;

    @Nullable
    public static BdubsEntity getImposterBdubs(boolean left) {
        return left ? imposterBdubsLeft : imposterBdubsRight;
    }

    @Nullable
    public static BdubsEntity getAndSetImposterBdubs(CompoundTag compoundTag, Player player, boolean left) {
        BdubsEntity imposterBdubs = left ? imposterBdubsLeft : imposterBdubsRight;
        if(imposterBdubs == null) {
            BdubsVariant bdubsVariant = BdubsVariant.getVariantFromCompoundTag(compoundTag, player.registryAccess());
            imposterBdubs = (BdubsEntity) EntityType.create(compoundTag, player.level()).get();
            imposterBdubs.setVariant(bdubsVariant != null ? bdubsVariant : BdubsVariant.DEFAULT);
            imposterBdubs.setOnShoulder(true, player);
        }
        return imposterBdubs;
    }

    public static void tickImposterBdubs(Player player) {
        tickImposterBdubs(player, true);
        tickImposterBdubs(player, false);
    }

    public static void tickImposterBdubs(Player player, boolean left) {
        CompoundTag compoundTag = left ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
        EntityType.byString(compoundTag.getString("id"))
                .filter(entityType -> entityType == LifeSizeBdubs.BDUBS_ENTITY.get())
                .ifPresentOrElse(entityType -> {
                    BdubsEntity imposterBdubs = getAndSetImposterBdubs(compoundTag, player, left);

                    if(imposterBdubs != null) {
                        imposterBdubs.tickAnimation();
                    }

                    //cache entity I guess maybe perhaps perchance
                    //cursed if else statements but it works?
                    if(left) imposterBdubsLeft = imposterBdubs;
                    else imposterBdubsRight = imposterBdubs;
                }, () -> {
                    ejectImposterBdubs(left);
                });
    }

    //sus (ʘ ͜ʖ ʘ)
    public static void ejectImposterBdubs(boolean left) {
        if(left) imposterBdubsLeft = null;
        else imposterBdubsRight = null;
    }
}
