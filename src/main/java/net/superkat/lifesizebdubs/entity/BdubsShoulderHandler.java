package net.superkat.lifesizebdubs.entity;

import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.superkat.lifesizebdubs.LifeSizeBdubs;
import net.superkat.lifesizebdubs.data.BdubsVariant;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.Map;

public class BdubsShoulderHandler {
    public static Map<Player, Pair<BdubsEntity, BdubsEntity>> imposters = Maps.newHashMap();

    @Nullable
    public static BdubsEntity getImposterBdubs(Player player, boolean left) {
        BdubsEntity imposter = null;
        Pair<BdubsEntity, BdubsEntity> pair = imposters.computeIfPresent(player, (player1, imposterPair) -> imposterPair);
        if(pair != null) {
            imposter = left ? pair.getA() : pair.getB();
        }
        return imposter;
    }

    public static Pair<BdubsEntity, BdubsEntity> imposterPair(Player player) {
        return new Pair<>(getImposterBdubs(player, true), getImposterBdubs(player, false));
    }

    public static void setImposterBdubs(Player player, BdubsEntity bdubs, boolean left) {
        Pair<BdubsEntity, BdubsEntity> pair = new Pair<>(left ? bdubs : getImposterBdubs(player, true), left ? getImposterBdubs(player, false) : bdubs);
        imposters.put(player, pair);
    }

    @Nullable
    public static BdubsEntity getAndSetImposterBdubs(CompoundTag compoundTag, Player player, boolean left) {
        BdubsEntity imposterBdubs = getImposterBdubs(player, left);
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
                        //likely serverside
                        imposterBdubs.tickMessages();
                    }

                    setImposterBdubs(player, imposterBdubs, left);
                    //cache entity I guess maybe perhaps perchance
                    //cursed if else statements but it works?
//                    if(left) imposterBdubsLeft = imposterBdubs;
//                    else imposterBdubsRight = imposterBdubs;
                }, () -> {
                    ejectImposterBdubs(player, left);
                });
    }

    //sus (ʘ ͜ʖ ʘ)
    public static void ejectImposterBdubs(Player player, boolean left) {
        imposters.put(player, new Pair<>(left ? null : getImposterBdubs(player, true), left ? getImposterBdubs(player, false) : null));
//        if(left) imposterBdubsLeft = null;
//        else imposterBdubsRight = null;
    }
}
