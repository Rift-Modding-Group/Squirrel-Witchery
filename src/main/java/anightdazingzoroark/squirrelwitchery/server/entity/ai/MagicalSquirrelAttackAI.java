package anightdazingzoroark.squirrelwitchery.server.entity.ai;

import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MagicalSquirrelAttackAI extends EntityAIBase {
    public static final float SHOT_RANGE = 16f;

    @NotNull
    private final MagicalSquirrelEntity squirrel;
    @Nullable
    private MagicalSquirrelAttackBehavior activeBehavior;
    @NotNull
    private ItemStack activeWeapon = ItemStack.EMPTY;

    public MagicalSquirrelAttackAI(@NotNull MagicalSquirrelEntity squirrel) {
        this.squirrel = squirrel;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        MagicalSquirrelAttackBehavior behavior = this.squirrel.getAttackBehavior();
        if (!this.squirrel.isEntityAlive() || !this.squirrel.getTransformed()
                || this.squirrel.getBehavior() == MagicalSquirrelEntity.Behavior.SIT
                || behavior == null || !behavior.canUse()
        ) {
            return false;
        }
        EntityLivingBase target = behavior.getTarget();
        return target != null && target.isEntityAlive() && behavior.canStart(target);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    @Override
    public void startExecuting() {
        MagicalSquirrelAttackBehavior behavior = this.squirrel.getAttackBehavior();
        if (behavior == null) return;
        this.activeBehavior = behavior;
        this.activeWeapon = this.squirrel.getHeldItemMainhand();
        this.activeBehavior.start();
    }

    @Override
    public void resetTask() {
        if (this.activeBehavior != null) {
            this.activeBehavior.stop();
            if (this.activeBehavior instanceof MagicalSquirrelRangedAttackBehavior rangedBehavior) rangedBehavior.resetRangedAttack();
        }
        this.squirrel.setUsingBow(false);
        this.squirrel.resetActiveHand();
        this.squirrel.getNavigator().clearPath();
        this.activeBehavior = null;
        this.activeWeapon = ItemStack.EMPTY;
    }

    @Override
    public void updateTask() {
        MagicalSquirrelAttackBehavior behavior = this.squirrel.getAttackBehavior();
        EntityLivingBase target = null;
        if (behavior != null) target = behavior.getTarget();
        if (target == null || !target.isEntityAlive()) {
            this.resetTask();
            return;
        }

        if (this.activeBehavior != behavior || this.activeWeapon != this.squirrel.getHeldItemMainhand()) {
            this.resetTask();
            if (!this.shouldExecute()) return;
            this.startExecuting();
        }
        if (this.activeBehavior == null) return;

        this.squirrel.getLookHelper().setLookPositionWithEntity(target, 30f, 30f);
        this.activeBehavior.move(target);
        this.activeBehavior.update(target);
    }
}
