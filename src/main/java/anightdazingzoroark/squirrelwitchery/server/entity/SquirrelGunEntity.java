package anightdazingzoroark.squirrelwitchery.server.entity;

import anightdazingzoroark.riftlib.core.AnimatableRunValue;
import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.controller.AnimationControllerState;
import anightdazingzoroark.riftlib.core.manager.AnimationDataEntityLiving;
import anightdazingzoroark.squirrelwitchery.server.sounds.SquirrelWitcherySounds;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class SquirrelGunEntity extends EntityMob implements IAnimatable<AnimationDataEntityLiving>, IRangedAttackMob {
    private static final DataParameter<Boolean> SHOOTING = EntityDataManager.createKey(SquirrelGunEntity.class, DataSerializers.BOOLEAN);
    private static final float SHOT_RANGE = 8f;
    private static final float SHOT_DAMAGE = 2f;

    @NotNull
    private final AnimationDataEntityLiving animationData = new AnimationDataEntityLiving(this);
    @Nullable
    private EntityLivingBase pendingShotTarget;

    public SquirrelGunEntity(@NotNull World worldIn) {
        super(worldIn);
        this.setSize(0.5f, 0.5f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SHOOTING, false);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16D);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackRanged(this, 1D, 60, SHOT_RANGE));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8f));
        this.tasks.addTask(6, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
    }

    @Override
    public boolean getCanSpawnHere() {
        //attempt spawn only when not peaceful
        if (this.world.getDifficulty() == EnumDifficulty.PEACEFUL) return false;

        //spawn only on dirt and grass
        Block ground = this.world.getBlockState(new BlockPos(this).down()).getBlock();
        return ground == Blocks.DIRT || ground == Blocks.GRASS;
    }

    //-----shooting stuff-----
    @Override
    public void attackEntityWithRangedAttack(@NotNull EntityLivingBase target, float distanceFactor) {
        if (this.world.isRemote || !this.isEntityAlive() || !target.isEntityAlive()) return;
        if (target != this.getAttackTarget() || this.getDistanceSq(target) > SHOT_RANGE * SHOT_RANGE) return;
        if (!this.getEntitySenses().canSee(target) || this.isShooting()) return;

        //send request for shootin by changin required variables
        this.pendingShotTarget = target;
        this.setShooting(true);
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {}

    private boolean isShooting() {
        return this.dataManager.get(SHOOTING);
    }

    private void setShooting(boolean value) {
        this.dataManager.set(SHOOTING, value);
    }

    //-----animation stuff-----
    @Override
    @NonNull
    public AnimationDataEntityLiving getAnimationData() {
        return this.animationData;
    }

    @Override
    public void initializeAnimationData(@NonNull AnimationDataEntityLiving animationDataEntityLiving) {
        animationDataEntityLiving.setScale(0.5f);
        animationDataEntityLiving.addInitAnimationValue("shooting", 0D);
        animationDataEntityLiving.addAnimationController(new AnimationController<SquirrelGunEntity, AnimationDataEntityLiving>(
                this, "movementCont", "default",
                new AnimationControllerState<AnimationDataEntityLiving>("default", 0.1)
                        .addStateTransition("walk", AnimationDataEntityLiving::isMoving),
                new AnimationControllerState<AnimationDataEntityLiving>("walk", 0.1)
                        .addAnimation("animation.squirrel_with_gun.walk")
                        .addStateTransition("default", data -> !data.isMoving())
        ));
        animationDataEntityLiving.addAnimationController(new AnimationController<SquirrelGunEntity, AnimationDataEntityLiving>(
                this, "shootingCont", "default",
                new AnimationControllerState<AnimationDataEntityLiving>("default", 0.1)
                        .addStateTransition("shoot", data -> this.isShooting()),
                new AnimationControllerState<AnimationDataEntityLiving>("shoot", 0.1)
                        .addAnimation("animation.squirrel_with_gun.shoot")
                        .addStateTransition("default", data -> data.allAnimationsFinished("shootingCont"))
                        .addExitEffect(data -> {
                            this.setShooting(false);
                            this.pendingShotTarget = null;
                        })
        ));
        animationDataEntityLiving.addAnimationMessageEffect("fireGun", new AnimatableRunValue(() -> {
            if (this.world.isRemote || !this.isEntityAlive()) return;

            EntityLivingBase target = this.pendingShotTarget;

            //consume the request before tracing so a repeated message cannot fire twice.
            this.pendingShotTarget = null;
            if (target == null || !target.isEntityAlive() || target != this.getAttackTarget()) return;
            if (target instanceof EntityPlayer player && (player.isSpectator() || player.capabilities.disableDamage)) return;
            if (this.getDistanceSq(target) > SHOT_RANGE * SHOT_RANGE || !this.getEntitySenses().canSee(target)) return;

            Vec3d start = this.getPositionEyes(1f);
            Vec3d direction = target.getPositionEyes(1f).subtract(start).normalize();
            Vec3d end = start.add(direction.scale(SHOT_RANGE));
            RayTraceResult blockHit = this.world.rayTraceBlocks(start, end, false, true, false);
            double nearestDistance = blockHit == null ? SHOT_RANGE : start.distanceTo(blockHit.hitVec);
            Entity nearestEntity = null;
            AxisAlignedBB searchBounds = this.getEntityBoundingBox()
                    .expand(direction.x * SHOT_RANGE, direction.y * SHOT_RANGE, direction.z * SHOT_RANGE)
                    .grow(1D);

            for (Entity candidate : this.world.getEntitiesWithinAABBExcludingEntity(this, searchBounds)) {
                if (!candidate.canBeCollidedWith()) continue;
                if (candidate instanceof EntityPlayer player && player.isSpectator()) continue;
                if (candidate.getLowestRidingEntity() == this.getLowestRidingEntity() && !candidate.canRiderInteract()) continue;

                AxisAlignedBB hitbox = candidate.getEntityBoundingBox().grow(candidate.getCollisionBorderSize());
                RayTraceResult entityHit = hitbox.calculateIntercept(start, end);
                double distance;
                if (hitbox.contains(start)) distance = 0D;
                else if (entityHit != null) distance = start.distanceTo(entityHit.hitVec);
                else continue;

                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestEntity = candidate;
                }
            }

            this.world.playSound(
                    null, this.posX, this.posY, this.posZ,
                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 0.8f, 1.5f
            );
            if (nearestEntity != null) {
                //scale damage based on world difficulty
                nearestEntity.attackEntityFrom(
                        DamageSource.causeMobDamage(this).setProjectile(),
                        SHOT_DAMAGE * this.world.getDifficulty().ordinal()
                );
            }
        }, Side.SERVER));
    }

    //---sound stuff---
    @Override
    protected SoundEvent getAmbientSound() {
        return SquirrelWitcherySounds.SQUIRREL_AMBIENT;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SquirrelWitcherySounds.SQUIRREL_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SquirrelWitcherySounds.SQUIRREL_DEATH;
    }
}
