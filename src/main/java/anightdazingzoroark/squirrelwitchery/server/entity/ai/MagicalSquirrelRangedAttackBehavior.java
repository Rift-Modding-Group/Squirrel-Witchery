package anightdazingzoroark.squirrelwitchery.server.entity.ai;

import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEntity;
import net.minecraft.entity.EntityLivingBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToDoubleFunction;

/**
 * Shared movement and hit feedback for ranged weapon behaviors.
 * */
public abstract class MagicalSquirrelRangedAttackBehavior extends MagicalSquirrelAttackBehavior {
    @NotNull
    private final ToDoubleFunction<MagicalSquirrelEntity> rangedAttackRange;
    @Nullable
    private EntityLivingBase rangedTarget;
    private double preferredRangedDistance = -1D;
    private boolean waitingForRangedHit;
    private int rangedHitDeadlineTick;

    public MagicalSquirrelRangedAttackBehavior(@NotNull MagicalSquirrelEntity squirrel) {
        this(squirrel, ignored -> MagicalSquirrelAttackAI.SHOT_RANGE);
    }

    public MagicalSquirrelRangedAttackBehavior(@NotNull MagicalSquirrelEntity squirrel, @NotNull ToDoubleFunction<MagicalSquirrelEntity> rangedAttackRange) {
        super(squirrel);
        this.rangedAttackRange = rangedAttackRange;
    }

    @Override
    public void move(@NotNull EntityLivingBase target) {
        if (this.canRangedAttack(target)) this.squirrel.getNavigator().clearPath();
        else this.squirrel.getNavigator().tryMoveToEntityLiving(target, 1D);
    }

    public boolean canRangedAttack(@NotNull EntityLivingBase target) {
        if (this.rangedTarget != target) {
            this.resetRangedAttack();
            this.rangedTarget = target;
        }
        double maximumRange = this.rangedAttackRange.applyAsDouble(this.squirrel);
        double range = maximumRange;
        if (this.preferredRangedDistance >= 0D) range = Math.min(range, this.preferredRangedDistance);
        //path closer to the target if the ranged attacks fail
        if (this.waitingForRangedHit && this.squirrel.ticksExisted >= this.rangedHitDeadlineTick) {
            range = Math.clamp(Math.min(range, this.squirrel.getDistance(target)) - 2D, Math.min(2D, maximumRange), maximumRange);
            this.preferredRangedDistance = range;
            this.waitingForRangedHit = false;
        }
        return this.squirrel.getDistanceSq(target) <= range * range && this.squirrel.getEntitySenses().canSee(target);
    }

    //returns whether this shot opened a new window for hit feedback
    public boolean recordRangedShot(@NotNull EntityLivingBase target) {
        if (this.rangedTarget != target) {
            this.resetRangedAttack();
            this.rangedTarget = target;
        }
        //rapid-fire weapons must not keep postponing the result of earlier shots
        if (this.waitingForRangedHit) return false;
        this.waitingForRangedHit = true;

        //give projectiles time to arrive before treating the attack as a miss
        int travelWindow = Math.clamp(40 + (int) Math.ceil(this.squirrel.getDistance(target) * 2D), 40, 100);
        this.rangedHitDeadlineTick = this.squirrel.ticksExisted + travelWindow;
        return true;
    }

    //for when hit is successful
    public void recordRangedHit(@NotNull EntityLivingBase target) {
        if (this.rangedTarget == target) this.waitingForRangedHit = false;
    }

    public void resetRangedAttack() {
        this.rangedTarget = null;
        this.preferredRangedDistance = -1D;
        this.waitingForRangedHit = false;
        this.rangedHitDeadlineTick = 0;
    }
}
