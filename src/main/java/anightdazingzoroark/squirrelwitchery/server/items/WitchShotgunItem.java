package anightdazingzoroark.squirrelwitchery.server.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class WitchShotgunItem extends Item implements IRisuniumConsumer {
    private static final int RISUNIUM_COST = 20;
    private static final double RANGE = 32D;

    public WitchShotgunItem() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) return;

        ItemStack stack = new ItemStack(this);
        this.setRisuniumAmount(stack, this.getMaxRisunium());
        items.add(stack);
    }

    @Override
    public EnumActionResult onItemUseFirst(
            EntityPlayer player, World world, BlockPos pos, EnumFacing side,
            float hitX, float hitY, float hitZ, EnumHand hand
    ) {
        if (this.getRisuniumFromJarBlock(player, world, pos, hand)) return EnumActionResult.SUCCESS;
        return this.useShotgun(world, player, hand).getType();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        return this.useShotgun(world, player, hand);
    }

    private ActionResult<ItemStack> useShotgun(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, stack);

        //---refill---
        if (player.isSneaking()) {
            boolean refilled = this.getRisuniumFromJarInInventory(player, world, hand);
            return new ActionResult<>(refilled ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, stack);
        }

        //---block when no risunium or is cooling down---
        if (player.getCooldownTracker().hasCooldown(this) || this.getRisuniumAmount(stack) < RISUNIUM_COST) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        //---normal shotgun use---
        //use risunium and other stuff
        this.setRisuniumAmount(stack, this.getRisuniumAmount(stack) - RISUNIUM_COST);
        player.getCooldownTracker().setCooldown(this, 20);
        player.swingArm(hand);
        world.playSound(
                null,
                player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS,
                0.8f, 1.35f
        );

        Vec3d start = player.getPositionEyes(1f);
        Map<Entity, Integer> pelletHits = new IdentityHashMap<>();

        //shotgun pellet spread
        for (int pellet = 0; pellet < 12; pellet++) {
            double spreadRadius = pellet == 0 ? 0D : Math.sqrt(player.getRNG().nextDouble()) * 15D; //pellet spread is by up to 15 degrees
            double spreadAngle = player.getRNG().nextDouble() * Math.PI * 2.0;
            float pitch = player.rotationPitch + (float) (Math.sin(spreadAngle) * spreadRadius);
            float yaw = player.rotationYaw + (float) (Math.cos(spreadAngle) * spreadRadius);
            Vec3d direction = Vec3d.fromPitchYaw(pitch, yaw);
            Vec3d end = start.add(direction.scale(RANGE));
            RayTraceResult blockHit = world.rayTraceBlocks(start, end, false, true, false);
            double nearestDistance = blockHit == null ? RANGE : start.distanceTo(blockHit.hitVec);
            Entity nearestEntity = null;
            AxisAlignedBB searchBounds = player.getEntityBoundingBox()
                    .expand(direction.x * RANGE, direction.y * RANGE, direction.z * RANGE)
                    .grow(1D);

            for (Entity candidate : world.getEntitiesWithinAABBExcludingEntity(player, searchBounds)) {
                if (!candidate.canBeCollidedWith()) continue;
                if (candidate.getLowestRidingEntity() == player.getLowestRidingEntity() && !candidate.canRiderInteract()) continue;

                AxisAlignedBB hitbox = candidate.getEntityBoundingBox().grow(candidate.getCollisionBorderSize());
                RayTraceResult entityHit = hitbox.calculateIntercept(start, end);

                double distance;
                if (hitbox.contains(start)) distance = 0D;
                else if (entityHit != null) distance = start.distanceTo(entityHit.hitVec);
                else continue;

                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestEntity = candidate;
                }
            }

            if (nearestEntity != null) pelletHits.merge(nearestEntity, 1, Integer::sum);
        }

        //deal damage
        DamageSource damageSource = new EntityDamageSource("witchShotgun", player).setProjectile();
        for (Map.Entry<Entity, Integer> hit : pelletHits.entrySet()) {
            hit.getKey().attackEntityFrom(damageSource, hit.getValue() * 5f); //each pellet does 5 damage
        }

        player.inventoryContainer.detectAndSendChanges();
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(this.stringForDisplayAmount(stack));
    }

    @Override
    public int getMaxRisunium() {
        return RISUNIUM_COST;
    }
}
