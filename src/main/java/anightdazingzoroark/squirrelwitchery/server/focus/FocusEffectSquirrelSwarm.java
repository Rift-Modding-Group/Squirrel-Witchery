package anightdazingzoroark.squirrelwitchery.server.focus;

import anightdazingzoroark.riftlib.particle.RiftLibParticleHelper;
import anightdazingzoroark.squirrelwitchery.server.SquirrelWitcheryResearch;
import anightdazingzoroark.squirrelwitchery.server.aspects.SquirrelWitcheryAspects;
import anightdazingzoroark.squirrelwitchery.server.effect.SquirrelWitcheryEffects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;

public class FocusEffectSquirrelSwarm extends FocusEffect {
    @Override
    @NotNull
    public String getResearch() {
        return SquirrelWitcheryResearch.SQUIRREL_SWARM;
    }

    @Override
    @NotNull
    public String getKey() {
        return "squirrelwitchery.SQUIRREL_SWARM";
    }

    @Override
    @NotNull
    public Aspect getAspect() {
        return SquirrelWitcheryAspects.RISUNIUM;
    }

    @Override
    public int getComplexity() {
        return 8 + this.getSettingValue("power") * 3 + this.getSettingValue("duration");
    }

    @Override
    public float getDamageForDisplay(float power) {
        return Math.clamp(Math.round(3f * this.getSettingValue("power") * power), 1, 128);
    }

    @Override
    public boolean execute(@Nullable RayTraceResult target, @Nullable Trajectory trajectory, float power, int num) {
        World world = this.getPackage().world;
        if (world.isRemote || target == null || target.typeOfHit != RayTraceResult.Type.ENTITY || !(target.entityHit instanceof EntityLivingBase living)) {
            return false;
        }

        int damage = Math.clamp(Math.round(3f * this.getSettingValue("power") * power), 1, 128);
        living.addPotionEffect(new PotionEffect(
                SquirrelWitcheryEffects.SQUIRREL_SWARM, this.getSettingValue("duration") * 20, damage - 1
        ));
        return living.isPotionActive(SquirrelWitcheryEffects.SQUIRREL_SWARM);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderParticleFX(@NotNull World world, double x, double y, double z, double mx, double my, double mz) {
        RiftLibParticleHelper.createParticle("squirrelwitchery:nut_rain_impact", x, y, z);
    }

    @Override
    @NotNull
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
                new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)),
                new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntRange(3, 10))
        };
    }
}
