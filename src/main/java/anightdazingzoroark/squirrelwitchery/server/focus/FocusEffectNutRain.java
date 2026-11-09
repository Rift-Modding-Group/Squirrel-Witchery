package anightdazingzoroark.squirrelwitchery.server.focus;

import anightdazingzoroark.riftlib.RiftLibMod;
import anightdazingzoroark.riftlib.particle.RiftLibParticleHelper;
import anightdazingzoroark.riftlib.sounds.RiftLibSoundHelper;
import anightdazingzoroark.squirrelwitchery.server.SquirrelWitcheryResearch;
import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import anightdazingzoroark.squirrelwitchery.server.entity.NutRainEntity;
import anightdazingzoroark.squirrelwitchery.server.sounds.SquirrelWitcherySounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;

public class FocusEffectNutRain extends FocusEffect {
    @Override
    @NotNull
    public String getResearch() {
        return SquirrelWitcheryResearch.NUT_RAIN;
    }

    @Override
    @NotNull
    public String getKey() {
        return "squirrelwitchery.NUT_RAIN";
    }

    @Override
    @NotNull
    public Aspect getAspect() {
        return SquirrelWitcheryAspects.RISUNIUM;
    }

    @Override
    public int getComplexity() {
        return 6 + this.getSettingValue("power") * 3 + this.getSettingValue("duration");
    }

    @Override
    public float getDamageForDisplay(float power) {
        return this.getSettingValue("power") * power;
    }

    @Override
    public boolean execute(@Nullable RayTraceResult target, @Nullable Trajectory trajectory, float power, int num) {
        World world = getPackage().world;
        if (world.isRemote || target == null || target.typeOfHit == RayTraceResult.Type.MISS) return false;

        Vec3d origin;
        //spawn above the entity if it hit one
        if (target.typeOfHit == RayTraceResult.Type.ENTITY && target.entityHit != null) {
            origin = new Vec3d(target.entityHit.posX, target.entityHit.getEntityBoundingBox().minY + 0.05D, target.entityHit.posZ);
        }
        //spawn on top of the block if it hit one
        else if (target.typeOfHit == RayTraceResult.Type.BLOCK && target.hitVec != null && target.sideHit != null) {
            origin = target.hitVec.add(new Vec3d(target.sideHit.getDirectionVec()).scale(0.05D));
        }
        else return false;

        //create le entity
        NutRainEntity nutRainEntity = new NutRainEntity(
                world, origin, this.getPackage().getCasterUUID(),
                this.getSettingValue("duration") * 20,
                Math.max(1, Math.round(8f * this.getSettingValue("power") * power))
        );
        if (world.spawnEntity(nutRainEntity)) {
            RiftLibSoundHelper.playLoopingSound(nutRainEntity, SquirrelWitcherySounds.NUT_RAIN_SOUNDS, SoundCategory.PLAYERS);
            return true;
        }
        return false;
    }

    @Override
    @NotNull
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
                new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)),
                new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntRange(3, 10))
        };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderParticleFX(@NotNull World world, double x, double y, double z, double mx, double my, double mz) {
        RiftLibParticleHelper.createParticle("squirrelwitchery:nut_rain_impact", x, y, z);
    }

    public void onCast(Entity caster) {
        caster.world.playSound(
                null, caster.getPosition().up(), SoundEvents.ENTITY_ENDERDRAGON_FIREBALL_EPLD, SoundCategory.PLAYERS,
                0.25f, 1f + (float) (caster.world.rand.nextGaussian() * 0.05D)
        );
    }
}