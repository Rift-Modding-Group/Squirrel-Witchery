package anightdazingzoroark.squirrelwitchery.server.entity;

import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.controller.AnimationControllerState;
import anightdazingzoroark.riftlib.core.manager.AnimationDataProjectile;
import anightdazingzoroark.riftlib.projectile.RiftLibProjectile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class ExplosiveNutProjectileEntity extends RiftLibProjectile {
    @NotNull
    private final AnimationDataProjectile data = new AnimationDataProjectile(this);

    public ExplosiveNutProjectileEntity(World worldIn) {
        super(worldIn);
    }

    public ExplosiveNutProjectileEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public ExplosiveNutProjectileEntity(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
    }

    @Override
    public void projectileImpactEffects(@Nullable EntityLivingBase entityLivingBase, @NotNull Vec3d hitPos) {
        this.world.createExplosion(this, hitPos.x, hitPos.y, hitPos.z, 4f, true);
    }

    @Override
    public double getDamage() {
        return 0;
    }

    @Override
    public boolean canRotateVertically() {
        return false;
    }

    @Override
    public SoundEvent getOnProjectileHitSound() {
        return null;
    }

    @Override
    @NonNull
    public AnimationDataProjectile getAnimationData() {
        return this.data;
    }

    @Override
    public void initializeAnimationData(@NonNull AnimationDataProjectile animData) {
        animData.addAnimationController(new AnimationController<ExplosiveNutProjectileEntity, AnimationDataProjectile>(
                this, "bomb", "default",
                new AnimationControllerState<AnimationDataProjectile>("default")
                        .addParticleEffect("riftlib:bomb_flame", "locator")
        ));
    }
}
