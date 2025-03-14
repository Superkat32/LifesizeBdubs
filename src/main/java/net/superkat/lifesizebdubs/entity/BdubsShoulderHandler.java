package net.superkat.lifesizebdubs.entity;

import com.google.common.collect.Maps;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.superkat.lifesizebdubs.LifeSizeBdubs;
import net.superkat.lifesizebdubs.data.BdubsVariant;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

//very strange trickery indeed
public class BdubsShoulderHandler {

    //Stored server and client side separately, so technically not synced together - used on both
    public static Map<PlayerEntity, BdubsEntity> impostersLeft = Maps.newHashMap();
    public static Map<PlayerEntity, BdubsEntity> impostersRight = Maps.newHashMap();
    //Stored server and client side separately, so technically not synced together - used on client
    public static Map<PlayerEntity, Integer> resetTimers = Maps.newHashMap();

    //Called both server and client
    public static void tickImposterBdubs(PlayerEntity player) {
        tickBdubs(player, true);
        tickBdubs(player, false);
    }

    public static void tickBdubs(PlayerEntity player, boolean left) {
        NbtCompound nbt = left ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
        EntityType.get(nbt.getString("id"))
                .filter(entityType -> entityType == LifeSizeBdubs.BDUBS_ENTITY)
                .ifPresentOrElse(entityType -> {
                    BdubsEntity bdubs = getImposter(player, left);
                    if(bdubs == null) { //shoulder nbt is present as bdubs, but the entity isn't in the maps yet
                        addImposter(player, left); //adds the bdubs entity to the imposters map
                    }

                    if(!player.getWorld().isClient()) return;

                    if(bdubs != null) {
                        bdubs.tickMessages();
                    }

                    int resetTimer = resetTimers.getOrDefault(player, 1200) - 1;
                    if(resetTimer <= 0) {
                        resetTimer = 1200;
                        //refreshes the imposter bdubs on the client to hopefully reduce them turning invisible randomly
                        addImposter(player, true);
                        addImposter(player, false);
                    }
                    resetTimers.put(player, resetTimer);

                }, () -> {
                    ejectImposter(player, left); //removes the bdubs entity from the imposters map
                });
    }

    //Adds an imposter bdubs from the player's shoulder entity nbt (which should be present at this point as a bdubs entity)
    public static void addImposter(PlayerEntity player, boolean left) {
        NbtCompound nbt = left ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
        if(nbt.isEmpty()) return;

        BdubsEntity bdubs = fromNbt(player, nbt);
        if (left) {
            impostersLeft.put(player, bdubs);
        } else {
            impostersRight.put(player, bdubs);
        }
    }

    public static BdubsEntity fromNbt(PlayerEntity player, NbtCompound nbt) {
        AtomicReference<BdubsEntity> returnEntity = new AtomicReference<>();
        EntityType.get(nbt.getString("id"))
                .filter(entityType -> entityType == LifeSizeBdubs.BDUBS_ENTITY)
                .ifPresent(entityType -> {
                    BdubsVariant variant = BdubsVariant.fromNbt(player.getRegistryManager(), nbt);
                    BdubsEntity bdubs = (BdubsEntity) EntityType.getEntityFromNbt(nbt, player.getWorld()).get();
                    bdubs.activateShoulderMode(player);
                    bdubs.setVariant(variant);
                    returnEntity.set(bdubs);
                });
        return returnEntity.get();
    }

    public static void ejectImposter(PlayerEntity player, boolean left) {
        if(left) impostersLeft.remove(player);
        else impostersRight.remove(player);
    }

    @Nullable
    public static BdubsEntity getImposter(PlayerEntity player, boolean left) {
        return left ? impostersLeft.get(player) : impostersRight.get(player);
    }

}
