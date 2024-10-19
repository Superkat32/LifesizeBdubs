package net.superkat.lifesizebdubs.entity;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.superkat.lifesizebdubs.LifeSizeBdubs;
import net.superkat.lifesizebdubs.data.BdubsVariant;
import net.superkat.lifesizebdubs.entity.client.BdubsEntityRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class BdubsEntity extends ShoulderRidingEntity implements VariantHolder<BdubsVariant>, GeoEntity {
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(BdubsEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SUGAR_TICKS_ID = SynchedEntityData.defineId(BdubsEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected final String controller = "default";
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.bdubs.idle");
    protected static final RawAnimation SUGAR_IDLE_ANIM = RawAnimation.begin().thenLoop("animation.bdubs.sugaridle");
    protected static final RawAnimation WAVE_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.wave");
    protected static final RawAnimation CHEER_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.cheer");
    protected static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.perish");
    protected static final RawAnimation DESPAWN_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.leave");
    protected static final RawAnimation GIVEN_SUGAR_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.givensugarohno");
    protected static final RawAnimation LAUGH_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.laugh");
    protected static final RawAnimation NOD_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.nod");
    protected static final RawAnimation BOW_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.yourewelcome");
    protected static final RawAnimation TADA_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.tada");

    public boolean onShoulder = false;
    public LivingEntity shoulderRidingPlayer = null;
    public int messageTicks = 100;
    public int sleepMessageTicks;

    public int waveTicks = 10;
    public int ticksSinceWave = 0;
    public int ticksSinceSpyglassWave = 0;
    public Player spyglassWavedPlayer = null;


    public BdubsEntity(EntityType<? extends ShoulderRidingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8f));
    }

    @Override
    protected boolean shouldStayCloseToLeashHolder() {
        //whatever happens in Leashable#closeRangeLeashBehaviour is messing with
        //my Bdubs shoulder renderer, causing it to spin
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT_ID, BdubsVariant.getIntFromVariant(BdubsVariant.DEFAULT, this.registryAccess()));
        builder.define(SUGAR_TICKS_ID, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(DATA_VARIANT_ID.equals(key)) {
            int variantId = this.entityData.get(DATA_VARIANT_ID);
            BdubsVariant variant = BdubsVariant.getVariantFromInt(variantId, this.registryAccess());
            this.setVariant(variant);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        Optional<Holder.Reference<BdubsVariant>> holder = registryAccess().registryOrThrow(LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY).getHolder(BdubsVariant.getIntFromVariant(this.getVariant(), this.registryAccess()));
        holder.flatMap(Holder.Reference::unwrapKey).ifPresent(bdubsVariantResourceKey -> compound.putString("variant", bdubsVariantResourceKey.location().toString()));

        compound.putInt("sugarticks", getSugarTicks());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        BdubsVariant bdubsVariant = BdubsVariant.getVariantFromCompoundTag(compound, this.registryAccess());
        if(bdubsVariant != null) {
            this.setVariant(bdubsVariant);
        }

        this.setSugarTicks(compound.getInt("sugarticks"));
    }

    @Override
    public void setVariant(@NotNull BdubsVariant variant) {
        int id = BdubsVariant.getIntFromVariant(variant, this.registryAccess());
        this.entityData.set(DATA_VARIANT_ID, id);
    }

    @Override @NotNull
    public BdubsVariant getVariant() {
        return BdubsVariant.getVariantFromInt(this.entityData.get(DATA_VARIANT_ID), this.registryAccess());
    }

    public void setSugarTicks(int ticks) {
        this.entityData.set(SUGAR_TICKS_ID, ticks);
    }

    public int getSugarTicks() {
        return this.entityData.get(SUGAR_TICKS_ID);
    }

    public void setOnShoulder(boolean onShoulder, LivingEntity player) {
        this.onShoulder = onShoulder;
        this.shoulderRidingPlayer = player;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, controller, 5, event -> {

            if(this.dead) {
                return event.setAndContinue(DEATH_ANIM);
            }

            if(this.getSugarTicks() > 0) {
                return event.setAndContinue(SUGAR_IDLE_ANIM);
            }
            return event.setAndContinue(IDLE_ANIM);
        })
            .triggerableAnim(WAVE_ANIM.toString().toLowerCase(Locale.US), WAVE_ANIM)
            .triggerableAnim(CHEER_ANIM.toString().toLowerCase(Locale.US), CHEER_ANIM)
            .triggerableAnim(GIVEN_SUGAR_ANIM.toString().toLowerCase(Locale.US), GIVEN_SUGAR_ANIM)
        );
    }

    public void wave(boolean canCheer) {
        this.wave(canCheer, false);
    }

    public void wave(boolean canCheer, boolean overrideTime) {
        if(ticksSinceWave >= 70 || overrideTime) {
            if(canCheer && this.getOwner() != null && this.getOwner().getMainHandItem().is(Items.SPYGLASS)) {
                this.cheer();
            } else {
                this.triggerAnim(controller, WAVE_ANIM.toString().toLowerCase(Locale.US));
            }
            ticksSinceWave = 0;
        }
        waveTicks = this.random.nextInt(100, 1000);
    }

    public void cheer() {
        this.triggerAnim(controller, CHEER_ANIM.toString().toLowerCase(Locale.US));
    }

    public void handleMessages() {
        if(this.getOwner() != null && this.onShoulder) {

        }
    }

    public void sendMessageToOwner(Component text) {
        Minecraft.getInstance().player.displayClientMessage(text, false);
    }

    public void tickAnimation() {
        waveTicks--;
        ticksSinceWave++;
        if(waveTicks <= 0) {
            wave(true);
        }
    }

    @Override
    public void tick() {
        super.tick();

        tickAnimation();
        handleMessages();
        int sugarTicks = getSugarTicks();
        if(sugarTicks > 0) {
            setSugarTicks(sugarTicks--);
        }

        if(!this.level().isClientSide) {
            if(ticksSinceWave >= 20) {
                List<Player> spyglassUsingPlayers = this.level().getNearbyPlayers(
                        TargetingConditions.forNonCombat().selector(player -> player.getMainHandItem().is(Items.SPYGLASS)),
                        this, this.getBoundingBox().inflate(50, 25, 50)
                );

                for (Player player : spyglassUsingPlayers) {
                    if(player instanceof ServerPlayer serverPlayer) {
                        if(serverPlayer.getUseItem().is(Items.SPYGLASS)) {
                            Vec3 eyePos = serverPlayer.getEyePosition();
                            Vec3 viewVector = serverPlayer.getViewVector(1f);
                            Vec3 added = eyePos.add(viewVector.multiply(100f, 100f, 100f));
                            EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(serverPlayer.level(), serverPlayer, eyePos, added,
                                    (new AABB(eyePos, added)).inflate(1), (entity) -> {
                                return !entity.isSpectator();
                                    }, 0f);
                            if(entityHitResult != null && entityHitResult.getEntity() == this && serverPlayer.hasLineOfSight(this)) {
                                if(serverPlayer != this.spyglassWavedPlayer || (ticksSinceSpyglassWave >= 300 || serverPlayer.getTicksUsingItem() <= 40)) {
                                    this.spyglassWavedPlayer = serverPlayer;
                                    wave(false);
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        boolean hasOwner = this.getOwner() != null;
        boolean isOwner = hasOwner && player == this.getOwner();
        boolean hadItem = this.getItemBySlot(EquipmentSlot.MAINHAND) != ItemStack.EMPTY;
        BdubsVariant newVariant = BdubsVariant.getVariantFromItem(itemStack, this.registryAccess());

        if(itemStack.is(Items.SUGAR)) {
            //TODO - handle variant item possibly being sugar
            itemStack.consume(1, player);
            this.setSugarTicks(1200); //1 minute
            this.triggerAnim(controller, GIVEN_SUGAR_ANIM.toString().toLowerCase(Locale.US));
            this.spawnEvenFunnierParticles();
            this.level().playSound(this.level().isClientSide ? player : null, this.getX(), this.getY(), this.getZ(), SoundEvents.ALLAY_ITEM_GIVEN, SoundSource.AMBIENT, 2, 1);
            this.level().playSound(this.level().isClientSide ? player : null, this.getX(), this.getY(), this.getZ(), SoundEvents.BREEZE_CHARGE, SoundSource.AMBIENT, 1f, 1.25f);
            this.level().playSound(this.level().isClientSide ? player : null, this.getX(), this.getY(), this.getZ(), SoundEvents.BREEZE_DEFLECT, SoundSource.AMBIENT, 1f, 1.25f);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if(newVariant != null && newVariant != this.getVariant()) {
            itemStack.consume(1, player);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemStack.split(1));
            this.setOwnerUUID(player.getUUID());
            this.level().playSound(this.level().isClientSide ? player : null, this.getX(), this.getY(), this.getZ(), SoundEvents.ALLAY_AMBIENT_WITH_ITEM, SoundSource.AMBIENT);
            if(hadItem || newVariant != this.getVariant()) {
                spawnFunnyParticles();
                this.wave(true, true);
            }
            this.setVariant(newVariant);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if(isOwner && hadItem) {
            if(player instanceof ServerPlayer serverPlayer) {
                this.setEntityOnShoulder(serverPlayer);
            }
        }
        return super.mobInteract(player, hand);
    }

    public void spawnFunnyParticles() {
        for (int i = 0; i < 7; i++) {
            this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getEyeY(), this.getZ(), this.random.nextGaussian() / 4, this.random.nextGaussian() / 8, this.random.nextGaussian() / 4);
            this.level().addParticle(ParticleTypes.WAX_OFF, this.getX(), this.getEyeY(), this.getZ(), this.random.nextGaussian() * 8, this.random.nextGaussian(), this.random.nextGaussian() * 8);
            this.level().addParticle(ParticleTypes.WHITE_SMOKE, this.getX(), this.getEyeY(), this.getZ(), this.random.nextGaussian() / 2, this.random.nextGaussian() / 8, this.random.nextGaussian() / 2);
        }
    }

    public void spawnEvenFunnierParticles() {
        for (int i = 0; i < 7; i++) {
            this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getEyeY(), this.getZ(), this.random.nextGaussian() / 4, this.random.nextGaussian() / 8, this.random.nextGaussian() / 4);
            this.level().addParticle(ParticleTypes.WAX_OFF, this.getX(), this.getEyeY(), this.getZ(), this.random.nextGaussian() * 8, this.random.nextGaussian() * 4, this.random.nextGaussian() * 8);
            this.level().addParticle(ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS, this.getX(), this.getEyeY(), this.getZ(), this.random.nextGaussian() / 24, 0.01, this.random.nextGaussian() / 24);
        }
        this.level().addParticle(ParticleTypes.SONIC_BOOM, this.getX(), this.getEyeY(), this.getZ(), 0, 0, 0);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.AXOLOTL_IDLE_AIR;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.AXOLOTL_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.AXOLOTL_DEATH;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }
}
