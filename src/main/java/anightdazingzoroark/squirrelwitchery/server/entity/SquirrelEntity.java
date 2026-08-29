package anightdazingzoroark.squirrelwitchery.server.entity;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.controller.AnimationControllerState;
import anightdazingzoroark.riftlib.core.manager.AnimationDataEntity;
import anightdazingzoroark.squirrelwitchery.server.items.SquirrelWitcheryItems;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class SquirrelEntity extends EntityAnimal implements IAnimatable<AnimationDataEntity> {
    @NotNull
    private final AnimationDataEntity animData = new AnimationDataEntity(this, 0.5f);

    public SquirrelEntity(World worldIn) {
        super(worldIn);
        this.setSize(0.5f, 0.5f);
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
        this.tasks.addTask(4, new EntityAITempt(this, 1.2D, false, Set.of(SquirrelWitcheryItems.NUT)));
        this.tasks.addTask(5, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    }

    @Override
    public AnimationDataEntity getAnimationData() {
        return this.animData;
    }

    @Override
    public void initializeAnimationData(AnimationDataEntity animationData) {
        animationData.addAnimationController(new AnimationController<SquirrelEntity, AnimationDataEntity>(
                this, "movement", "default",
                new AnimationControllerState<AnimationDataEntity>("default", 0.1)
                        .addStateTransition("walk", AnimationDataEntity::isMoving),
                new AnimationControllerState<AnimationDataEntity>("walk", 0.1)
                        .addAnimation("animation.squirrel.walk")
                        .addStateTransition("default", data -> !data.isMoving())
        ));
    }

    @Override
    public @Nullable EntityAgeable createChild(EntityAgeable ageable) {
        return null;
    }
}
