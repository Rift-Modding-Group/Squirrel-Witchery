package anightdazingzoroark.squirrelwitchery.server.entity;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.controller.AnimationControllerState;
import anightdazingzoroark.riftlib.core.manager.AnimationDataEntity;
import anightdazingzoroark.squirrelwitchery.SquirrelWitcheryUtils;
import anightdazingzoroark.squirrelwitchery.server.SquirrelWitcheryResearch;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import anightdazingzoroark.squirrelwitchery.server.sounds.SquirrelWitcherySounds;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.SoundsTC;

import java.util.List;
import java.util.Set;

public class SquirrelEntity extends EntityAnimal implements IAnimatable<AnimationDataEntity>, IShearable {
    @NotNull
    private final AnimationDataEntity animData = new AnimationDataEntity(this);
    private static final DataParameter<Boolean> SHEARED = EntityDataManager.createKey(SquirrelEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> PERFORM_RITUAL = EntityDataManager.createKey(SquirrelEntity.class, DataSerializers.BOOLEAN);
    private static final int RITUAL_DURATION = 100; //as good as 5 seconds
    private static final float RITUAL_VIS_PER_TICK = 0.1f;

    //server only
    private int shearCountdown;
    private int ritualTicksRemaining;
    private int ritualCooldown;

    public SquirrelEntity(World worldIn) {
        super(worldIn);
        this.setSize(0.5f, 0.5f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SHEARED, false);
        this.dataManager.register(PERFORM_RITUAL, false);
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

            if (this.isPerformingRitual()) {
                this.getNavigator().clearPath();
                this.motionX = 0;
                this.motionZ = 0;
                AuraHelper.drainVis(this.world, this.getPosition(), RITUAL_VIS_PER_TICK, false);
                this.ritualTicksRemaining--;
                if (this.ritualTicksRemaining <= 0) {
                    this.entityDropItem(new ItemStack(SquirrelWitcheryItems.CRYSTALLIZED_SQUIRREL_HEART), 0.1f);
                    this.world.playSound(
                            null, this.posX, this.posY, this.posZ,
                            SoundsTC.wand, SoundCategory.NEUTRAL, 1f, 1.15f
                    );
                    this.setIsPerformingRitual(false);
                    this.ritualCooldown = 24000; //should correspond to 1 in-game day
                }
            }

            if (this.ritualCooldown > 0) this.ritualCooldown--;
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack heldItem = player.getHeldItem(hand);
        //if given a risunium crystal, perform a ritual
        if (SquirrelWitcheryUtils.isRisuniumCrystal(heldItem)) {
            if (!this.world.isRemote) {
                //block if player is not serverPlayer instance or if its a baby
                if (!(player instanceof EntityPlayerMP serverPlayer) || this.growingAge < 0) return true;

                //block if no knowledge or if research tab doesn't exist yet
                IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(serverPlayer);
                if (knowledge == null || !knowledge.isResearchKnown(SquirrelWitcheryResearch.CRYSTALLIZED_SQUIRREL_HEART + "@1")) return true;

                //block ritual if its bein performed and when coolin down
                if (this.isPerformingRitual() || this.ritualCooldown > 0) return true;

                //block ritual due to lack of vis
                float requiredVis = RITUAL_DURATION * RITUAL_VIS_PER_TICK;
                if (AuraHelper.drainVis(this.world, this.getPosition(), requiredVis, true) < requiredVis) {
                    this.world.playSound(
                            null, this.posX, this.posY, this.posZ,
                            SoundsTC.wandfail, SoundCategory.NEUTRAL, 0.6f, 1.2f
                    );
                    return true;
                }

                //add knowledge
                if (!knowledge.isResearchKnown(SquirrelWitcheryResearch.CRYSTALLIZED_SQUIRREL_HEART + "@2")
                        && knowledge.addResearch(SquirrelWitcheryResearch.SQUIRREL_RISUNIUM_INTERACTION)
                ) {
                    knowledge.sync(serverPlayer);
                }

                //final ritual stuff
                this.ritualTicksRemaining = RITUAL_DURATION;
                this.setIsPerformingRitual(true);
                if (!player.capabilities.isCreativeMode) heldItem.shrink(1);
            }
            return true;
        }
        return super.processInteract(player, hand);
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
        return !this.isSheared() && !this.isPerformingRitual();
    }

    @Override
    @NonNull
    public List<ItemStack> onSheared(@NonNull ItemStack itemStack, IBlockAccess iBlockAccess, BlockPos blockPos, int i) {
        this.setSheared(true);
        this.shearCountdown = 24000; //should correspond to 1 in-game day
        return List.of(new ItemStack(SquirrelWitcheryItems.SQUIRREL_FUR, this.world.rand.nextInt(1, 4)));
    }

    //---squirrel heart ritual stuff---
    private boolean isPerformingRitual() {
        return this.dataManager.get(PERFORM_RITUAL);
    }

    private void setIsPerformingRitual(boolean value) {
        this.dataManager.set(PERFORM_RITUAL, value);
        this.setNoAI(value);
        this.setEntityInvulnerable(value);
    }

    //---nbt stuff---
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Sheared", this.isSheared());
        compound.setInteger("ShearCooldown", this.shearCountdown);
        compound.setInteger("RitualCooldown", this.ritualCooldown);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("Sheared")) this.setSheared(compound.getBoolean("Sheared"));
        if (compound.hasKey("ShearCooldown")) this.shearCountdown = compound.getInteger("ShearCooldown");
        if (compound.hasKey("RitualCooldown")) this.ritualCooldown = compound.getInteger("RitualCooldown");
    }

    //---anim stuff---
    @Override
    @NonNull
    public AnimationDataEntity getAnimationData() {
        return this.animData;
    }

    @Override
    public void initializeAnimationData(@NonNull AnimationDataEntity animationData) {
        animationData.setScale(entity -> entity.isChild() ? 0.25f : 0.5f);
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
        animationData.addAnimationController(new AnimationController<SquirrelEntity, AnimationDataEntity>(
                this, "ritualCont", "default",
                new AnimationControllerState<AnimationDataEntity>("default")
                        .addStateTransition("ritual", data -> this.isPerformingRitual()),
                new AnimationControllerState<AnimationDataEntity>("ritual")
                        .addStateTransition("default", data -> !this.isPerformingRitual())
                        .addParticleEffect("squirrelwitchery:risunium_squirrel_orbs", "bodyCenter")
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
