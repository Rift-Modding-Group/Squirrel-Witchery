package anightdazingzoroark.squirrelwitchery;

import anightdazingzoroark.riftlib.inventory.RiftLibInventoryHandler;
import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEntity;
import anightdazingzoroark.squirrelwitchery.server.entity.ai.MagicalSquirrelAttackAI;
import anightdazingzoroark.squirrelwitchery.server.entity.ai.MagicalSquirrelRangedAttackBehavior;
import anightdazingzoroark.squirrelwitchery.server.entity.ai.MagicalSquirrelSpellHazards;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.casters.*;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.casters.foci.FocusEffectHeal;
import thaumcraft.common.items.casters.foci.FocusMediumBolt;
import thaumcraft.common.items.casters.foci.FocusMediumPlan;
import thaumcraft.common.items.casters.foci.FocusMediumTouch;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.ArrayList;

public class MagicalSquirrelCastingUtils {
    @NotNull
    public static ItemStack getOrInstallFocus(@NotNull ItemStack weapon, @NotNull RiftLibInventoryHandler inventory) {
        if (!(weapon.getItem() instanceof ItemCaster caster)) return ItemStack.EMPTY;
        ItemStack installedFocus = caster.getFocusStack(weapon);
        if (installedFocus == null) installedFocus = ItemStack.EMPTY;
        if (hasSpell(installedFocus)) return installedFocus;
        RiftLibInventoryHandler.ItemSearchResult result = inventory.findItem(
                RiftLibInventoryHandler.ItemSearchDirection.LAST_TO_FIRST,
                MagicalSquirrelCastingUtils::hasSpell
        );
        if (!result.successful()) return installedFocus;
        ItemStack focus = inventory.extractItem(result.slot(), 1, true);
        if (focus.isEmpty()) return ItemStack.EMPTY;

        int returnSlot = -1;
        if (!installedFocus.isEmpty()) {
            //a single replacement focus frees its slot, even when every inventory slot is occupied
            if (inventory.getStackInSlot(result.slot()).getCount() == 1 && inventory.isItemValid(result.slot(), installedFocus)
                    && installedFocus.getCount() <= Math.min(inventory.getSlotLimit(result.slot()), installedFocus.getMaxStackSize())) {
                returnSlot = result.slot();
            }
            else {
                for (int slot = 0; slot < inventory.getSlots(); slot++) {
                    if (inventory.insertItem(slot, installedFocus, true).isEmpty()) {
                        returnSlot = slot;
                        break;
                    }
                }
            }
            //Keep both focuses where they are if the old one cannot be returned without losing items.
            if (returnSlot < 0) return installedFocus;
        }
        caster.setFocus(weapon, focus);
        inventory.extractItem(result.slot(), 1, false);
        if (returnSlot >= 0) inventory.insertItem(returnSlot, installedFocus, false);
        return focus;
    }

    public static boolean hasSpell(@NotNull ItemStack focus) {
        if (!(focus.getItem() instanceof ItemFocus)) return false;
        return hasFocusMedium(ItemFocus.getPackage(focus));
    }

    //mostly to prevent magical sqrls from choosing spells that will hit themselves
    private static boolean hasFocusMedium(@Nullable FocusPackage spell) {
        if (spell == null) return false;
        ArrayList<FocusPackage> packages = new ArrayList<>();
        packages.add(spell);
        for (int index = 0; index < packages.size(); index++) {
            for (IFocusElement element : packages.get(index).nodes) {
                //The engine's root is implicit and cannot replace a medium chosen for the spell.
                if (element instanceof FocusMedium && !(element instanceof FocusMediumRoot)) return true;
                if (element instanceof FocusPackage nested) packages.add(nested);
                else if (element instanceof FocusModSplit split) packages.addAll(split.getSplitPackages());
            }
        }
        return false;
    }

