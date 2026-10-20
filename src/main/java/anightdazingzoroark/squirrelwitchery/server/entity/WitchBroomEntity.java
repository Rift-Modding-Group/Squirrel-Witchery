package anightdazingzoroark.squirrelwitchery.server.entity;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.manager.AnimationDataEntityNonLiving;
import anightdazingzoroark.squirrelwitchery.client.SquirrelWitcheryControls;
import anightdazingzoroark.squirrelwitchery.server.ServerProxy;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import anightdazingzoroark.squirrelwitchery.server.message.MessageControlWitchBroom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

public class WitchBroomEntity extends Entity implements IAnimatable<AnimationDataEntityNonLiving> {
    @NotNull
    private final AnimationDataEntityNonLiving animData = new AnimationDataEntityNonLiving(this);
    private static final DataParameter<Boolean> GOING_UP = EntityDataManager.createKey(WitchBroomEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> GOING_DOWN = EntityDataManager.createKey(WitchBroomEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> RISUNIUM = EntityDataManager.createKey(WitchBroomEntity.class, DataSerializers.VARINT);
    public static final int MAX_RISUNIUM = 250;

    private int interpolationSteps;
    private double targetX;
    private double targetY;
    private double targetZ;
    private float targetYaw;
    private float targetPitch;

    public WitchBroomEntity(@NotNull World worldIn) {
        super(worldIn);
        this.setSize(1f, 1f);
        this.stepHeight = 0.6f;
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(GOING_UP, false);
        this.dataManager.register(GOING_DOWN, false);
        this.dataManager.register(RISUNIUM, 0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        //living movement and network interpolation
        if (this.world.isRemote && !this.canPassengerSteer()) {
            if (this.interpolationSteps > 0) {
                this.setPosition(
                        this.posX + (this.targetX - this.posX) / this.interpolationSteps,
                        this.posY + (this.targetY - this.posY) / this.interpolationSteps,
                        this.posZ + (this.targetZ - this.posZ) / this.interpolationSteps
                );
                this.setRotation(
                        this.rotationYaw + MathHelper.wrapDegrees(this.targetYaw - this.rotationYaw) / this.interpolationSteps,
                        this.rotationPitch + (this.targetPitch - this.rotationPitch) / this.interpolationSteps
                );
                this.interpolationSteps--;
            }
        }
        else if (this.getControllingPassenger() instanceof EntityPlayer controller) {
            this.interpolationSteps = 0;
            this.setRotation(controller.rotationYaw, controller.rotationPitch * 0.5f);
            double yaw = Math.toRadians(this.rotationYaw);

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX = -Math.sin(yaw) * 0.5D;
            this.motionY = this.isGoingUp() ? 0.15D : this.isGoingDown() ? -0.15D : 0.0D;
            this.motionZ = Math.cos(yaw) * 0.5D;
            this.fallDistance = 0f;
        }
        else {
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            if (!this.hasNoGravity()) this.motionY -= 0.08D;
            this.motionX *= 0.91D;
            this.motionY *= 0.98D;
            this.motionZ *= 0.91D;
        }

        //player controls
        if (this.world.isRemote) {
            if (!(this.getControllingPassenger() instanceof EntityPlayer controller)) return;

            Minecraft minecraft = Minecraft.getMinecraft();
            if (minecraft.player == null || !minecraft.player.equals(controller)) return;

            boolean goingUp = SquirrelWitcheryControls.broomUp.isKeyDown() && !SquirrelWitcheryControls.broomDown.isKeyDown();
            boolean goingDown = !SquirrelWitcheryControls.broomUp.isKeyDown() && SquirrelWitcheryControls.broomDown.isKeyDown();

            if (goingUp) ServerProxy.MESSAGE_WRAPPER.sendToServer(new MessageControlWitchBroom(this, FlightState.UP));
            else if (goingDown) ServerProxy.MESSAGE_WRAPPER.sendToServer(new MessageControlWitchBroom(this, FlightState.DOWN));
            else ServerProxy.MESSAGE_WRAPPER.sendToServer(new MessageControlWitchBroom(this, FlightState.NONE));
        }
        //other server-only stuff
        else {
            //count down risunium use when being ridden (as good as every second)
            if (this.ticksExisted % 20 == 0 && this.ticksExisted > 0 && this.isBeingRidden()) {
                this.setRisuniumAmount(this.getRisuniumAmount() - 1);
            }

            //convert to item upon runnin out of risunium
            if (this.getRisuniumAmount() == 0 && !this.isDead) this.convertToItem();

            //convert to item upon colliding
            if (this.collidedHorizontally && !this.isDead) this.convertToItem();
        }
    }

    @Override
    public void writeEntityToNBT(@NotNull NBTTagCompound compound) {
        compound.setInteger("Risunium", this.getRisuniumAmount());
    }

    @Override
    public void readEntityFromNBT(@NotNull NBTTagCompound compound) {
        this.setRisuniumAmount(compound.getInteger("Risunium"));
    }

    @Override
    public boolean attackEntityFrom(@NotNull DamageSource source, float amount) {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public boolean canBePushed() {
        return !this.isDead;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(
            double x, double y, double z, float yaw, float pitch,
            int posRotationIncrements, boolean teleport
    ) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
        this.targetYaw = yaw;
        this.targetPitch = pitch;
        this.interpolationSteps = Math.max(1, posRotationIncrements);
    }

    //server only, helper for converting back into item
    public void convertToItem() {
        if (this.world.isRemote || this.isDead) return;

        //create item
        ItemStack stack = new ItemStack(SquirrelWitcheryItems.WITCH_BROOM);
        SquirrelWitcheryItems.WITCH_BROOM.setRisuniumAmount(stack, this.getRisuniumAmount());
        EntityItem entityItem = new EntityItem(this.world, this.posX, this.posY, this.posZ, stack);
        this.world.spawnEntity(entityItem);

        //despawn
        this.setDead();
    }

    //---ride management---
    //(note that because this entity is pretty simple its not a IDynamicRideUser)
    @Override
    public void updatePassenger(@NotNull Entity passenger) {
        if (!this.isPassenger(passenger)) return;

        this.rotationYaw = passenger.rotationYaw;
        this.prevRotationYaw = this.rotationYaw;
        this.rotationPitch = passenger.rotationPitch * 0.5f;
        this.setRotation(this.rotationYaw, this.rotationPitch);

        passenger.setPosition(this.posX, this.posY - 0.175D, this.posZ);
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof EntityPlayer) return passenger;
        }
        return null;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {}

    @Override
    protected void updateFallState(double y, boolean onGroundIn, @NotNull IBlockState state, @NotNull BlockPos pos) {}

    public boolean isGoingUp() {
        return this.dataManager.get(GOING_UP);
    }

    public void setGoingUp(boolean value) {
        this.dataManager.set(GOING_UP, value);
    }

    public boolean isGoingDown() {
        return this.dataManager.get(GOING_DOWN);
    }

    public void setGoingDown(boolean value) {
        this.dataManager.set(GOING_DOWN, value);
    }

    //---risunium management---
    public int getRisuniumAmount() {
        return this.dataManager.get(RISUNIUM);
    }

    public void setRisuniumAmount(int value) {
        this.dataManager.set(RISUNIUM, Math.clamp(value, 0, MAX_RISUNIUM));
    }

    //---anim stuff---
    @Override
    @NonNull
    public AnimationDataEntityNonLiving getAnimationData() {
        return this.animData;
    }

    @Override
    public void initializeAnimationData(@NonNull AnimationDataEntityNonLiving animationData) {
        animationData.setScale(1.25f);
    }

    //---enum for flight---
    public enum FlightState {
        NONE,
        UP,
        DOWN;
    }
}
