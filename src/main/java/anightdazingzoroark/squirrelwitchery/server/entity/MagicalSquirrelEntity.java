package anightdazingzoroark.squirrelwitchery.server.entity;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.controller.AnimationControllerState;
import anightdazingzoroark.riftlib.core.manager.AnimationDataEntityLiving;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import thaumcraft.common.lib.SoundsTC;

import java.util.Set;

public class MagicalSquirrelEntity extends EntityTameable implements IAnimatable<AnimationDataEntityLiving> {
    @NonNull
    private final AnimationDataEntityLiving animationData = new AnimationDataEntityLiving(this);
    private static final DataParameter<Boolean> TRANSFORMED = EntityDataManager.createKey(MagicalSquirrelEntity.class, DataSerializers.BOOLEAN);
    @Nullable
    private EntityAITempt aiTempt;
    private int transformTicks;

    public MagicalSquirrelEntity(World worldIn) {
        super(worldIn);
        this.setSize(0.5f, 0.5f);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(TRANSFORMED, false);
    }

    @Override
    protected void initEntityAI() {
        this.aiTempt = new EntityAITempt(this, 1.2D, false, Set.of(SquirrelWitcheryItems.NUT, SquirrelWitcheryItems.BIG_NUT));
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
        this.tasks.addTask(2, this.aiTempt);
        this.tasks.addTask(3, new EntityAIAvoidEntity<>(this, EntityPlayer.class, player -> !this.isTamed(), 16f, 0.8D, 1.33D));
        this.tasks.addTask(4, new EntityAIWanderAvoidWater(this, 1D));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 6f));
        this.tasks.addTask(6, new EntityAILookIdle(this));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.world.isRemote) {
            //transform
            if (this.isTamed() && !this.getTransformed()) {
                this.getNavigator().clearPath();
                this.motionX = 0;
                this.motionZ = 0;
                this.transformTicks++;

                if (this.transformTicks >= 100) {
                    this.setTransformed(true);
                    this.setNoAI(false);
                    this.transformTicks = 0;
                    this.world.playSound(
                            null, this.posX, this.posY, this.posZ,
                            SoundsTC.wand, SoundCategory.NEUTRAL, 1f, 1.15f
                    );
                }
            }
        }
    }

    @Override
    public boolean processInteract(@NonNull EntityPlayer player, @NonNull EnumHand hand) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (!this.isTamed() && (heldItem.getItem() == SquirrelWitcheryItems.NUT || heldItem.getItem() == SquirrelWitcheryItems.BIG_NUT)) {
            if (!this.world.isRemote) {
                //only accept food while tempted, never while avoiding a player or panicking
                if (this.aiTempt == null || !this.aiTempt.isRunning() || ForgeEventFactory.onAnimalTame(this, player)) return true;

                this.setTamedBy(player);
                this.setNoAI(true);
                this.getNavigator().clearPath();
                if (!player.capabilities.isCreativeMode) heldItem.shrink(1);
                this.world.setEntityState(this, (byte) 7);
            }
            return true;
        }
        return super.processInteract(player, hand);
    }

    public void setTransformed(boolean value) {
        this.dataManager.set(TRANSFORMED, value);
    }

    public boolean getTransformed() {
        return this.dataManager.get(TRANSFORMED);
    }

    @Override
    public void notifyDataManagerChange(@NonNull DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (key.equals(TRANSFORMED)) {
            //change size on server and client upon finishing transformation
            if (this.getTransformed()) this.setSize(0.6f, 1.8f);
            else this.setSize(0.5f, 0.5f);
            this.setScaleForAge(false);
        }
    }

    @Override
    public void writeEntityToNBT(@NonNull NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Transformed", this.getTransformed());
    }

    @Override
    public void readEntityFromNBT(@NonNull NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setTransformed(compound.getBoolean("Transformed"));
        this.setNoAI(this.isTamed() && !this.getTransformed());
    }

    //-----animation stuff-----
    @Override
    @NonNull
    public AnimationDataEntityLiving getAnimationData() {
        return this.animationData;
    }

    @Override
    public void initializeAnimationData(@NonNull AnimationDataEntityLiving animationDataEntityLiving) {
        animationDataEntityLiving.setScale(entity -> this.getTransformed() ? 1f : 0.5f);

        //normal form anims
        animationDataEntityLiving.addAnimation("animation.squirrel.look", data -> !this.getTransformed());
        animationDataEntityLiving.addAnimationController(new AnimationController<MagicalSquirrelEntity, AnimationDataEntityLiving>(
                this, "movementCont", "default",
                new AnimationControllerState<AnimationDataEntityLiving>("default", 0.1)
                        .addStateTransition("walk", data -> data.isMoving() && !this.getTransformed()),
                new AnimationControllerState<AnimationDataEntityLiving>("walk", 0.1)
                        .addAnimation("animation.squirrel.walk")
                        .addStateTransition("default", data -> !data.isMoving() || this.getTransformed())
        ));
        animationDataEntityLiving.addAnimationController(new AnimationController<MagicalSquirrelEntity, AnimationDataEntityLiving>(
                this, "transformationCont", "default",
                new AnimationControllerState<AnimationDataEntityLiving>("default")
                        .addStateTransition("transform", data -> this.isTamed() && !this.getTransformed()),
                new AnimationControllerState<AnimationDataEntityLiving>("transform")
                        .addAnimation("animation.magical_squirrel.transform")
                        .addStateTransition("default", data -> this.getTransformed())
        ));

        //transformed form anims
        animationDataEntityLiving.addAnimation("animation.magical_squirrel.look", data -> this.getTransformed());
        animationDataEntityLiving.addAnimation("animation.magical_squirrel.walk", data -> this.getTransformed());
    }

    //-----stuff i dont care about-----
    @Override
    @Nullable
    public EntityAgeable createChild(EntityAgeable ageable) {
        return null;
    }
}
