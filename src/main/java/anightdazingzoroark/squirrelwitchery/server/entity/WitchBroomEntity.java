package anightdazingzoroark.squirrelwitchery.server.entity;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.manager.AnimationDataEntity;
import anightdazingzoroark.squirrelwitchery.client.SquirrelWitcheryControls;
import anightdazingzoroark.squirrelwitchery.server.ServerProxy;
import anightdazingzoroark.squirrelwitchery.server.message.MessageControlWitchBroom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class WitchBroomEntity extends EntityLiving implements IAnimatable<AnimationDataEntity> {
    @NotNull
    private final AnimationDataEntity animData = new AnimationDataEntity(this, entity -> 1.25f);
    private static final DataParameter<Boolean> GOING_UP = EntityDataManager.createKey(WitchBroomEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> GOING_DOWN = EntityDataManager.createKey(WitchBroomEntity.class, DataSerializers.BOOLEAN);

    public WitchBroomEntity(World worldIn) {
        super(worldIn);
        this.setSize(1f, 1f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(GOING_UP, false);
        this.dataManager.register(GOING_DOWN, false);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

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
    }

    //temporary, will be replaced w an item automountin to this entity
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (!player.isRiding()) {
            this.getNavigator().clearPath();
            player.startRiding(this, true);
        }
        return super.processInteract(player, hand);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return super.attackEntityFrom(source, amount);
    }

    //---ride management---
    //(note that because this entity is pretty simple its not a IDynamicRideUser)
    @Override
    public void updatePassenger(Entity passenger) {
        if (!this.isPassenger(passenger)) return;

        this.rotationYaw = passenger.rotationYaw;
        this.prevRotationYaw = this.rotationYaw;
        this.rotationPitch = passenger.rotationPitch * 0.5f;
        this.setRotation(this.rotationYaw, this.rotationPitch);
        this.renderYawOffset = this.rotationYaw;

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
    public void travel(float strafe, float vertical, float forward) {
        if (this.isBeingRidden() && this.getControllingPassenger() instanceof EntityPlayer controller) {
            this.rotationYaw = controller.rotationYaw;
            this.prevRotationYaw = this.rotationYaw;
            this.rotationPitch = controller.rotationPitch * 0.5f;

            this.renderYawOffset = this.rotationYaw;
            this.rotationYawHead = this.rotationYaw;

            double speed = this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
            double yaw = Math.toRadians(this.rotationYaw);

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX = -Math.sin(yaw) * speed;
            this.motionY = this.isGoingUp() ? 0.15D : this.isGoingDown() ? -0.15D : 0.0D;
            this.motionZ =  Math.cos(yaw) * speed;

            this.fallDistance = 0f;
        }
        else super.travel(strafe, vertical, forward);
    }

    @Override
    public boolean canBeSteered() {
        return true;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {}

    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {}

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

    //---anim stuff---
    @Override
    public AnimationDataEntity getAnimationData() {
        return this.animData;
    }

    @Override
    public void initializeAnimationData(AnimationDataEntity animationDataEntity) {}

    //---stuff idc about---
    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return List.of();
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {}

    @Override
    public EnumHandSide getPrimaryHand() {
        return null;
    }

    //---enum for flight---
    public enum FlightState {
        NONE,
        UP,
        DOWN;
    }
}