    //special method to resolve casting target
    @Nullable
    public static EntityLivingBase getCastingTarget(
            @Nullable FocusPackage spell, @Nullable EntityLivingBase owner, @Nullable EntityLivingBase enemy, boolean healingOwner
    ) {
        if (spell == null) return null;
        double healingThreshold = SquirrelWitcheryConfig.magicalSquirrelBehavior.ownerHealingHealthThreshold;
        for (FocusEffect effect : spell.getFocusEffects()) {
            //special case for the focus heal effect
            if (effect instanceof FocusEffectHeal) {
                //start at the configured threshold, then continue until the owner is fully healed
                if (owner != null && owner.isEntityAlive() && owner.getHealth() < owner.getMaxHealth()
                        && (healingOwner || owner.getHealth() <= owner.getMaxHealth() * (Math.clamp(healingThreshold, 0D, 100D) / 100D))
                ) {
                    return owner;
                }
                //enemy is undead
                else if (enemy != null && enemy.isEntityUndead()) return enemy;
            }
            else return enemy;
        }

        return null;
    }

    public static double getAttackRange(@NotNull MagicalSquirrelEntity squirrel) {
        ItemStack focus = getOrInstallFocus(squirrel.getHeldItemMainhand(), squirrel.getInventoryHandler());
        FocusPackage focusPackage = ItemFocus.getPackage(focus);
        if (focusPackage == null) return MagicalSquirrelAttackAI.SHOT_RANGE;
        double range = getPackageRange(focusPackage, 0D);
        return range > 0D ? range : MagicalSquirrelAttackAI.SHOT_RANGE;
    }

    private static double getPackageRange(@NotNull FocusPackage focusPackage, double range) {
        for (IFocusElement element : focusPackage.nodes) {
            if (element instanceof FocusMediumRoot) continue;
            if (element instanceof FocusModSplit split) {
                double branchRange = range;
                for (FocusPackage branch : split.getSplitPackages()) {
                    branchRange = Math.max(branchRange, getPackageRange(branch, range));
                }
                return branchRange;
            }
            if (element instanceof FocusPackage nested) return getPackageRange(nested, range);
            if (element instanceof FocusMediumBolt) range += 16D;
            else if (element instanceof FocusMediumTouch) range += 4D;
            else if (element instanceof FocusMedium medium) {
                double mediumRange = MagicalSquirrelAttackAI.SHOT_RANGE;
                if (medium.getSettingList() != null && medium.getSettingList().contains("range") && medium.getSettingValue("range") > 0) {
                    mediumRange = medium.getSettingValue("range");
                }
                range += mediumRange;
            }
        }
        return range;
    }

