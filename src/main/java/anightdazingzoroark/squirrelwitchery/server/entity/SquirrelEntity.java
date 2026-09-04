package anightdazingzoroark.squirrelwitchery.server.entity;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.controller.AnimationControllerState;
import anightdazingzoroark.riftlib.core.manager.AnimationDataEntity;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import anightdazingzoroark.squirrelwitchery.server.sounds.SquirrelWitcherySounds;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class SquirrelEntity extends EntityAnimal implements IAnimatable<AnimationDataEntity>, IShearable {
    @NotNull
    private final AnimationDataEntity animData = new AnimationDataEntity(this, entity -> entity.isChild() ? 0.25f : 0.5f);
    private static final DataParameter<Boolean> SHEARED = EntityDataManager.createKey(SquirrelEntity.class, DataSerializers.BOOLEAN);

    //server only
    private int shearCountdown;

    public SquirrelEntity(World worldIn) {
        super(worldIn);
        this.setSize(0.5f, 0.5f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SHEARED, false);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
        this.tasks.addTask(3, new EntityAIMate(this, 1D));
        this.tasks.addTask(4, new EntityAITempt(this, 1.2D, false, Set.of(SquirrelWitcheryItems.NUT, SquirrelWitcheryItems.BIG_NUT)));
        this.tasks.addTask(5, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.world.isRemote) {
            if (this.isSheared()) {
                if (this.shearCountdown > 0) this.shearCountdown--;
                else this.setSheared(false);
            }
        }
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        return new SquirrelEntity(this.world);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == SquirrelWitcheryItems.NUT || stack.getItem() == SquirrelWitcheryItems.BIG_NUT;
    }

    //---shearing stuff---
    public boolean isSheared() {
        return this.dataManager.get(SHEARED);
    }

    private void setSheared(boolean value) {
        this.dataManager.set(SHEARED, value);
    }

    @Override
    public boolean isShearable(@NonNull ItemStack itemStack, IBlockAccess iBlockAccess, BlockPos blockPos) {
        return !this.isSheared();
    }

    @Override
    @NonNull
    public List<ItemStack> onSheared(@NonNull ItemStack itemStack, IBlockAccess iBlockAccess, BlockPos blockPos, int i) {
        this.setSheared(true);
        this.shearCountdown = 24000; //should correspond to 1 in-game day
        return List.of(new ItemStack(SquirrelWitcheryItems.SQUIRREL_FUR, this.world.rand.nextInt(1, 4)));
    }

    //---nbt stuff---
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Sheared", this.isSheared());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("Sheared")) this.setSheared(compound.getBoolean("Sheared"));
    }

    //---anim stuff---
    @Override
    public AnimationDataEntity getAnimationData() {
        return this.animData;
    }

    @Override
    public void initializeAnimationData(AnimationDataEntity animationData) {
        animationData.addAnimationController(new AnimationController<SquirrelEntity, AnimationDataEntity>(
                this, "movementCont", "default",
                new AnimationControllerState<AnimationDataEntity>("default", 0.1)
                        .addStateTransition("walk", AnimationDataEntity::isMoving),
                new AnimationControllerState<AnimationDataEntity>("walk", 0.1)
                        .addAnimation("animation.squirrel.walk")
                        .addStateTransition("default", data -> !data.isMoving())
        ));
        animationData.addAnimationController(new AnimationController<SquirrelEntity, AnimationDataEntity>(
                this, "shearedCont", "default",
                new AnimationControllerState<AnimationDataEntity>("default")
                        .addStateTransition("sheared", data -> this.isSheared()),
                new AnimationControllerState<AnimationDataEntity>("sheared")
                        .addAnimation("animation.squirrel.sheared")
                        .addStateTransition("default", data -> !this.isSheared())
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
