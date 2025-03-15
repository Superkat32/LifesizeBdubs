package net.superkat.lifesizebdubs.entity;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableShoulderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.superkat.lifesizebdubs.LifeSizeBdubs;
import net.superkat.lifesizebdubs.data.BdubsVariant;
import net.superkat.lifesizebdubs.network.BdubsEffectPacket;
import net.superkat.lifesizebdubs.network.BdubsMessagePacket;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class BdubsEntity extends TameableShoulderEntity implements VariantHolder<BdubsVariant>, GeoEntity {
    private static final TrackedData<Integer> VARIANT_ID = DataTracker.registerData(BdubsEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> SUGAR_TICKS_ID = DataTracker.registerData(BdubsEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> SHOWCASE_MODE_ID = DataTracker.registerData(BdubsEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> PUSHABLE_ID = DataTracker.registerData(BdubsEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SHOULD_DESPAWN_ID = DataTracker.registerData(BdubsEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Optional<UUID>> PROFILE_UUID_ID = DataTracker.registerData(BdubsEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    public ProfileComponent profile;
    public int ownerInteractionTicks = 0;

    public boolean onShoulder = false;
    public PlayerEntity shoulderRidingPlayer = null;
//    public int messageTicks = 1200;
    public int messageTicks = 10;
    public int lastMessageTicks = 0;
    public List<Text> lastMessages = new ArrayList<>();
    public Text lastTimedMessage = null;

    public int idleAnimTicks = 300;
    public int ticksSinceIdleAnim = 0;
    public int waveTicks = 10;
    public int ticksSinceWave = 0;
    public int ticksSinceSpyglassWave = 0;
    public PlayerEntity spyglassWavedPlayer = null;

    public BdubsEntity(EntityType<? extends TameableShoulderEntity> entityType, World world) {
        super(entityType, world);
        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0f);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);

        builder.add(VARIANT_ID, BdubsVariant.toId(this.getRegistryManager(), BdubsVariant.DEFAULT));
        builder.add(SUGAR_TICKS_ID, 0);
        builder.add(SHOWCASE_MODE_ID, false);
        builder.add(PUSHABLE_ID, true);
        builder.add(SHOULD_DESPAWN_ID, true);
        builder.add(PROFILE_UUID_ID, Optional.empty());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        this.getRegistryManager().get(LifeSizeBdubs.BDUBS_VARIANT_KEY)
                .getEntry(BdubsVariant.toId(this.getRegistryManager(), this.getVariant()))
                .flatMap(RegistryEntry.Reference::getKey)
                        .ifPresent(key -> nbt.putString("variant", key.getValue().toString()));

        nbt.putInt("sugarticks", getSugarTicks());
        nbt.putBoolean("showcaseMode", isShowcaseMode());
        nbt.putBoolean("pushable", pushable());
        nbt.putBoolean("shouldDespawn", shouldDespawn());

        if(this.profile != null && this.getProfileUuid().isPresent()) nbt.putUuid("profile_uuid", this.getProfileUuid().get());
//        if(this.profile != null) {
//            nbt.put("profile", ProfileComponent.CODEC.encodeStart(NbtOps.INSTANCE, this.profile).getOrThrow());
//        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        BdubsVariant variant = BdubsVariant.fromNbt(this.getRegistryManager(), nbt);
        if(variant != null) this.setVariant(variant);

        if(nbt.contains("sugarticks")) this.setSugarTicks(nbt.getInt("sugarticks"));
        if(nbt.contains("showcaseMode")) this.setShowcaseMode(nbt.getBoolean("showcaseMode"));
        if(nbt.contains("pushable")) this.setPushable(nbt.getBoolean("pushable"));
        if(nbt.contains("shouldDespawn")) this.setShouldDespawn(nbt.getBoolean("shouldDespawn"));

        if(nbt.contains("profile")) {
            ProfileComponent.CODEC.parse(NbtOps.INSTANCE, nbt.get("profile"))
                    .resultOrPartial(s -> LifeSizeBdubs.LOGGER.error("Failed to load profile for Bdubs: {}", s))
                    .ifPresent(this::setProfile);
        }
        if(nbt.contains("profile_uuid")) this.setProfileUuid(nbt.getUuid("profile_uuid"));

    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8f));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        AnimationController<BdubsEntity> animController = new AnimationController<>(this, BdubsAnims.controller, 5, event -> {
            if(this.isDead()) return event.setAndContinue(BdubsAnims.DEATH_ANIM);
            if(this.getSugarTicks() > 0) return event.setAndContinue(BdubsAnims.SUGAR_IDLE_ANIM);

            return event.setAndContinue(BdubsAnims.IDLE_ANIM);
        });

        BdubsAnims.registerIdleAnims(animController);
        BdubsAnims.triggerableAnim(animController, BdubsAnims.DESPAWN_ANIM);
        controllerRegistrar.add(animController);
    }

    @Override
    public Text getDisplayName() {
        if(this.isDead()) {
            if(this.getProfile() != null) {
                return Text.of(this.getProfile().name().get());
            }
            return this.getVariant().getName();
        }
        return super.getDisplayName();
    }

    @Override
    public void tick() {
        super.tick();

        this.ownerInteractionTicks++;

        if(this.ownerInteractionTicks >= 6000 && (!isShowcaseMode() || shouldDespawn())) {
            BdubsAnims.triggerAnim(this, BdubsAnims.DESPAWN_ANIM);
            if(this.ownerInteractionTicks >= 6045) this.discard();
            return;
        }

        int sugarTicks = this.getSugarTicks();
        if(sugarTicks > 0) this.setSugarTicks(sugarTicks - 1);

        if(this.getWorld().isClient()) return;
        this.tickAnimation();
    }

    public void tickAnimation() {
        this.idleAnimTicks--;
        this.waveTicks--;
        this.ticksSinceIdleAnim++;
        this.ticksSinceWave++;
        this.ticksSinceSpyglassWave++;

        if(idleAnimTicks <= 0) playIdleAnim();
        if(waveTicks == 0) this.wave(true);
        tryWavingAtSpyglass();
    }

    //likely called serverside
    public void tickMessages() {
        if(this.getOwner() == null || !this.onShoulder) return;
        if(this.getProfile() != null) return;
        messageTicks--;
        lastMessageTicks++;
        tickMessageList();
        tickTimedMessages();
    }

    private void tickMessageList() {
        BdubsVariant variant = this.getVariant();
        if(messageTicks > 0 || variant.getMessages().isEmpty()) return;
        List<Text> messages = variant.getMessages().get();

        Text sentMessage = null;
        for (int i = 0; i < 10; i++) {
            int msgIndex = this.random.nextInt(messages.size());
            Text message = messages.get(msgIndex);
            if(message == null || message.getString().isEmpty() || lastMessages.contains(message)) continue;

            sentMessage = message;
            break;
        }

        if(sentMessage != null) {
            sendMessageToOwner(sentMessage);
            lastMessages.addFirst(sentMessage);
            int maxLastMessages = messages.size() > 5 ? 5 : messages.size() - 1;
            if (lastMessages.size() > maxLastMessages) {
                lastMessages.removeLast();
            }
        }

        messageTicks = this.random.nextBetween(7500, 10000);
    }

    private void tickTimedMessages() {
        int time = (int) this.getWorld().getTimeOfDay();
        BdubsVariant variant = this.getVariant();
        if(variant.getTimedMessages().isEmpty()) return;

        List<Pair<Text, Integer>> timedMessages = variant.getTimedMessages().get();
        for (Pair<Text, Integer> message : timedMessages) {
            if(message.getSecond() == time) {
                Text msg = message.getFirst();
                if(msg == null || msg.getString().isEmpty()) continue;

                boolean isPreviousMessage = msg.equals(lastTimedMessage);
                boolean sendMessage = !isPreviousMessage || lastMessageTicks >= 20;
                if(!sendMessage) continue;

                lastTimedMessage = msg;
                lastMessageTicks = 0;
                sendMessageToOwner(msg);
            }
        }
    }

    public void sendMessageToOwner(Text message) {
        if(this.getOwner() instanceof ServerPlayerEntity owner) {
            BdubsVariant variant = getVariant();
            Text sentMessage = Text.translatable("entity.lifesizebdubs.funnybdubsmessage", variant.getName(), message);
            ServerPlayNetworking.send(owner, new BdubsMessagePacket(sentMessage, false));
        }
    }

    public void playIdleAnim() {
        BdubsAnims.playIdleAnim(this);
        this.idleAnimTicks = this.getRandom().nextBetween(200, 1000);
        this.ticksSinceIdleAnim = 0;
    }

    public void tryWavingAtSpyglass() {
        List<PlayerEntity> spyingPlayers = this.getWorld().getPlayers(
                TargetPredicate.createNonAttackable().setPredicate(
                        player -> player.getActiveItem().isOf(Items.SPYGLASS)
                ), this, this.getBoundingBox().expand(50, 25, 50));

        for (PlayerEntity spyingPlayer : spyingPlayers) {
            if(!(spyingPlayer instanceof ServerPlayerEntity player)) continue;

            Vec3d eyePos = player.getEyePos();
            Vec3d viewVector = player.getRotationVec(1f);
            Vec3d added = eyePos.add(viewVector.multiply(100f, 100f, 100f));

            EntityHitResult hitResult = ProjectileUtil.getEntityCollision(player.getWorld(), player,
                    eyePos, added, new Box(eyePos, added).expand(1),
                    entity -> !entity.isSpectator(), 0.1f);

            if(hitResult != null && hitResult.getEntity() == this && player.canSee(this)) {
                boolean overrideWaitTime = player.getItemUseTime() <= 40 && this.ticksSinceSpyglassWave > 20 && this.ticksSinceIdleAnim >= 50;
                if(player != this.spyglassWavedPlayer || (ticksSinceSpyglassWave >= 140 || overrideWaitTime)) {
                    this.ticksSinceSpyglassWave = 0;
                    this.spyglassWavedPlayer = player;
                    boolean cheer = this.getRandom().nextBetween(1, 15) == 1; //rare chance of cheering at the owner(if owner)
                    this.wave(cheer, true);
                }
            }
        }
    }

    public void wave(boolean canCheer) {
        this.wave(canCheer, false);
    }

    public void wave(boolean canCheer, boolean overrideTime) {
        if(ticksSinceWave < 70 && !overrideTime) return;
        if(canCheer && this.getOwner() != null && this.getOwner().getMainHandStack().isOf(Items.SPYGLASS)) {
            BdubsAnims.triggerAnim(this, BdubsAnims.CHEER_ANIM);
        } else {
            if(overrideTime) {
                this.getAnimatableInstanceCache().getManagerForId(this.getId()).getAnimationControllers().get(BdubsAnims.controller).forceAnimationReset();
            }
            BdubsAnims.triggerAnim(this, BdubsAnims.WAVE_ANIM);
        }

        ticksSinceWave = 0;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ActionResult variantUpdateResult = attemptVariantUpdate(player, hand);
        if(variantUpdateResult != ActionResult.PASS) return variantUpdateResult;

        ActionResult sugarResult = feedPureSugar(player, hand);
        if(sugarResult != ActionResult.PASS) return sugarResult;

        ActionResult mountResult = this.attemptMountPlayer(player, hand);
        if(mountResult != ActionResult.PASS) return mountResult;

        return super.interactMob(player, hand);
    }

    public ActionResult attemptVariantUpdate(PlayerEntity player, Hand hand) {
        ItemStack item = player.getStackInHand(hand);
        boolean hasOwner = this.getOwnerUuid() != null;
        boolean isOwner = hasOwner && player == this.getOwner();
        boolean bdubsHadItem = this.getEquippedStack(EquipmentSlot.MAINHAND) != ItemStack.EMPTY;
        boolean consumeItem = true;
        boolean actuallySetVariant = true;

        if(this.isShowcaseMode() && hasOwner) {
            if(!isOwner) return ActionResult.PASS; //only let owner do things
        }

        if(hasOwner && !isOwner) return ActionResult.PASS;

        BdubsVariant newVariant = null;
        BdubsVariant currentVariant = this.getVariant();
        Set<BdubsVariant> variantsWithItem = BdubsVariant.variantsWithItem(this.getRegistryManager(), item);
        if(variantsWithItem.isEmpty()) return ActionResult.PASS;
        List<BdubsVariant> sortedVariants = variantsWithItem.stream().sorted(Comparator.comparing(o -> o.getName().getString())).toList();

        if(variantsWithItem.size() > 1 && variantsWithItem.contains(currentVariant)) {
            //cycle through variants
            int currentIndex = sortedVariants.indexOf(currentVariant);
            int newIndex = (currentIndex + 1) >= sortedVariants.size() ? 0 : currentIndex + 1;
            newVariant = sortedVariants.get(newIndex);

            consumeItem = false;
        } else {
            //choose random variant
            int variantIndex = this.getRandom().nextInt(sortedVariants.size());
            newVariant = sortedVariants.get(variantIndex);
            //let server set variant to prevent switching between 2 variants for a split second on the client
            actuallySetVariant = !this.getWorld().isClient();
        }

        if(newVariant == null || currentVariant == newVariant) return ActionResult.PASS;

        //if item is sugar, player needs to be sneaking to cycle through variants
        if(item.isOf(Items.SUGAR)) {
            if(hasOwner && !player.isSneaking()) return ActionResult.PASS;
        }

        this.equipStack(EquipmentSlot.MAINHAND, consumeItem ? item.split(1) : item);
        if(actuallySetVariant) {
            this.setVariant(newVariant);
        }

        this.setOwner(player);
        this.getWorld().playSound(this.getWorld().isClient() ? player : null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ALLAY_AMBIENT_WITH_ITEM, SoundCategory.AMBIENT);
        if(!hasOwner || bdubsHadItem) {
            sendEffectUpdate(false);
            this.wave(true, true);
        }

        return ActionResult.SUCCESS;
    }

    public ActionResult feedPureSugar(PlayerEntity player, Hand hand) {
        ItemStack item = player.getStackInHand(hand);
        boolean hasOwner = this.getOwnerUuid() != null;
        boolean isOwner = hasOwner && player == this.getOwner();

        if(hasOwner && !isOwner) return ActionResult.PASS;

        if(!item.isOf(Items.SUGAR) || !isOwner || isShowcaseMode()) return ActionResult.PASS;
        item.split(1);
        this.setSugarTicks(1200);

        sendEffectUpdate(true);
        BdubsAnims.triggerAnim(this, BdubsAnims.GIVEN_SUGAR_ANIM);

        return ActionResult.SUCCESS;
    }

    public ActionResult attemptMountPlayer(PlayerEntity player, Hand hand) {
        ItemStack item = player.getStackInHand(hand);
        boolean hasOwner = this.getOwnerUuid() != null;
        boolean isOwner = hasOwner && player == this.getOwner();
        boolean bdubsHadItem = this.getEquippedStack(EquipmentSlot.MAINHAND) != ItemStack.EMPTY;

        if(!isOwner || !bdubsHadItem || isShowcaseMode() || item.isOf(Items.SPYGLASS)) return ActionResult.PASS;
        if(!(player instanceof ServerPlayerEntity serverPlayer)) return ActionResult.PASS;
        ownerInteractionTicks = 0;
        this.mountOnto(serverPlayer);
        return ActionResult.SUCCESS;
    }

    public void sendEffectUpdate(boolean sugarMode) {
        if(!this.getWorld().isClient()) return;
        ClientPlayNetworking.send(new BdubsEffectPacket(this.getId(), sugarMode));
    }

    public void onEffectUpdate(boolean sugarMode) {
        if(!this.getWorld().isClient()) return;
        if(sugarMode) {
            sugarEffects();
        } else {
            variantEffects();
        }
    }

    public void variantEffects() {
        this.spawnFunnyParticles();
    }

    public void sugarEffects() {
        this.spawnEvenFunnierParticles();

        this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ALLAY_ITEM_GIVEN, SoundCategory.AMBIENT, 2f, 1f, true);
        this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_BREEZE_CHARGE, SoundCategory.AMBIENT, 1f, 1.25f, true);
        this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_BREEZE_DEFLECT, SoundCategory.AMBIENT, 1f, 1.25f, true);
        this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_TRIDENT_THUNDER.value(), SoundCategory.AMBIENT, 0.3f, 2f, true);
    }

    public void spawnFunnyParticles() {
        for (int i = 0; i < 7; i++) {
            this.getWorld().addParticle(ParticleTypes.END_ROD, this.getX(), this.getEyeY(), this.getZ(), this.random.nextGaussian() / 4, this.random.nextGaussian() / 8, this.random.nextGaussian() / 4);
            this.getWorld().addParticle(ParticleTypes.WAX_OFF, this.getX(), this.getEyeY(), this.getZ(), this.random.nextGaussian() * 8, this.random.nextGaussian(), this.random.nextGaussian() * 8);
            this.getWorld().addParticle(ParticleTypes.WHITE_SMOKE, this.getX(), this.getEyeY(), this.getZ(), this.random.nextGaussian() / 2, this.random.nextGaussian() / 8, this.random.nextGaussian() / 2);
        }
    }

    public void spawnEvenFunnierParticles() {
        for (int i = 0; i < 7; i++) {
            this.getWorld().addParticle(ParticleTypes.END_ROD, this.getX(), this.getEyeY(), this.getZ(), this.random.nextGaussian() / 4, this.random.nextGaussian() / 8, this.random.nextGaussian() / 4);
            this.getWorld().addParticle(ParticleTypes.WAX_OFF, this.getX(), this.getEyeY(), this.getZ(), this.random.nextGaussian() * 8, this.random.nextGaussian() * 4, this.random.nextGaussian() * 8);
            this.getWorld().addParticle(ParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS, this.getX(), this.getEyeY(), this.getZ(), this.random.nextGaussian() / 24, 0.01, this.random.nextGaussian() / 24);
        }
        this.getWorld().addParticle(ParticleTypes.SONIC_BOOM, this.getX(), this.getEyeY(), this.getZ(), 0, 0, 0);
    }

    public void setProfile(@Nullable ProfileComponent profile) {
        synchronized (this) {
            this.profile = profile;
        }

        this.loadProfileProperties();
    }

    private void loadProfileProperties() {
        if(this.profile != null && !this.profile.isCompleted()) {
            this.profile.getFuture().thenAcceptAsync(profileComponent -> {
                this.profile = profileComponent;
                this.setProfileUuid(profileComponent.gameProfile().getId());
            }, SkullBlockEntity.EXECUTOR);
        }
    }

    @Nullable
    public ProfileComponent getProfile() {
        if(this.profile == null && this.getProfileUuid().isPresent()) {
            this.setProfileUuid(this.getProfileUuid().get());
        }
        return this.profile;
    }

    public void setProfileUuid(UUID uuid) {
        this.dataTracker.set(PROFILE_UUID_ID, Optional.ofNullable(uuid));

        SkullBlockEntity.fetchProfileByUuid(uuid).thenAccept(gameProfile -> {
            gameProfile.ifPresent(value -> this.profile = new ProfileComponent(value));
        });
    }

    public Optional<UUID> getProfileUuid() {
        return this.dataTracker.get(PROFILE_UUID_ID);
    }

    @Override
    public BdubsVariant getVariant() {
        return BdubsVariant.fromId(this.getRegistryManager(), this.dataTracker.get(VARIANT_ID));
    }

    @Override
    public void setVariant(BdubsVariant variant) {
        int id = BdubsVariant.toId(this.getRegistryManager(), variant);
        this.dataTracker.set(VARIANT_ID, id);
    }

    public int getSugarTicks() {
        return this.dataTracker.get(SUGAR_TICKS_ID);
    }

    public void setSugarTicks(int ticks) {
        this.dataTracker.set(SUGAR_TICKS_ID, ticks);
    }

    public boolean isShowcaseMode() {
        return this.dataTracker.get(SHOWCASE_MODE_ID);
    }

    public void setShowcaseMode(boolean showcaseMode) {
        this.dataTracker.set(SHOWCASE_MODE_ID, showcaseMode);
        this.setInvulnerable(showcaseMode);
        this.setPushable(!showcaseMode);
        this.setShouldDespawn(!showcaseMode);
    }

    public boolean pushable() {
        return this.dataTracker.get(PUSHABLE_ID);
    }

    public void setPushable(boolean pushable) {
        this.dataTracker.set(PUSHABLE_ID, pushable);
    }

    public boolean shouldDespawn() {
        return this.dataTracker.get(SHOULD_DESPAWN_ID);
    }

    public void setShouldDespawn(boolean shouldDespawn) {
        this.dataTracker.set(SHOULD_DESPAWN_ID, shouldDespawn);
    }

    public void activateShoulderMode(PlayerEntity player) {
        this.onShoulder = true;
        this.shoulderRidingPlayer = player;
//        this.setYaw(0); //<- THIS IS BAD DON'T DO
        this.setBodyYaw(0); //HAHAHAHA FIXED THE SPINNING BUG HAHAHAHAHHAHAA... - also it's a feature now lol
        this.prevBodyYaw = 0;
        this.setHeadYaw(0);
        this.prevHeadYaw = 0;
        this.setPitch(0);
        this.prevPitch = 0;

        this.hurtTime = 0; //prevent staying hurt while on shoulder wow what a crazy bug that was
    }



    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_AXOLOTL_IDLE_AIR;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_AXOLOTL_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_AXOLOTL_DEATH;
    }

    @Override
    public void setVelocity(Vec3d velocity) {
        if(isShowcaseMode() || !pushable()) return; //no velocity on showcase mode or not pushable to prevent fishing rods pulling
        super.setVelocity(velocity);
    }

    @Override
    public boolean isPushable() {
        return super.isPushable() && this.pushable();
    }

    @Override
    protected boolean shouldFollowLeash() {
        return false;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }
}
