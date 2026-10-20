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
import net.minecraft.world.World;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class MagicalSquirrelEntity extends EntityTameable implements IAnimatable<AnimationDataEntityLiving> {
    @NonNull
    private final AnimationDataEntityLiving animationData = new AnimationDataEntityLiving(this);

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
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
        this.tasks.addTask(2, new EntityAITempt(this, 1.2D, false, Set.of(SquirrelWitcheryItems.NUT, SquirrelWitcheryItems.BIG_NUT)));
        this.tasks.addTask(3, new EntityAIAvoidEntity<>(this, EntityPlayer.class, 16f, 0.8D, 1.33D));
        this.tasks.addTask(4, new EntityAIWanderAvoidWater(this, 1D));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 6f));
        this.tasks.addTask(6, new EntityAILookIdle(this));
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
        animationDataEntityLiving.addAnimationController(new AnimationController<MagicalSquirrelEntity, AnimationDataEntityLiving>(
                this, "movementCont", "default",
                new AnimationControllerState<AnimationDataEntityLiving>("default", 0.1)
                        .addStateTransition("walk", AnimationDataEntityLiving::isMoving),
                new AnimationControllerState<AnimationDataEntityLiving>("walk", 0.1)
                        .addAnimation("animation.squirrel.walk")
                        .addStateTransition("default", data -> !data.isMoving())
        ));
    }

    //-----stuff i dont care about-----
    @Override
    @Nullable
    public EntityAgeable createChild(EntityAgeable ageable) {
        return null;
    }
}
