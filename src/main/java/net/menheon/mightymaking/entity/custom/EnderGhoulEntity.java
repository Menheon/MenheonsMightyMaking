package net.menheon.mightymaking.entity.custom;

import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import software.bernie.geckolib.core.animation.AnimationController;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import software.bernie.geckolib.core.animation.Animation;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.syncher.SynchedEntityData;
import software.bernie.geckolib.core.object.PlayState;
import net.minecraft.world.damagesource.DamageSource;
import software.bernie.geckolib.animatable.GeoEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import java.util.function.Predicate;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.core.BlockPos;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class EnderGhoulEntity extends Monster implements GeoEntity, NeutralMob {
  private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
  private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
  private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_UUID,
      "Attacking speed boost", (double) 0.15F, AttributeModifier.Operation.ADDITION);
  private static final EntityDataAccessor<Optional<BlockState>> DATA_CARRY_STATE = SynchedEntityData
      .defineId(EnderGhoulEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);
  private static final EntityDataAccessor<Boolean> DATA_CREEPY = SynchedEntityData.defineId(EnderGhoulEntity.class,
      EntityDataSerializers.BOOLEAN);
  private static final EntityDataAccessor<Boolean> DATA_STARED_AT = SynchedEntityData.defineId(EnderGhoulEntity.class,
      EntityDataSerializers.BOOLEAN);
  private int lastStareSound = Integer.MIN_VALUE;
  private int targetChangeTime;
  private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
  private int remainingPersistentAngerTime;
  @Nullable
  private UUID persistentAngerTarget;

  public EnderGhoulEntity(EntityType<? extends Monster> entityType, Level level) {
    super(entityType, level);
  }

  public static AttributeSupplier setAttributes() {
    return Monster.createMonsterAttributes()
        .add(Attributes.MAX_HEALTH, 40D)
        .add(Attributes.MOVEMENT_SPEED, 0.4F)
        .add(Attributes.ATTACK_DAMAGE, 7.5D)
        .add(Attributes.FOLLOW_RANGE, 64.0D)
        .build();
  }

  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(0, new FloatGoal(this));
    this.goalSelector.addGoal(1, new EnderGhoulEntity.EnderGhoulFreezeWhenLookedAt(this));
    this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
    this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
    this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 0.0F));
    this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

    this.targetSelector.addGoal(1, new EnderGhoulEntity.EnderGhoulLookForPlayerGoal(this, this::isAngryAt));
    this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return this.cache;
  }

  @Override
  public void registerControllers(ControllerRegistrar registrar) {
    registrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
  }

  private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
    if (tAnimationState.isMoving()) {
      tAnimationState.getController()
          .setAnimation(RawAnimation.begin().then("animation.ender_ghoul.walk", Animation.LoopType.LOOP));
      return PlayState.CONTINUE;
    }

    tAnimationState.getController()
        .setAnimation(RawAnimation.begin().then("animation.ender_ghoul.idle", Animation.LoopType.LOOP));
    return PlayState.CONTINUE;
  }

  public void setTarget(@Nullable LivingEntity targetEntity) {
    AttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
    if (targetEntity == null) {
      this.targetChangeTime = 0;
      this.entityData.set(DATA_CREEPY, false);
      this.entityData.set(DATA_STARED_AT, false);
      attributeInstance.removeModifier(SPEED_MODIFIER_ATTACKING);
    } else {
      this.targetChangeTime = this.tickCount;
      this.entityData.set(DATA_CREEPY, true);
      if (!attributeInstance.hasModifier(SPEED_MODIFIER_ATTACKING)) {
        attributeInstance.addTransientModifier(SPEED_MODIFIER_ATTACKING);
      }
    }
    super.setTarget(targetEntity); // Forge: Moved down to allow event handlers to write data manager values.
  }

  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(DATA_CARRY_STATE, Optional.empty());
    this.entityData.define(DATA_CREEPY, false);
    this.entityData.define(DATA_STARED_AT, false);
  }

  public void startPersistentAngerTimer() {
    this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
  }

  public void setRemainingPersistentAngerTime(int angerTime) {
    this.remainingPersistentAngerTime = angerTime;
  }

  public int getRemainingPersistentAngerTime() {
    return this.remainingPersistentAngerTime;
  }

  public void setPersistentAngerTarget(@Nullable UUID uuid) {
    this.persistentAngerTarget = uuid;
  }

  @Nullable
  public UUID getPersistentAngerTarget() {
    return this.persistentAngerTarget;
  }

  public void playStareSound() {
    if (this.tickCount >= this.lastStareSound + 400) {
      this.lastStareSound = this.tickCount;
      if (!this.isSilent()) {
        this.level.playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENDERMAN_STARE,
            this.getSoundSource(), 2.5F, 1.0F, false);
      }
    }
  }

  public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
    if (DATA_CREEPY.equals(accessor) && this.hasBeenStaredAt() && this.level.isClientSide) {
      this.playStareSound();
    }
    super.onSyncedDataUpdated(accessor);
  }

  public void addAdditionalSaveData(CompoundTag tag) {
    super.addAdditionalSaveData(tag);
    this.addPersistentAngerSaveData(tag);
  }

  public void readAdditionalSaveData(CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    this.readPersistentAngerSaveData(this.level, tag);
  }

  boolean isLookingAtMe(Player player) {
    Vec3 vec3 = player.getViewVector(1.0F).normalize();
    Vec3 vec31 = new Vec3(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(),
        this.getZ() - player.getZ());
    double d0 = vec31.length();
    vec31 = vec31.normalize();
    double d1 = vec3.dot(vec31);
    return d1 > 1.0D - 0.025D / d0 ? player.hasLineOfSight(this) : false;
  }

  protected float getStandingEyeHeight(Pose p_32517_, EntityDimensions p_32518_) {
    return 2.85F;
  }

  public void aiStep() {
    if (this.level.isClientSide) {
      for (int i = 0; i < 2; ++i) {
        this.level.addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY() - 0.25D,
            this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(),
            (this.random.nextDouble() - 0.5D) * 2.0D);
      }
    }

    this.jumping = false;
    if (!this.level.isClientSide) {
      this.updatePersistentAnger((ServerLevel) this.level, true);
    }

    super.aiStep();
  }

  public boolean isSensitiveToWater() {
    return true;
  }

  protected void customServerAiStep() {
    if (this.level.isDay() && this.tickCount >= this.targetChangeTime + 600) {
      float f = this.getLightLevelDependentMagicValue();
      if (f > 0.5F && this.level.canSeeSky(this.blockPosition())
          && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
        this.setTarget((LivingEntity) null);
        this.teleport();
      }
    }

    super.customServerAiStep();
  }

  protected boolean teleport() {
    if (!this.level.isClientSide() && this.isAlive()) {
      double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
      double d1 = this.getY() + (double) (this.random.nextInt(64) - 32);
      double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
      return this.teleport(d0, d1, d2);
    } else {
      return false;
    }
  }

  boolean teleportTowardsEntity(Entity entity) {
    Vec3 vec3 = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(), this.getZ() - entity.getZ());
    vec3 = vec3.normalize();
    double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.x * 16.0D;
    double d2 = this.getY() + (double) (this.random.nextInt(16) - 8) - vec3.y * 16.0D;
    double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.z * 16.0D;
    return this.teleport(d1, d2, d3);
  }

  private boolean teleport(double p_32544_, double p_32545_, double p_32546_) {
    BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(p_32544_, p_32545_, p_32546_);

    while (mutableBlockPos.getY() > this.level.getMinBuildHeight()
        && !this.level.getBlockState(mutableBlockPos).getMaterial().blocksMotion()) {
      mutableBlockPos.move(Direction.DOWN);
    }

    BlockState blockState = this.level.getBlockState(mutableBlockPos);
    boolean flag = blockState.getMaterial().blocksMotion();
    boolean flag1 = blockState.getFluidState().is(FluidTags.WATER);
    if (flag && !flag1) {
      net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory
          .onEnderTeleport(this, p_32544_, p_32545_, p_32546_);
      if (event.isCanceled())
        return false;
      Vec3 vec3 = this.position();
      boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
      if (flag2) {
        this.level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
        if (!this.isSilent()) {
          this.level.playSound((Player) null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT,
              this.getSoundSource(), 1.0F, 1.0F);
          this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
      }
      return flag2;
    } else {
      return false;
    }
  }

  protected SoundEvent getAmbientSound() {
    return this.isCreepy() ? SoundEvents.ENDERMAN_SCREAM : SoundEvents.ENDERMAN_AMBIENT;
  }

  protected SoundEvent getHurtSound(DamageSource p_32527_) {
    return SoundEvents.ENDERMAN_HURT;
  }

  protected SoundEvent getDeathSound() {
    return SoundEvents.ENDERMAN_DEATH;
  }

  protected void dropCustomDeathLoot(DamageSource damageSource, int p_32498_, boolean p_32499_) {
    super.dropCustomDeathLoot(damageSource, p_32498_, p_32499_);
  }

  public boolean hurt(DamageSource damageSource, float amount) {
    boolean isHurt = super.hurt(damageSource, amount);
    if (this.isInvulnerableTo(damageSource)) {
      return false;
    } else if (damageSource.getEntity() instanceof Player) {
      for (int i = 0; i < 64; ++i) {
        if (this.teleport()) {
          return true;
        }
      }
      return isHurt;
    } else {
      boolean flag = damageSource.getDirectEntity() instanceof ThrownPotion;
      if (!damageSource.is(DamageTypeTags.IS_PROJECTILE) && !flag) {
        boolean flag2 = super.hurt(damageSource, amount);
        if (!this.level.isClientSide() && !(damageSource.getEntity() instanceof LivingEntity)
            && this.random.nextInt(10) != 0) {
          this.teleport();
        }
        return flag2;
      } else {
        boolean flag1 = flag
            && this.hurtWithCleanWater(damageSource, (ThrownPotion) damageSource.getDirectEntity(), amount);

        for (int i = 0; i < 64; ++i) {
          if (this.teleport()) {
            return true;
          }
        }
        return flag1;
      }
    }
  }

  private boolean hurtWithCleanWater(DamageSource damageSource, ThrownPotion thrownPotion, float damage) {
    ItemStack itemstack = thrownPotion.getItem();
    Potion potion = PotionUtils.getPotion(itemstack);
    List<MobEffectInstance> list = PotionUtils.getMobEffects(itemstack);
    boolean flag = potion == Potions.WATER && list.isEmpty();
    return flag ? super.hurt(damageSource, damage) : false;
  }

  public boolean isCreepy() {
    return this.entityData.get(DATA_CREEPY);
  }

  public boolean hasBeenStaredAt() {
    return this.entityData.get(DATA_STARED_AT);
  }

  public void setBeingStaredAt() {
    this.entityData.set(DATA_STARED_AT, true);
  }

  public boolean requiresCustomPersistence() {
    return super.requiresCustomPersistence();
  }

  static class EnderGhoulFreezeWhenLookedAt extends Goal {
    private final EnderGhoulEntity enderGhoul;
    @Nullable
    private LivingEntity target;

    public EnderGhoulFreezeWhenLookedAt(EnderGhoulEntity enderGhoul) {
      this.enderGhoul = enderGhoul;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    public boolean canUse() {
      this.target = this.enderGhoul.getTarget();
      if (!(this.target instanceof Player)) {
        return false;
      } else {
        double d0 = this.target.distanceToSqr(this.enderGhoul);
        return d0 > 256.0D ? false : this.enderGhoul.isLookingAtMe((Player) this.target);
      }
    }

    public void start() {
      this.enderGhoul.getNavigation().stop();
    }

    public void tick() {
      this.enderGhoul.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
    }
  }

  static class EnderGhoulLookForPlayerGoal extends NearestAttackableTargetGoal<Player> {
    private final EnderGhoulEntity enderGhoul;
    @Nullable
    private Player pendingTarget;
    private int aggroTime;
    private int teleportTime;
    private final TargetingConditions startAggroTargetConditions;
    private final TargetingConditions continueAggroTargetConditions = TargetingConditions.forCombat()
        .ignoreLineOfSight();
    private final Predicate<LivingEntity> isAngerInducing;

    public EnderGhoulLookForPlayerGoal(EnderGhoulEntity enderGhoul, @Nullable Predicate<LivingEntity> entity) {
      super(enderGhoul, Player.class, 10, false, false, entity);
      this.enderGhoul = enderGhoul;
      this.isAngerInducing = (player) -> {
        return (enderGhoul.isLookingAtMe((Player) player) || enderGhoul.isAngryAt(player))
            && !enderGhoul.hasIndirectPassenger(player);
      };
      this.startAggroTargetConditions = TargetingConditions.forCombat().range(this.getFollowDistance())
          .selector(this.isAngerInducing);
    }

    public boolean canUse() {
      this.pendingTarget = this.enderGhoul.level.getNearestPlayer(this.startAggroTargetConditions, this.enderGhoul);
      return this.pendingTarget != null;
    }

    public void start() {
      this.aggroTime = this.adjustedTickDelay(5);
      this.teleportTime = 0;
      this.enderGhoul.setBeingStaredAt();
    }

    public void stop() {
      this.pendingTarget = null;
      super.stop();
    }

    public boolean canContinueToUse() {
      if (this.pendingTarget != null) {
        if (!this.isAngerInducing.test(this.pendingTarget)) {
          return false;
        } else {
          this.enderGhoul.lookAt(this.pendingTarget, 10.0F, 10.0F);
          return true;
        }
      } else {
        if (this.target != null) {
          if (this.enderGhoul.hasIndirectPassenger(this.target)) {
            return false;
          }
          if (this.continueAggroTargetConditions.test(this.enderGhoul, this.target)) {
            return true;
          }
        }
        return super.canContinueToUse();
      }
    }

    public void tick() {
      if (this.enderGhoul.getTarget() == null) {
        super.setTarget((LivingEntity) null);
      }
      if (this.pendingTarget != null) {
        if (--this.aggroTime <= 0) {
          this.target = this.pendingTarget;
          this.pendingTarget = null;
          super.start();
        }
      } else {
        if (this.target != null && !this.enderGhoul.isPassenger()) {
          if (this.enderGhoul.isLookingAtMe((Player) this.target)) {
            if (this.target.distanceToSqr(this.enderGhoul) < 16.0D) {
              this.enderGhoul.teleport();
            }
            this.teleportTime = 0;
          } else if (this.target.distanceToSqr(this.enderGhoul) > 256.0D
              && this.teleportTime++ >= this.adjustedTickDelay(30)
              && this.enderGhoul.teleportTowardsEntity(this.target)) {
            this.teleportTime = 0;
          }
        }
        super.tick();
      }
    }
  }
}
