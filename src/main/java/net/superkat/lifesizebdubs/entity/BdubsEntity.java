package net.superkat.lifesizebdubs.entity;

import com.mojang.datafixers.util.Pair;
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
import net.neoforged.neoforge.network.PacketDistributor;
import net.superkat.lifesizebdubs.LifeSizeBdubs;
import net.superkat.lifesizebdubs.data.BdubsVariant;
import net.superkat.lifesizebdubs.network.BdubsMessagePacket;
import net.superkat.lifesizebdubs.network.BdubsVariantChangeEffectsPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class BdubsEntity extends ShoulderRidingEntity implements VariantHolder<BdubsVariant>, GeoEntity {
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(BdubsEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SUGAR_TICKS_ID = SynchedEntityData.defineId(BdubsEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOWCASE_MODE_ID = SynchedEntityData.defineId(BdubsEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected final String controller = "default";
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.bdubs.idle");
    protected static final RawAnimation SUGAR_IDLE_ANIM = RawAnimation.begin().thenLoop("animation.bdubs.sugaridle");
    protected static final RawAnimation WAVE_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.wave");
    protected static final RawAnimation CHEER_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.cheer");
    protected static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.perish");
    protected static final RawAnimation DESPAWN_ANIM = RawAnimation.begin().thenPlayAndHold("animation.bdubs.leave");
    protected static final RawAnimation GIVEN_SUGAR_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.givensugarohno");
    protected static final RawAnimation LAUGH_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.laugh");
    protected static final RawAnimation NOD_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.nod");
    protected static final RawAnimation BOW_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.yourewelcome");
    protected static final RawAnimation TADA_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.tada");
    protected static final RawAnimation SMOOTH_DANCE_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.smooth");
    public static List<RawAnimation> IDLE_ANIMS = List.of(WAVE_ANIM, CHEER_ANIM, GIVEN_SUGAR_ANIM, LAUGH_ANIM, NOD_ANIM, BOW_ANIM, TADA_ANIM, SMOOTH_DANCE_ANIM);

    public int ownerInteractionTicks = 0;

    public boolean onShoulder = false;
    public LivingEntity shoulderRidingPlayer = null;
    public int messageTicks = 1200;
    public List<String> lastMessages = new ArrayList<>();
    public String lastTimedMessage = "";
    public int lastMessageTicks = 0;

    public int idleAnimTicks = 300;
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT_ID, BdubsVariant.getIntFromVariant(BdubsVariant.DEFAULT, this.registryAccess()));
        builder.define(SUGAR_TICKS_ID, 0);
        builder.define(SHOWCASE_MODE_ID, false);
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
        compound.putBoolean("showcaseMode", isShowcaseMode());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        BdubsVariant bdubsVariant = BdubsVariant.getVariantFromCompoundTag(compound, this.registryAccess());
        if(bdubsVariant != null) {
            this.setVariant(bdubsVariant);
        }

        this.setSugarTicks(compound.getInt("sugarticks"));
        this.setShowcaseMode(compound.getBoolean("showcaseMode"));
    }

    @Override
    public void setVariant(@NotNull BdubsVariant variant) {
        int id = BdubsVariant.getIntFromVariant(variant, this.registryAccess());
        this.entityData.set(DATA_VARIANT_ID, id);

        if(this.getOwner() != null) {
//            Component name = Component.translatable("entity.lifesizebdubs.name", this.getOwner().getDisplayName(), this.getVariant().name());
//            this.setCustomName(name);
            ownerInteractionTicks = 0;
        }
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

    public void setShowcaseMode(boolean showcaseMode) {
        this.entityData.set(SHOWCASE_MODE_ID, showcaseMode);
        this.setInvulnerable(showcaseMode);
        this.setNoAi(showcaseMode);
    }

    public boolean isShowcaseMode() {
        return this.entityData.get(SHOWCASE_MODE_ID);
    }

    public void setOnShoulder(boolean onShoulder, LivingEntity player) {
        this.onShoulder = onShoulder;
        this.shoulderRidingPlayer = player;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        AnimationController<BdubsEntity> animController = new AnimationController<>(this, controller, 5, event -> {
            if(this.dead) {
                return event.setAndContinue(DEATH_ANIM);
            }

            if(this.getSugarTicks() > 0) {
                return event.setAndContinue(SUGAR_IDLE_ANIM);
            }
            return event.setAndContinue(IDLE_ANIM);
        });

        //cheat haha
        for (RawAnimation idleAnim : IDLE_ANIMS) {
            animController.triggerableAnim(animString(idleAnim), idleAnim);
            if(idleAnim == LAUGH_ANIM) {
                animController.setSoundKeyframeHandler(event -> {
                    Player player = ClientUtil.getClientPlayer();

                    if(player != null) {
                        player.playSound(SoundEvents.VEX_AMBIENT);
                    }
                });
            }
        }

        animController.triggerableAnim(animString(DESPAWN_ANIM), DESPAWN_ANIM);

        controllers.add(animController);
    }

    private String animString(RawAnimation anim) {
        return anim.getAnimationStages().getFirst().animationName();
    }

    public void playIdleAnim() {
        int idleIndex = this.random.nextInt(IDLE_ANIMS.size());
        RawAnimation idleAnim = IDLE_ANIMS.get(idleIndex);
        if(idleAnim == WAVE_ANIM) {
            this.wave(false);
        } else {
            //don't play animation if a triggered animation(likely idle) is already playing
            if(!this.getAnimatableInstanceCache().getManagerForId(this.getId()).getAnimationControllers().get(controller).isPlayingTriggeredAnimation()) {
                this.triggerAnim(controller, animString(idleAnim));
            }
        }
        this.idleAnimTicks = this.random.nextInt(200, 1000);
    }

    public void wave(boolean canCheer) {
        this.wave(canCheer, false);
    }

    public void wave(boolean canCheer, boolean overrideTime) {
        if(ticksSinceWave >= 70 || overrideTime) {
            if(canCheer && this.getOwner() != null && this.getOwner().getMainHandItem().is(Items.SPYGLASS)) {
                this.cheer();
            } else {
                if(overrideTime) {
                    this.getAnimatableInstanceCache().getManagerForId(this.getId()).getAnimationControllers().get(controller).forceAnimationReset();
                }
                this.triggerAnim(controller, animString(WAVE_ANIM));
            }
            ticksSinceWave = 0;
        }
    }

    public void cheer() {
        this.triggerAnim(controller, animString(CHEER_ANIM));
    }

    public void givenSugarAnim() {
        this.triggerAnim(controller, animString(GIVEN_SUGAR_ANIM));
    }

    //likely called serverside
    public void tickMessages() {
        if(this.getOwner() != null && this.onShoulder) {
            BdubsVariant variant = this.getVariant();
            List<String> messages = variant.messages();

            messageTicks--;
            lastMessageTicks++;

            if(messageTicks <= 0 && !messages.isEmpty()) {
                String sentMessage = null;
                for (int i = 0; i < 10; i++) {
                    //get random message
                    int msgIndex = this.random.nextInt(messages.size());
                    String message = messages.get(msgIndex);
                    if(message != null && !message.isEmpty()) {
                        //check if message has been sent recently
                        boolean sentRecently = lastMessages.contains(message);
                        if(!sentRecently) {
                            sentMessage = message;
                            break;
                        }
                    }
                }

                if(sentMessage != null) {
                    sendMessageToOwner(sentMessage);
                    lastMessages.addFirst(sentMessage);
                    int maxLastMessages = variant.messages().size() > 5 ? 5 : variant.messages().size() - 1;
                    if(lastMessages.size() > maxLastMessages) {
                        lastMessages.removeLast();
                    }
                }

                messageTicks = this.random.nextInt(6000, 8400); //5-7 minutes
            }

            int time = (int) this.level().getDayTime();
            //yeah okay that works I guess
            Optional<List<Pair<String, Integer>>> optionalTimedMessages = variant.timedMessages();
            if(optionalTimedMessages.isPresent()) {
                List<Pair<String, Integer>> timedMessages = optionalTimedMessages.get();
                for (Pair<String, Integer> timedMessage : timedMessages) {
                    if(timedMessage.getSecond() == time) {
                        String msg = timedMessage.getFirst();
                        if(msg != null && !msg.isEmpty()) {
                            boolean sendMessage = false;
                            boolean isPreviousTimedMessage = !lastTimedMessage.isEmpty() && msg.equals(lastTimedMessage);
                            if(isPreviousTimedMessage) {
                                sendMessage = lastMessageTicks >= 20;
                            } else {
                                sendMessage = true;
                            }

                            if(sendMessage) {
                                sendMessageToOwner(msg);
                                lastTimedMessage = msg;
                                lastMessageTicks = 0;
                            }
                        }
                    }
                }
            }

        }
    }

    public void sendMessageToOwner(String message) {
        if(this.getOwner() instanceof ServerPlayer owner) {
            Component sentMessage = Component.translatable("entity.lifesizebdubs.funnybdubsmessage", getVariant().name(), message);
            PacketDistributor.sendToPlayer(owner, new BdubsMessagePacket(sentMessage));
//            owner.displayClientMessage(sentMessage, false);
        }
    }

    public void tickAnimation() {
        idleAnimTicks--;
        waveTicks--;
        ticksSinceWave++;
        if(idleAnimTicks <= 0) {
            playIdleAnim();
        }
        if(waveTicks == 0) {
            wave(true);
        }
    }

    @Override
    public void tick() {
        super.tick();

        ownerInteractionTicks++;
        if(ownerInteractionTicks >= 6000) { //5 minutes
            triggerAnim(controller, animString(DESPAWN_ANIM));
            if(ownerInteractionTicks >= 6045) {
                this.discard();
            }
        } else {
            //don't allow idle animations to play while despawning
            tickAnimation();
            int sugarTicks = getSugarTicks();
            if(sugarTicks > 0) {
                setSugarTicks(sugarTicks - 1);
            }
        }

        //wave to spyglass :)
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
                                    wave(false, true);
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

        if(itemStack.is(Items.SUGAR) && !isShowcaseMode()) {
            boolean giveSugar;
            boolean newVariantItemIsSugar = newVariant != null && newVariant.item().is(itemStack.getItem());
            if(newVariantItemIsSugar) {
                giveSugar = newVariant.equals(this.getVariant()); //give sugar if new variant
            } else {
                giveSugar = true;
            }

            if(giveSugar) {
                itemStack.consume(1, player);
                this.setSugarTicks(1200); //1 minute
                if(this.level().isClientSide) {
                    PacketDistributor.sendToServer(new BdubsVariantChangeEffectsPacket(this.getId(), true));
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }

        if(newVariant != null && newVariant != this.getVariant()) {
            if(isShowcaseMode() && this.getOwner() != null) {
                //only let owner do things
                if(!player.equals(this.getOwner())) return InteractionResult.PASS;
            }
            itemStack.consume(1, player);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemStack.split(1));
            this.setOwnerUUID(player.getUUID());
            this.level().playSound(this.level().isClientSide ? player : null, this.getX(), this.getY(), this.getZ(), SoundEvents.ALLAY_AMBIENT_WITH_ITEM, SoundSource.AMBIENT);
            if(hadItem || newVariant != this.getVariant()) {
                if(this.level().isClientSide) {
                    PacketDistributor.sendToServer(new BdubsVariantChangeEffectsPacket(this.getId(), false));
                }
                this.wave(true, true);
            }
            this.setVariant(newVariant);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if(isOwner && hadItem && !itemStack.is(Items.SPYGLASS) && !isShowcaseMode()) {
            if(player instanceof ServerPlayer serverPlayer) {
                ownerInteractionTicks = 0;
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

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }
}
