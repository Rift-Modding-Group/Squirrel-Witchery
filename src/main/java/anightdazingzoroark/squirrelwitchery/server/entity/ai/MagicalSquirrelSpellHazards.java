package anightdazingzoroark.squirrelwitchery.server.entity.ai;

import anightdazingzoroark.squirrelwitchery.server.entity.NutRainEntity;
import anightdazingzoroark.squirrelwitchery.server.focus.FocusEffectNutRain;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.common.entities.projectile.EntityFocusCloud;
import thaumcraft.common.entities.projectile.EntityFocusMine;
import thaumcraft.common.items.casters.foci.FocusEffectCurse;
import thaumcraft.common.items.casters.foci.FocusEffectHeal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Help deal with spell-affected spaces where magical squirrels should not path to.
 * Is to be universal for all magical sqrls + the effects already save on unload
 * in some way and come back here or don't save at all and no one cares about them
 * being saved on unload, hence being a static cache
 * */
public class MagicalSquirrelSpellHazards {
    //hazardous plan areas
    private static final Map<World, Map<AxisAlignedBB, Long>> PLAN_AREAS = new WeakHashMap<>();
    //healing areas which can still be pathed into
    private static final Map<Entity, Boolean> HEALING_AREAS = new WeakHashMap<>();

    public static boolean isHealingOnly(@Nullable FocusPackage spell) {
        if (spell == null) return false;
        FocusEffect[] effects = spell.getFocusEffects();
        if (effects.length == 0) return false;
        for (FocusEffect effect : effects) {
            if (!(effect instanceof FocusEffectHeal)) return false;
        }
        return true;
    }

    public static void recordPlanArea(@Nullable FocusPackage spell, @NotNull List<RayTraceResult> targets) {
        if (spell == null || targets.isEmpty() || isHealingOnly(spell)) return;
        AxisAlignedBB area = new AxisAlignedBB(targets.getFirst().getBlockPos()).expand(0D, 1D, 0D);
        int duration = 0;
        double radius = 0D;
        for (FocusEffect effect : spell.getFocusEffects()) {
            duration = Math.max(duration, effect.getSettingValue("duration"));
            if (effect instanceof FocusEffectCurse) {
                radius = Math.clamp(1.5D * effect.getSettingValue("power") * spell.getPower(), radius, 8D);
            }
            if (effect instanceof FocusEffectNutRain) radius = Math.max(radius, NutRainEntity.RADIUS);
        }
        for (int index = 1; index < targets.size(); index++) {
            AxisAlignedBB block = new AxisAlignedBB(targets.get(index).getBlockPos()).expand(0D, 1D, 0D);
            area = area.union(block);
        }
        //Plan itself is instantaneous; keep its selected area clear while its effects can linger.
        PLAN_AREAS.computeIfAbsent(spell.world, world -> new HashMap<>())
                .merge(area.grow(radius + 0.5D), spell.world.getTotalWorldTime() + duration * 20L + 60L, Math::max);
    }

    @NotNull
    public static List<AxisAlignedBB> getDangerousAreas(@NotNull World world, @NotNull AxisAlignedBB bounds) {
        ArrayList<AxisAlignedBB> areas = new ArrayList<>();
        Map<AxisAlignedBB, Long> plans = PLAN_AREAS.get(world);
        if (plans != null) {
            plans.entrySet().removeIf(entry -> entry.getValue() <= world.getTotalWorldTime());
            for (AxisAlignedBB area : plans.keySet()) {
                if (area.intersects(bounds)) areas.add(area);
            }
            if (plans.isEmpty()) PLAN_AREAS.remove(world);
        }
        for (NutRainEntity rain : world.getEntitiesWithinAABB(
                NutRainEntity.class, bounds.grow(NutRainEntity.RADIUS, 0D, NutRainEntity.RADIUS)
                        .expand(0D, NutRainEntity.MAX_HEIGHT, 0D), candidate -> !candidate.isDead
        )) {
            AxisAlignedBB rainArea = rain.getRainArea();
            if (rainArea.intersects(bounds)) areas.add(rainArea);
        }
        List<Entity> spellHazardEntities = world.getEntitiesWithinAABB(
                Entity.class, bounds,
                candidate -> {
                    if (!(candidate instanceof EntityFocusCloud || candidate instanceof EntityFocusMine) || candidate.isDead) return false;
                    //healing spots r to be an exception
                    return !HEALING_AREAS.computeIfAbsent(candidate, entity -> {
                        //These entities expose their spell through public NBT serialization, but have no package getter.
                        NBTTagCompound data = new NBTTagCompound();
                        switch (entity) {
                            case EntityFocusCloud cloud -> cloud.writeEntityToNBT(data);
                            case EntityFocusMine mine -> mine.writeEntityToNBT(data);
                            default -> { return false; }
                        }
                        FocusPackage spell = new FocusPackage();
                        spell.deserialize(data.getCompoundTag("pack"));
                        return isHealingOnly(spell);
                    });
                }
        );
        for (Entity entity : spellHazardEntities) {
            double radius = 2D;
            if (entity instanceof EntityFocusCloud cloud) radius = cloud.getRadius() + 0.5D;
            areas.add(new AxisAlignedBB(
                    entity.posX - radius, entity.posY - radius, entity.posZ - radius,
                    entity.posX + radius, entity.posY + radius, entity.posZ + radius
            ));
        }
        return areas;
    }
}
