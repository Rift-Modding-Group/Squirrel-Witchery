package anightdazingzoroark.squirrelwitchery.server.entity.ai;

import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEntity;
import net.minecraft.entity.EntityLivingBase;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for attack behavior depending on a magical squirrel's weapon
 * */
public abstract class MagicalSquirrelAttackBehavior {
    @NotNull
    protected final MagicalSquirrelEntity squirrel;

    public MagicalSquirrelAttackBehavior(@NotNull MagicalSquirrelEntity squirrel) {
        this.squirrel = squirrel;
    }

    public abstract boolean canUse();

    public abstract boolean canStart(@NotNull EntityLivingBase target);

    public abstract void start();

    public abstract void stop();

    public abstract void move(@NotNull EntityLivingBase target);

    public abstract void update(@NotNull EntityLivingBase target);

    public abstract void attack(@NotNull EntityLivingBase target);

    public abstract void finishUsing();
}
