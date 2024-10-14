package net.superkat.lifesizebdubs.entity;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.superkat.lifesizebdubs.LifeSizeBdubs;
import net.superkat.lifesizebdubs.data.BdubsVariant;
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

public class BdubsEntity extends TamableAnimal implements VariantHolder<BdubsVariant>, GeoEntity {
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(BdubsEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.bdubs.idle");
    protected static final RawAnimation WAVE_ANIM = RawAnimation.begin().thenLoop("animation.bdubs.wave");
    protected static final RawAnimation CHEER_ANIM = RawAnimation.begin().thenLoop("animation.bdubs.cheer");

    public BdubsVariant variant = BdubsVariant.DEFAULT;

    public BdubsEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        chooseVariant(level);
    }

    public void chooseVariant(Level world) {
        Registry<BdubsVariant> registry = world.registryAccess().registryOrThrow(LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY);
        List<Holder.Reference<BdubsVariant>> holders = registry.holders().toList();
        int number = this.level().random.nextInt(holders.size());
        Holder<BdubsVariant> variant = holders.get(number);
        this.setVariant(variant.value());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8f));
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        Registry<BdubsVariant> registry = this.registryAccess().registryOrThrow(LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY);
        Holder<BdubsVariant> variant = registry.holders().findFirst().or(() -> registry.getHolder(LifeSizeBdubs.BDUBS_DEFAULT_VARIANT)).or(registry::getAny).orElseThrow();
        builder.define(DATA_VARIANT_ID, BdubsVariant.getIntFromVariant(variant.value(), this.registryAccess()));
    }

    @Override
    public void setVariant(@NotNull BdubsVariant variant) {
        int id = BdubsVariant.getIntFromVariant(variant, this.registryAccess());
        this.entityData.set(DATA_VARIANT_ID, id);
        this.variant = variant;
    }

    @Override @NotNull
    public BdubsVariant getVariant() {
        return this.variant;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "default", 5, event -> {
            Player player = this.level().getNearestPlayer(this, 10);
            if(player != null) {
                if(player.getMainHandItem().is(Items.SPYGLASS)) {
                    return event.setAndContinue(CHEER_ANIM);
                }
                return event.setAndContinue(WAVE_ANIM);
            }
            if(!event.isMoving()) {
                return event.setAndContinue(IDLE_ANIM);
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        BdubsVariant newVariant = BdubsVariant.getVariantFromItem(itemStack, this.registryAccess());
        if(newVariant != null && newVariant != this.variant) {
            itemStack.consume(1, player);
            boolean hadItem = this.getItemBySlot(EquipmentSlot.MAINHAND) != ItemStack.EMPTY;
            this.setItemSlot(EquipmentSlot.MAINHAND, itemStack.split(1));
            this.level().playSound(this.level().isClientSide ? player : null, this.getX(), this.getY(), this.getZ(), SoundEvents.ALLAY_AMBIENT_WITH_ITEM, SoundSource.AMBIENT);
            if(hadItem || newVariant != this.variant) {
                spawnFunnyParticles();
            }
            this.setVariant(newVariant);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    public void spawnFunnyParticles() {
        for (int i = 0; i < 7; i++) {
            this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getEyeY(), this.getZ(), this.random.nextGaussian() / 4, this.random.nextGaussian() / 8, this.random.nextGaussian() / 4);
            this.level().addParticle(ParticleTypes.GLOW_SQUID_INK, this.getX(), this.getEyeY(), this.getZ(), this.random.nextGaussian() / 4, this.random.nextGaussian() / 8, this.random.nextGaussian() / 4);
            this.level().addParticle(ParticleTypes.WITCH, this.getX(), this.getEyeY(), this.getZ(), this.random.nextGaussian(), this.random.nextGaussian(), this.random.nextGaussian());
        }
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
