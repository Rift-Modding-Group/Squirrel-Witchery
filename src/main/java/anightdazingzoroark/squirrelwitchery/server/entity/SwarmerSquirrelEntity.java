package anightdazingzoroark.squirrelwitchery.server.entity;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.controller.AnimationControllerState;
import anightdazingzoroark.riftlib.core.manager.AnimationDataEntityLiving;
import anightdazingzoroark.squirrelwitchery.server.sounds.SquirrelWitcherySounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.SoundsTC;

public class SwarmerSquirrelEntity extends EntityMob implements IAnimatable<AnimationDataEntityLiving> {
    @NotNull
    private final AnimationDataEntityLiving animationData = new AnimationDataEntityLiving(this);
    private int remainingTicks = 160;

    public SwarmerSquirrelEntity(@NotNull World worldIn) {
        super(worldIn);
        this.setSize(0.5f, 0.5f);
        this.experienceValue = 0;
    }

    public SwarmerSquirrelEntity(@NotNull World worldIn, @NotNull EntityLivingBase target, double damage) {
        this(worldIn);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(damage);
        this.setAttackTarget(target);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1D, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3D);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!this.world.isRemote) {
            //count down until despawn
            if (this.remainingTicks-- <= 0) {
                this.setDead();
                return;
            }

            //despawn automatically when target is gone
            EntityLivingBase target = this.getAttackTarget();
            if (target == null || !target.isEntityAlive()) {
                this.setDead();
            }
        }
    }

    @Override
    protected void onDeathUpdate() {
        this.setDead();
    }

    @Override
    public void setDead() {
        //make puff
        if (this.world.isRemote) {
            for (int index = 0; index < 8; index++) {
                FXGeneric puff = new FXGeneric(
                        this.world,
                        this.posX + (this.rand.nextDouble() - 0.5D) * this.width,
                        this.posY + this.rand.nextDouble() * this.height,
                        this.posZ + (this.rand.nextDouble() - 0.5D) * this.width,
                        this.rand.nextGaussian() * 0.02D, 0.015D + this.rand.nextDouble() * 0.02D,
                        this.rand.nextGaussian() * 0.02D
                );
                puff.setMaxAge(28 + this.rand.nextInt(16));
                puff.setRBGColorF(0.6823f, 0.2431f, 0.2431f);
                puff.setAlphaF(0f, 0.65f, 0.35f, 0f);
                puff.setGridSize(16);
                puff.setParticles(56 + this.rand.nextInt(4), 1, 1);
                float scale = 3f + this.rand.nextFloat() * 2f;
                puff.setScale(scale * 0.65f, scale, scale * 1.25f);
                puff.setLayer(0);
                puff.setRotationSpeed(this.rand.nextFloat(), (this.rand.nextFloat() - 0.5f) * 0.5f);
                puff.setSlowDown(0.98D);
                puff.setGravity(0f);
                ParticleEngine.addEffect(this.world, puff);
            }
        }

        //play sound
        this.world.playSound(
                null, this.posX, this.posY, this.posZ,
                SoundsTC.poof, SoundCategory.NEUTRAL, 1f, 1.15f
        );

        //continue
        super.setDead();
    }

    //---nbt overrides to ensure that upon restarting, the swarmers disappear---
    @Override
    public void readEntityFromNBT(@NotNull NBTTagCompound compound) {
        this.setDead();
    }

    @Override
    public void writeEntityToNBT(@NotNull NBTTagCompound compound) {}

    @Override
    public boolean writeToNBTOptional(@NotNull NBTTagCompound compound) {
        return false;
    }

    @Override
    public boolean writeToNBTAtomically(@NotNull NBTTagCompound compound) {
        return false;
    }

    //---anim stuff---
    @Override
    @NotNull
    public AnimationDataEntityLiving getAnimationData() {
        return this.animationData;
    }

    @Override
    public void initializeAnimationData(@NotNull AnimationDataEntityLiving animData) {
        animData.setScale(0.5f);
        animData.addAnimation("animation.squirrel.look");
        animData.addAnimationController(new AnimationController<SwarmerSquirrelEntity, AnimationDataEntityLiving>(
                this, "movementCont", "default",
                new AnimationControllerState<AnimationDataEntityLiving>("default", 0.1)
                        .addStateTransition("walk", AnimationDataEntityLiving::isMoving),
                new AnimationControllerState<AnimationDataEntityLiving>("walk", 0.1)
                        .addAnimation("animation.squirrel.walk")
                        .addStateTransition("default", data -> !data.isMoving())
        ));
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
