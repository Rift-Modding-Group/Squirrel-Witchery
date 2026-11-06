package anightdazingzoroark.squirrelwitchery.server.entity;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.controller.AnimationControllerState;
import anightdazingzoroark.riftlib.core.manager.AnimationDataEntityLiving;
import anightdazingzoroark.riftlib.inventory.RiftLibInventoryHandler;
import anightdazingzoroark.squirrelwitchery.SquirrelWitcheryConfig;
import anightdazingzoroark.squirrelwitchery.SquirrelWitcheryUtils;
import anightdazingzoroark.squirrelwitchery.server.ServerProxy;
import anightdazingzoroark.squirrelwitchery.server.inventory.MagicalSquirrelInventory;
import anightdazingzoroark.squirrelwitchery.server.items.NutsaberItem;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import anightdazingzoroark.squirrelwitchery.server.message.MessageOpenMagicalSquirrelInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
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
import java.util.concurrent.atomic.AtomicReference;

public class MagicalSquirrelEntity extends EntityTameable implements IAnimatable<AnimationDataEntityLiving>, IRangedAttackMob {
    private static final DataParameter<Boolean> TRANSFORMED = EntityDataManager.createKey(MagicalSquirrelEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Byte> BEHAVIOR = EntityDataManager.createKey(MagicalSquirrelEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Byte> WEAPON_TYPE = EntityDataManager.createKey(MagicalSquirrelEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Boolean> USING_BOW = EntityDataManager.createKey(MagicalSquirrelEntity.class, DataSerializers.BOOLEAN);
    private static final float SHOT_RANGE = 16f;

    @NonNull
    private final AnimationDataEntityLiving animationData = new AnimationDataEntityLiving(this);
    @NotNull
    private final MagicalSquirrelInventory inventoryHandler = new MagicalSquirrelInventory(this);

    //server only stuff
    @Nullable
    private EntityAITempt aiTempt;
    private int transformTicks;
    private int eatingCooldown;

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
        this.dataManager.register(WEAPON_TYPE, (byte) -1);
        this.dataManager.register(USING_BOW, false);
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
                if (!MagicalSquirrelEntity.this.getTransformed() || MagicalSquirrelEntity.this.getBehavior() == Behavior.SIT) return false;
                return super.shouldExecute();
            }
        });
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this) {
            @Override
            public boolean shouldExecute() {
                if (!MagicalSquirrelEntity.this.getTransformed() || MagicalSquirrelEntity.this.getBehavior() == Behavior.SIT) return false;
                return super.shouldExecute();
            }
        });
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true) {
            @Override
            public boolean shouldExecute() {
                if (!MagicalSquirrelEntity.this.getTransformed() || MagicalSquirrelEntity.this.getBehavior() == Behavior.SIT) return false;
                return super.shouldExecute();
            }
        });

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.25D) {
            @Override
            public boolean shouldExecute() {
                if (MagicalSquirrelEntity.this.isTamed()) return false;
                return super.shouldExecute();
            }
        });
        this.tasks.addTask(2, this.aiTempt);
        this.tasks.addTask(3, new EntityAIAvoidEntity<>(this, EntityPlayer.class, player -> !this.isTamed(), 16f, 0.8D, 1.33D));
        this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0, true) {
            @Override
            public boolean shouldExecute() {
                if (MagicalSquirrelEntity.this.getBehavior() == Behavior.SIT) return false;
                if (MagicalSquirrelEntity.this.getHeldWeaponType() != WeaponType.MELEE) return false;
                return super.shouldExecute();
            }

            @Override
            public boolean shouldContinueExecuting() {
                if (MagicalSquirrelEntity.this.getBehavior() == Behavior.SIT) return false;
                if (MagicalSquirrelEntity.this.getHeldWeaponType() != WeaponType.MELEE) return false;
                return super.shouldExecute();
            }
        });
        this.tasks.addTask(2, new EntityAIAttackRanged(this, 1D, 60, SHOT_RANGE) {
            @Override
            public boolean shouldExecute() {
                if (MagicalSquirrelEntity.this.getBehavior() == Behavior.SIT) return false;
                if (MagicalSquirrelEntity.this.getHeldWeaponType() != WeaponType.BOW) return false;
                return MagicalSquirrelEntity.this.findBowAmmunition().successful() && super.shouldExecute();
            }

            @Override
            public void startExecuting() {
                super.startExecuting();
                MagicalSquirrelEntity.this.setUsingBow(true);
            }

            @Override
            public void resetTask() {
                super.resetTask();
                MagicalSquirrelEntity.this.setUsingBow(false);
                MagicalSquirrelEntity.this.resetActiveHand();
            }

            @Override
            public void updateTask() {
                if (this.rangedAttackTime > 0 && this.rangedAttackTime <= 21) {
                    MagicalSquirrelEntity.this.setActiveHand(EnumHand.MAIN_HAND);
                }
                else MagicalSquirrelEntity.this.resetActiveHand();
                super.updateTask();
            }
        });
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
            if (this.eatingCooldown > 0) this.eatingCooldown--;

            //eat from inventory
            if (this.isEntityAlive() && this.isTamed() && this.getHealth() < this.getMaxHealth() && this.eatingCooldown == 0) {
                RiftLibInventoryHandler.ItemSearchResult food = this.inventoryHandler.findItem(
                        RiftLibInventoryHandler.ItemSearchDirection.LAST_TO_FIRST,
                        itemStack -> {
                            return itemStack.getItem() == SquirrelWitcheryItems.NUT || itemStack.getItem() == SquirrelWitcheryItems.BIG_NUT;
                        }
                );
                if (food.successful()) {
                    ItemStack eatenNut = this.inventoryHandler.extractItem(food.slot(), 1, false);
                    if (!eatenNut.isEmpty()) this.healFromNut(eatenNut);
                }
            }

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

            //equip the last usable inventory item, or clear the hand when none remains.
            ItemStack heldItem = ItemStack.EMPTY;
            AtomicReference<WeaponType> pendingWeaponType = new AtomicReference<>();
            if (this.isEntityAlive() && this.isTamed() && this.getTransformed()) {
                RiftLibInventoryHandler.ItemSearchResult result = this.inventoryHandler.findItem(
                        RiftLibInventoryHandler.ItemSearchDirection.LAST_TO_FIRST,
                        itemStack -> {
                            if (itemStack.isEmpty()) return false;

                            //usable melee weapons
                            for (String entryId : SquirrelWitcheryConfig.magicalSquirrelBehavior.usableMeleeWeapons) {
                                if (!(SquirrelWitcheryUtils.itemStackMatchesString(itemStack, entryId))) continue;
                                pendingWeaponType.set(WeaponType.MELEE);
                                return true;
                            }

                            //bows
                            if (SquirrelWitcheryConfig.magicalSquirrelBehavior.canUseBows && itemStack.getItem() instanceof ItemBow) {
                                pendingWeaponType.set(WeaponType.BOW);
                                return true;
                            }

                            return false;
                        }
                );
                heldItem = result.foundStack();
            }
            if (this.isHandActive() && this.getActiveItemStack() != heldItem) this.resetActiveHand();
            this.setHeldItem(EnumHand.MAIN_HAND, heldItem);
            this.setHeldWeaponType(pendingWeaponType.get());

            //equip last usable armor slots
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                if (slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) continue;
                ItemStack armor = ItemStack.EMPTY;
                if (this.isEntityAlive() && this.isTamed()) {
                    armor = this.inventoryHandler.findItem(
                            RiftLibInventoryHandler.ItemSearchDirection.LAST_TO_FIRST,
                            stack -> !stack.isEmpty() && stack.getItem() instanceof ItemArmor item && item.getEquipmentSlot() == slot
                    ).foundStack();
                }
                this.setItemStackToSlot(slot, armor);
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

        //feed tamed magical squirrels for healing
        if (this.isTamed() && this.isEntityAlive() && this.getHealth() < this.getMaxHealth()
                && (heldItem.getItem() == SquirrelWitcheryItems.NUT || heldItem.getItem() == SquirrelWitcheryItems.BIG_NUT)
        ) {
            if (!this.world.isRemote) {
                this.healFromNut(heldItem);
                if (!player.capabilities.isCreativeMode) heldItem.shrink(1);
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

    private void healFromNut(@NotNull ItemStack nut) {
        float healAmount = nut.getItem() == SquirrelWitcheryItems.BIG_NUT ? 4f : 2f;
        this.heal(healAmount);
        this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1f, 1f);
        this.eatingCooldown = 40;
    }

    @Override
    public boolean attackEntityAsMob(@NonNull Entity entityIn) {
        ItemStack weapon = this.getHeldItemMainhand();
        float attackDamage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        // The stack's normal attribute modifier requires fuel; squirrels power the saber themselves.
        if (weapon.getItem() instanceof NutsaberItem saber && saber.getRisuniumAmount(weapon) < NutsaberItem.RISUNIUM_PER_SWING) {
            attackDamage += NutsaberItem.ATTACK_DAMAGE;
        }
        boolean flag = entityIn.attackEntityFrom(
                DamageSource.causeMobDamage(this),
                (int) attackDamage
        );
        if (flag) {
            this.applyEnchantments(this, entityIn);
            if (!this.world.isRemote && !weapon.isEmpty() && entityIn instanceof EntityLivingBase target) {
                weapon.getItem().hitEntity(weapon, target, this);
                if (weapon.isEmpty()) this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
        return flag;
    }

    @Override
    protected void damageArmor(float damage) {
        if (damage <= 0f) return;
        int durabilityDamage = Math.max(1, (int) (damage / 4f));
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) continue;
            ItemStack armor = this.getItemStackFromSlot(slot);
            if (armor.isEmpty() || !(armor.getItem() instanceof ItemArmor)) continue;
            armor.damageItem(durabilityDamage, this);
            if (armor.isEmpty()) this.setItemStackToSlot(slot, ItemStack.EMPTY);
        }
    }

    @Override
    public void onDeath(@NonNull DamageSource cause) {
        super.onDeath(cause);
        if (!this.world.isRemote && this.dead) {
            this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                if (slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) this.setItemStackToSlot(slot, ItemStack.EMPTY);
            }
            this.inventoryHandler.dropAllItems(this.world, this.getPosition());
        }
    }

    @Override
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
        //equipped items belong to the inventory, which drops separately on death
        this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) this.setItemStackToSlot(slot, ItemStack.EMPTY);
        }
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

    private void setHeldWeaponType(@Nullable WeaponType weaponType) {
        if (weaponType != null) this.dataManager.set(WEAPON_TYPE, (byte) weaponType.ordinal());
        else this.dataManager.set(WEAPON_TYPE, (byte) -1);
    }

    @Nullable
    private WeaponType getHeldWeaponType() {
        byte heldWeaponOrdinal = this.dataManager.get(WEAPON_TYPE);
        return heldWeaponOrdinal >= 0 ? WeaponType.values()[heldWeaponOrdinal] : null;
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

        WeaponType weaponType = this.getHeldWeaponType();
        compound.setByte("WeaponType", weaponType != null ? (byte) weaponType.ordinal() : (byte) -1);
    }

    @Override
    public void readEntityFromNBT(@NonNull NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setTransformed(compound.getBoolean("Transformed"));
        this.setBehavior(Behavior.values()[compound.getByte("Behavior")]);
        if (compound.hasKey("Inventory", 10)) this.inventoryHandler.deserializeNBT(compound.getCompoundTag("Inventory"));

        byte weaponTypeOrdinal = compound.getByte("WeaponType");
        this.setHeldWeaponType(weaponTypeOrdinal >= 0 ? WeaponType.values()[weaponTypeOrdinal] : null);

        this.setNoAI(this.isTamed() && !this.getTransformed());
    }

    //-----ranged attack stuff, for bows-----
    @NotNull
    private RiftLibInventoryHandler.ItemSearchResult findBowAmmunition() {
        return this.inventoryHandler.findItem(
                RiftLibInventoryHandler.ItemSearchDirection.LAST_TO_FIRST,
                stack -> !stack.isEmpty() && this.getHeldItemMainhand().getItem() instanceof ItemBow bow && bow.isArrow(stack)
        );
    }

    @Override
    public void attackEntityWithRangedAttack(@NonNull EntityLivingBase target, float distanceFactor) {
        if (this.world.isRemote || !this.isEntityAlive() || !target.isEntityAlive()) return;
        if (target != this.getAttackTarget() || this.getDistanceSq(target) > SHOT_RANGE * SHOT_RANGE) return;
        if (!this.getEntitySenses().canSee(target)) return;

        ItemStack weapon = this.getHeldItemMainhand();
        if (!(weapon.getItem() instanceof ItemBow bow) || this.getItemInUseMaxCount() < 20) return;

        RiftLibInventoryHandler.ItemSearchResult ammunition = this.findBowAmmunition();
        if (!ammunition.successful()) return;

        ItemStack arrowStack = ammunition.foundStack();
        if (!(arrowStack.getItem() instanceof ItemArrow arrowItem)) return;

        //---create arrow that will be fired---
        EntityArrow arrow = bow.customizeArrow(arrowItem.createArrow(this.world, arrowStack, this));
        double deltaX = target.posX - arrow.posX;
        double deltaZ = target.posZ - arrow.posZ;
        double deltaY = target.getEntityBoundingBox().minY + target.height / 3D - arrow.posY;
        double flightTicks = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) / 3D;
        arrow.shoot(deltaX, deltaY + 0.025D * flightTicks * flightTicks, deltaZ, 3f, 1f);

        //apply enchantments
        int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, weapon);
        if (power > 0) arrow.setDamage(arrow.getDamage() + power * 0.5D + 0.5D);
        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, weapon);
        if (punch > 0) arrow.setKnockbackStrength(punch);
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, weapon) > 0) arrow.setFire(100);
        boolean infinite = arrowStack.getItem() == Items.ARROW && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, weapon) > 0;
        arrow.pickupStatus = infinite ? EntityArrow.PickupStatus.CREATIVE_ONLY : EntityArrow.PickupStatus.ALLOWED;

        if (!this.world.spawnEntity(arrow)) return;

        //---bow shot after effects---
        if (!infinite) this.inventoryHandler.extractItem(ammunition.slot(), 1, false);
        weapon.damageItem(1, this);
        this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1f, 1f / (this.rand.nextFloat() * 0.4f + 1.2f) + 0.5f);
        this.resetActiveHand();
    }

    private void setUsingBow(boolean swingingArms) {
        this.dataManager.set(USING_BOW, swingingArms);
    }

    private boolean isUsingBow() {
        return this.dataManager.get(USING_BOW);
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

        //variables for melee attack anims
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

        //for bow attack anims
        animationDataEntityLiving.addOnUpdateAnimationValue("variable.using_bow", () -> this.isUsingBow() ? 1D : 0D);

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
        animationDataEntityLiving.addAnimation("animation.magical_squirrel.attack", data -> {
            return this.isTamed() && this.getTransformed();
        });
        animationDataEntityLiving.addAnimation("animation.magical_squirrel.bow", data -> {
            return this.isTamed() && this.getTransformed() && this.isUsingBow();
        });
    }

    //-----stuff i dont care about-----
    @Override
    @Nullable
    public EntityAgeable createChild(EntityAgeable ageable) {
        return null;
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {}

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

    private enum WeaponType {
        MELEE,
        BOW;
    }
}