    /**
     * Returns the delay before the next attempt, including a short retry when local vis is depleted.
     * */
    public static int cast(@NotNull MagicalSquirrelEntity squirrel, @NotNull EntityLivingBase target) {
        ItemStack weapon = squirrel.getHeldItemMainhand();
        if (squirrel.world.isRemote || !(weapon.getItem() instanceof ItemCaster)) return 10;
        ItemStack focusStack = getOrInstallFocus(weapon, squirrel.getInventoryHandler());
        if (!(focusStack.getItem() instanceof ItemFocus focus)) return 10;
        FocusPackage source = ItemFocus.getPackage(focusStack);
        if (!hasFocusMedium(source)) return 10;
        if (!(squirrel.getAttackBehavior() instanceof MagicalSquirrelRangedAttackBehavior behavior) || target != behavior.getTarget()) return 10;

        float visCost = focus.getVisCost(focusStack);
        if (AuraHelper.drainVis(squirrel.world, squirrel.getPosition(), visCost, true) + 0.00001f < visCost) return 10;

        FocusPackage spell = preparePackage(source, squirrel);
        Vec3d start = squirrel.getPositionEyes(1f).add(0D, -0.1D, 0D);
        Vec3d direction = new Vec3d(target.posX, target.getEntityBoundingBox().minY + target.height / 2D, target.posZ).subtract(start).normalize();
        EntityLivingBase rootTarget = squirrel;
        if (target == squirrel.getOwner()) rootTarget = target;
        FocusMediumRoot root = new FocusMediumRoot(
                new Trajectory[]{new Trajectory(start, direction)},
                new RayTraceResult[]{new RayTraceResult(rootTarget)}
        );
        if (spell.nodes.getFirst() instanceof FocusMediumRoot) spell.nodes.set(0, root);
        else spell.nodes.addFirst(root);

        // Cast directly from the selected focus while keeping the real caster equipped.
        AuraHelper.drainVis(squirrel.world, squirrel.getPosition(), visCost, false);
        behavior.recordRangedShot(target);
        FocusEngine.castFocusPackage(squirrel, spell, true);
        squirrel.swingArm(EnumHand.MAIN_HAND);

        int delay = Math.max(1, focus.getActivationTime(focusStack));
        ArrayList<FocusPackage> packages = new ArrayList<>();
        packages.add(source);
        outer: for (int index = 0; index < packages.size(); index++) {
            for (IFocusElement element : packages.get(index).nodes) {
                //special case for plan, delay must use the spell's effect time + 3 more seconds
                if (element instanceof FocusMediumPlan) {
                    for (FocusEffect effect : source.getFocusEffects()) {
                        if (effect.getSetting("duration") != null) delay = Math.max(delay, effect.getSettingValue("duration") * 20);
                    }
                    delay += 60;
                    break outer;
                }
                else if (element instanceof FocusPackage nested) packages.add(nested);
                else if (element instanceof FocusModSplit split) packages.addAll(split.getSplitPackages());
            }
        }
        return delay;
    }

    @NotNull
    private static FocusPackage preparePackage(@NotNull FocusPackage source, @NotNull EntityLivingBase caster) {
        FocusPackage spell = source.copy(caster);
        spell.world = caster.world;
        spell.setCasterUUID(caster.getUniqueID());
        for (int index = 0; index < spell.nodes.size(); index++) {
            IFocusElement element = spell.nodes.get(index);
            if (element instanceof FocusPackage nested) {
                spell.nodes.set(index, preparePackage(nested, caster));
            }
            else if (element instanceof FocusModSplit split) {
                split.getSplitPackages().replaceAll(focusPackage -> preparePackage(focusPackage, caster));
            }
            else {
                //replace focus mediums originall meant for player with those that work for le magical sqrl
                //ps: stfu intellij
                FocusNode replacement;
                if (element instanceof FocusMediumBolt) replacement = new SquirrelBolt();
                else if (element instanceof FocusMediumTouch) replacement = new SquirrelTouch();
                else if (element instanceof FocusMediumPlan) replacement = new SquirrelPlan();
                else continue;

                replacement.initialize();
                for (String setting : ((FocusNode) element).getSettingList()) {
                    if (replacement.getSetting(setting) != null) replacement.getSetting(setting).setValue(((FocusNode) element).getSettingValue(setting));
                }
                spell.nodes.set(index, replacement);
            }
        }
        return spell;
    }

    @Nullable
    private static RayTraceResult trace(@NotNull FocusNode node, @NotNull Trajectory trajectory, double range) {
        FocusPackage spell = node.getPackage();
        Vec3d direction = trajectory.direction.normalize();
        RayTraceResult entityHit = EntityUtils.getPointedEntityRay(spell.world, spell.getCaster(), trajectory.source, direction, 0.25D, range, 0.25f, false);
        RayTraceResult blockHit = spell.world.rayTraceBlocks(trajectory.source, trajectory.source.add(direction.scale(range)));
        if (entityHit != null && (blockHit == null || trajectory.source.squareDistanceTo(entityHit.hitVec) < trajectory.source.squareDistanceTo(blockHit.hitVec))) {
            return entityHit;
        }
        return blockHit;
    }

