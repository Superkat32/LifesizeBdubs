package net.superkat.lifesizebdubs.entity;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.item.ItemStack;
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

public class BdubsEntity extends TamableAnimal implements VariantHolder<Holder<BdubsVariant>>, GeoEntity {
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(BdubsEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.model.idle");

    public BdubsVariant variant = BdubsVariant.DEFAULT;

    public BdubsEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        chooseVariant(level);
    }
    public void chooseVariant(Level world) {
        Registry<BdubsVariant> registry = world.registryAccess().registryOrThrow(LifeSizeBdubs.BDUBS_VARIANT_REGISTRY_KEY);
//        Holder<BdubsVariant> variant = registry.holders().findFirst().or(() -> registry.getHolder(LifeSizeBdubs.BDUBS_DEFAULT_VARIANT)).or(registry::getAny).orElseThrow();
        List<Holder.Reference<BdubsVariant>> holders = registry.holders().toList();
        int number = this.level().random.nextInt(holders.size());
        Holder<BdubsVariant> variant = holders.get(number);
        this.setVariant(variant);
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
    public void setVariant(Holder<BdubsVariant> variant) {
        BdubsVariant bdubsVariant = variant.value();
        int id = BdubsVariant.getIntFromVariant(bdubsVariant, this.registryAccess());
        this.entityData.set(DATA_VARIANT_ID, id);
        this.variant = bdubsVariant;
    }

    @Override @NotNull
    public Holder<BdubsVariant> getVariant() {
        return Holder.direct(this.variant);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "default", 5, event -> {
            if(!event.isMoving()) {
                return event.setAndContinue(IDLE_ANIM);
            }
            return PlayState.STOP;
        }));
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
