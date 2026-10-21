package anightdazingzoroark.squirrelwitchery.server.entity;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.controller.AnimationControllerState;
import anightdazingzoroark.riftlib.core.manager.AnimationDataEntityLiving;
import anightdazingzoroark.riftlib.inventory.RiftLibInventoryHandler;
import anightdazingzoroark.squirrelwitchery.server.ServerProxy;
import anightdazingzoroark.squirrelwitchery.server.inventory.MagicalSquirrelInventory;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import anightdazingzoroark.squirrelwitchery.server.message.MessageOpenMagicalSquirrelInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import thaumcraft.common.lib.SoundsTC;

import java.util.Set;

public class MagicalSquirrelEntity extends EntityTameable implements IAnimatable<AnimationDataEntityLiving> {
    private static final DataParameter<Boolean> TRANSFORMED = EntityDataManager.createKey(MagicalSquirrelEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Byte> BEHAVIOR = EntityDataManager.createKey(MagicalSquirrelEntity.class, DataSerializers.BYTE);

    @NonNull
    private final AnimationDataEntityLiving animationData = new AnimationDataEntityLiving(this);
    @NotNull
    private final MagicalSquirrelInventory inventoryHandler = new MagicalSquirrelInventory(this);

    //server only stuff
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
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(TRANSFORMED, false);
        this.dataManager.register(BEHAVIOR, (byte) 0);
    }

    @Override
    protected void initEntityAI() {
        this.aiTempt = new EntityAITempt(this, 1.2D, false, Set.of(SquirrelWitcheryItems.NUT, SquirrelWitcheryItems.BIG_NUT)) {
            @Override
            public boolean shouldExecute() {
                if (MagicalSquirrelEntity.this.isTamed()) return false;
                return super.shouldExecute();
            }
        };

        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this) {
            @Override
            public boolean shouldExecute() {
                if (!MagicalSquirrelEntity.this.getTransformed()) return false;
                return super.shouldExecute();
            }
        });
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this) {
            @Override
            public boolean shouldExecute() {
                if (!MagicalSquirrelEntity.this.getTransformed()) return false;
                return super.shouldExecute();
            }
        });

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
        this.tasks.addTask(2, this.aiTempt);
        this.tasks.addTask(3, new EntityAIAvoidEntity<>(this, EntityPlayer.class, player -> !this.isTamed(), 16f, 0.8D, 1.33D));
        this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0, true));
        this.tasks.addTask(5, new EntityAIFollowOwner(this, 1D, 10f, 2f) {
            @Override
            public boolean shouldExecute() {
                if (MagicalSquirrelEntity.this.getBehavior() != Behavior.FOLLOW) return false;
                return super.shouldExecute();
            }
        });
        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1D) {
            @Override
            public boolean shouldExecute() {
                if (MagicalSquirrelEntity.this.isTamed()) return false;
                return super.shouldExecute();
            }
        });
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    }

    @Override
    public void onLivingUpdate() {
        //arm swing update
        this.updateArmSwingProgress();

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

            //make sure it equps the last item in its
            if (this.isEntityAlive() && this.isTamed() && this.getTransformed()) {
                RiftLibInventoryHandler.ItemSearchResult result = this.inventoryHandler.findItem(
                        RiftLibInventoryHandler.ItemSearchDirection.LAST_TO_FIRST, Items.DIAMOND_SWORD //the diamond sword is temporary, will be made configurable
                );
                if (result.successful()) this.setHeldItem(EnumHand.MAIN_HAND, result.foundStack());
            }
        }
    }

    @Override
    public boolean processInteract(@NonNull EntityPlayer player, @NonNull EnumHand hand) {
        ItemStack heldItem = player.getHeldItem(hand);

        //taming process
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

        //open inventory
        if (this.isTamed() && this.getTransformed() && this.isOwner(player) && player.isSneaking() && heldItem.isEmpty()) {
            if (this.world.isRemote && hand == EnumHand.MAIN_HAND) {
                ServerProxy.MESSAGE_WRAPPER.sendToServer(new MessageOpenMagicalSquirrelInventory(this.getEntityId()));
            }
            return true;
        }

        //change behavior
        if (this.isTamed() && this.getTransformed() && heldItem.isEmpty()) {
            if (!this.world.isRemote) {
                int next = this.getBehavior().ordinal() + 1;
                if (next >= Behavior.values().length) next = 0;
                Behavior newBehavior = Behavior.values()[next];
                this.setBehavior(newBehavior);
                player.sendStatusMessage(new TextComponentTranslation("warning.squirrelwitchery.set_behavior", newBehavior.getTranslatedName()), false);
            }
            return true;
        }

        return super.processInteract(player, hand);
    }


    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean flag = entityIn.attackEntityFrom(
                DamageSource.causeMobDamage(this),
                (int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()
        );
        if (flag) this.applyEnchantments(this, entityIn);
        return flag;
    }

    @Override
    public void onDeath(@NonNull DamageSource cause) {
        super.onDeath(cause);
        if (!this.world.isRemote && this.dead) {
            this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            this.inventoryHandler.dropAllItems(this.world, this.getPosition());
        }
    }

    @Override
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
        //the held sword belongs to the inventory, which drops separately on death.
        this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        super.dropEquipment(wasRecentlyHit, lootingModifier);
    }

    public void setTransformed(boolean value) {
        this.dataManager.set(TRANSFORMED, value);
    }

    public boolean getTransformed() {
        return this.dataManager.get(TRANSFORMED);
    }

    public void setBehavior(@NotNull Behavior behavior) {
        this.dataManager.set(BEHAVIOR, (byte) behavior.ordinal());
    }

    public Behavior getBehavior() {
        return Behavior.values()[this.dataManager.get(BEHAVIOR)];
    }

    @NotNull
    public MagicalSquirrelInventory getInventoryHandler() {
        return this.inventoryHandler;
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
        compound.setByte("Behavior", (byte) this.getBehavior().ordinal());
        compound.setTag("Inventory", this.inventoryHandler.serializeNBT());
    }

    @Override
    public void readEntityFromNBT(@NonNull NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setTransformed(compound.getBoolean("Transformed"));
        this.setBehavior(Behavior.values()[compound.getByte("Behavior")]);
        if (compound.hasKey("Inventory", 10)) this.inventoryHandler.deserializeNBT(compound.getCompoundTag("Inventory"));
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

        // Use vanilla's synchronized swing, including its duration and frame interpolation.
        animationDataEntityLiving.addOnUpdateAnimationValue("variable.attack_progress", () -> {
            float partialTicks = 1f;
            if (this.world.isRemote) partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
            return (double) Math.clamp(this.getSwingProgress(partialTicks), 0f, 1f);
        });
        animationDataEntityLiving.addOnUpdateAnimationValue("variable.attack_body_yaw", () -> {
            float partialTicks = 1f;
            if (this.world.isRemote) partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
            double attackProgress = Math.clamp(this.getSwingProgress(partialTicks), 0f, 1f);
            return Math.toDegrees(Math.sin(Math.sqrt(attackProgress) * Math.PI * 2D) * 0.2D);
        });

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
        animationDataEntityLiving.addAnimation("animation.magical_squirrel.attack", data -> this.isTamed() && this.getTransformed());
    }

    //-----stuff i dont care about-----
    @Override
    @Nullable
    public EntityAgeable createChild(EntityAgeable ageable) {
        return null;
    }

    //-----other enums-----
    public enum Behavior {
        FOLLOW,
        SIT,
        WANDER;

        @NotNull
        public String getTranslatedName() {
            return I18n.format("behavior.squirrelwitchery."+this.name().toLowerCase()+".name");
        }
    }
}