    //-----replacement classes for focus mediums for magical squirrels-----
    private static class SquirrelPlan extends FocusMediumPlan {
        @Override
        @NotNull
        public RayTraceResult[] supplyTargets() {
            FocusPackage spell = this.getPackage();
            if (spell == null || this.getParent() == null || spell.getCaster() == null) return new RayTraceResult[0];
            Trajectory[] trajectories = this.getParent().supplyTrajectories();
            if (trajectories == null) return new RayTraceResult[0];

            EntityLivingBase caster = spell.getCaster();
            ItemStack weapon = caster.getHeldItemMainhand();
            if (!(weapon.getItem() instanceof ICaster)) weapon = caster.getHeldItemOffhand();
            if (!(weapon.getItem() instanceof ICaster)) return new RayTraceResult[0];

            ArrayList<RayTraceResult> targets = new ArrayList<>();
            for (Trajectory trajectory : trajectories) {
                RayTraceResult hit = trace(this, trajectory, 16D);
                if (hit != null && hit.entityHit instanceof EntityLivingBase target) {
                    //Plan effects act on blocks; aim the saved area at the ground beneath the intercepted target
                    Vec3d feet = new Vec3d(target.posX, target.getEntityBoundingBox().minY + 0.1D, target.posZ);
                    hit = spell.world.rayTraceBlocks(feet, feet.add(0D, -4D, 0D));
                    if (hit != null && trajectory.source.squareDistanceTo(hit.hitVec) > 16D * 16D) continue;
                }
                if (hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK) continue;

                //reuse Plan's full/surface selection and the caster's saved area; its selector does not use the player argument
                ArrayList<BlockPos> blocks = this.getArchitectBlocks(weapon, spell.world, hit.getBlockPos(), hit.sideHit, null);
                blocks.sort(new BlockUtils.BlockPosComparator(hit.getBlockPos()));
                for (BlockPos pos : blocks) {
                    targets.add(new RayTraceResult(new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), hit.sideHit, pos));
                }
            }
            MagicalSquirrelSpellHazards.recordPlanArea(this.getRemainingPackage(), targets);
            return targets.toArray(RayTraceResult[]::new);
        }
    }

    private static class SquirrelTouch extends FocusMediumTouch {
        @Override
        @NotNull
        public RayTraceResult[] supplyTargets() {
            return findTargets(this, 4D);
        }

        @Override
        @NotNull
        public Trajectory[] supplyTrajectories() {
            return findTrajectories(this, 4D);
        }
    }

    private static class SquirrelBolt extends FocusMediumBolt {
        @Override
        @NotNull
        public RayTraceResult[] supplyTargets() {
            return findTargets(this, 16D);
        }

        @Override
        @NotNull
        public Trajectory[] supplyTrajectories() {
            return findTrajectories(this, 16D);
        }
    }

    @NotNull
    private static RayTraceResult[] findTargets(@NotNull FocusNode node, double range) {
        ArrayList<RayTraceResult> targets = new ArrayList<>();
        if (node.getParent() != null && node.getParent().supplyTrajectories() != null) {
            for (Trajectory trajectory : node.getParent().supplyTrajectories()) {
                RayTraceResult hit = trace(node, trajectory, range);
                if (hit != null) targets.add(hit);
            }
        }
        return targets.toArray(RayTraceResult[]::new);
    }

    @NotNull
    private static Trajectory[] findTrajectories(@NotNull FocusNode node, double range) {
        ArrayList<Trajectory> trajectories = new ArrayList<>();
        if (node.getParent() != null && node.getParent().supplyTrajectories() != null) {
            for (Trajectory trajectory : node.getParent().supplyTrajectories()) {
                Vec3d direction = trajectory.direction.normalize();
                Vec3d end = trajectory.source.add(direction.scale(range));
                RayTraceResult hit = trace(node, trajectory, range);
                if (hit != null && hit.hitVec != null) end = hit.hitVec;
                trajectories.add(new Trajectory(end, direction));
            }
        }
        return trajectories.toArray(Trajectory[]::new);
    }
}
