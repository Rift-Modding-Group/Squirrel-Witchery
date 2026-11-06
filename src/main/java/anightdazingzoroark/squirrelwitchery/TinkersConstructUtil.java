package anightdazingzoroark.squirrelwitchery;

import anightdazingzoroark.riftlib.inventory.RiftLibInventoryHandler;
import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArrow;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.events.ProjectileEvent;
import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ProjectileLauncherNBT;
import slimeknights.tconstruct.library.tools.ProjectileNBT;
import slimeknights.tconstruct.library.tools.ranged.BowCore;
import slimeknights.tconstruct.library.tools.ranged.IAmmo;
import slimeknights.tconstruct.library.tools.ranged.ProjectileCore;
import slimeknights.tconstruct.library.traits.IProjectileTrait;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.tools.Scythe;
import slimeknights.tconstruct.tools.ranged.item.Arrow;
import slimeknights.tconstruct.tools.ranged.item.Bolt;
import slimeknights.tconstruct.tools.ranged.item.CrossBow;
import slimeknights.tconstruct.tools.ranged.item.LongBow;
import slimeknights.tconstruct.tools.ranged.item.Shuriken;

import java.util.List;

public class TinkersConstructUtil {
    //-----melee weapons-----
    public static boolean isMeleeWeapon(@NotNull ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ToolCore tool
                && !tool.hasCategory(Category.NO_MELEE) && !tool.hasCategory(Category.PROJECTILE)
                && !tool.hasCategory(Category.LAUNCHER);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean canUseMeleeWeapon(@NotNull ItemStack stack) {
        return isMeleeWeapon(stack) && stack.hasTagCompound() && !ToolHelper.isBroken(stack);
    }

    public static int getAttackCooldownTicks(@NotNull MagicalSquirrelEntity squirrel) {
        double speed = squirrel.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue();
        return Math.max(1, (int) Math.ceil(20D / Math.max(0.1D, speed)));
    }

    public static void updateTools(@NotNull MagicalSquirrelEntity squirrel) {
        for (int slot = 0; slot < squirrel.getInventoryHandler().getSlots(); slot++) {
            ItemStack stack = squirrel.getInventoryHandler().getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ToolCore tool) {
                tool.onUpdate(stack, squirrel.world, squirrel, slot, stack == squirrel.getHeldItemMainhand());
            }
        }
    }

    public static int getLootingLevel(@NotNull ItemStack stack) {
        return TinkerModifiers.modLuck.getLuckLevel(stack);
    }

    public static boolean attackEntity(@NotNull MagicalSquirrelEntity squirrel, @NotNull Entity target) {
        ItemStack stack = squirrel.getHeldItemMainhand();
        if (squirrel.world.isRemote || !canUseMeleeWeapon(stack)) return false;
        ToolCore tool = (ToolCore) stack.getItem();
        if (!(tool instanceof Scythe)) return attackTarget(squirrel, target, stack, tool);

        int width = 1;
        int height = 1;
        if (TinkerModifiers.modHarvestWidth != null && TinkerUtil.hasModifier(TagUtil.getTagSafe(stack), TinkerModifiers.modHarvestWidth.getIdentifier())) {
            width++;
        }
        if (TinkerModifiers.modHarvestHeight != null && TinkerUtil.hasModifier(TagUtil.getTagSafe(stack), TinkerModifiers.modHarvestHeight.getIdentifier())) {
            height++;
        }
        squirrel.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);
        boolean hit = false;
        for (Entity nearby : squirrel.world.getEntitiesWithinAABBExcludingEntity(squirrel, target.getEntityBoundingBox().grow(width, height, width))) {
            if (squirrel.isOnSameTeam(nearby)) continue;
            hit |= attackTarget(squirrel, nearby, stack, tool);
        }
        return hit;
    }

    private static boolean attackTarget(
            @NotNull MagicalSquirrelEntity squirrel, @NotNull Entity entity,
            @NotNull ItemStack stack, @NotNull ToolCore tool
    ) {
        if (!entity.canBeAttackedWithItem() || entity.hitByEntity(squirrel) || ToolHelper.isBroken(stack) || stack.isEmpty()) return false;
        EntityLivingBase target = entity instanceof EntityLivingBase living ? living : null;
        List<ITrait> traits = TinkerUtil.getTraitsOrdered(stack);
        float baseDamage = (float) squirrel.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        boolean critical = false;
        for (ITrait trait : traits) {
            if (trait.isCriticalHit(stack, squirrel, target)) critical = true;
        }
        float damage = baseDamage;
        if (target != null) {
            for (ITrait trait : traits) damage = trait.damage(stack, squirrel, target, baseDamage, damage, critical);
        }
        if (critical) damage *= 1.5f;
        damage = ToolHelper.calcCutoffDamage(damage, tool.damageCutoff());

        float baseKnockback = squirrel.isSprinting() ? 1f : 0f;
        float knockback = baseKnockback;
        if (target != null) {
            for (ITrait trait : traits) {
                knockback = trait.knockBack(stack, squirrel, target, damage, baseKnockback, knockback, critical);
            }
        }

        float oldHealth = 0f;
        double oldMotionX = entity.motionX;
        double oldMotionY = entity.motionY;
        double oldMotionZ = entity.motionZ;
        if (target != null) {
            oldHealth = target.getHealth();
            int hurtResistantTime = target.hurtResistantTime;
            for (ITrait trait : traits) {
                trait.onHit(stack, squirrel, target, damage, critical);
                target.hurtResistantTime = hurtResistantTime;
            }
        }

        // Keep each weapon's own damage implementation, including rapier armor piercing and broadsword sweeps.
        boolean hit = tool.dealDamage(stack, squirrel, entity, damage);
        if (!hit || target == null) return hit;
        float damageDealt = oldHealth - target.getHealth();
        entity.motionX = oldMotionX + (entity.motionX - oldMotionX) * tool.knockback();
        entity.motionY = oldMotionY + (entity.motionY - oldMotionY) * tool.knockback() / 3D;
        entity.motionZ = oldMotionZ + (entity.motionZ - oldMotionZ) * tool.knockback();
        if (knockback > 0f) {
            float yaw = squirrel.rotationYaw * (float) Math.PI / 180f;
            entity.addVelocity(-MathHelper.sin(yaw) * knockback * 0.5f, 0.1D, MathHelper.cos(yaw) * knockback * 0.5f);
            squirrel.motionX *= 0.6D;
            squirrel.motionZ *= 0.6D;
            squirrel.setSprinting(false);
        }
        if (entity instanceof EntityPlayerMP && entity.velocityChanged) {
            TinkerNetwork.sendPacket(entity, new SPacketEntityVelocity(entity));
            entity.velocityChanged = false;
        }
        squirrel.setLastAttackedEntity(target);
        for (ITrait trait : traits) trait.afterHit(stack, squirrel, target, damageDealt, critical, true);
        tool.hitEntity(stack, target, squirrel);

        float durabilityDamage = Math.max(1f, damage / 10f);
        if (!tool.hasCategory(Category.WEAPON)) durabilityDamage *= 2f;
        ToolHelper.damageTool(stack, (int) durabilityDamage, squirrel);
        if (ToolHelper.isBroken(stack)) {
            squirrel.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.8f, 0.8f + squirrel.getRNG().nextFloat() * 0.4f);
            squirrel.resetActiveHand();
            if (Config.vanillaToolBreaking) stack.shrink(1);
        }
        if (stack.isEmpty()) squirrel.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        return true;
    }

    //-----ranged weapons-----
    public static boolean isBow(@NotNull ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof BowCore && !(stack.getItem() instanceof CrossBow);
    }

    public static boolean isCrossbow(@NotNull ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof CrossBow;
    }

    public static boolean isShuriken(@NotNull ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof Shuriken;
    }

    public static boolean isCrossbowLoaded(@NotNull ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof CrossBow crossbow && crossbow.isLoaded(stack);
    }

    public static boolean isChargingCrossbow(@NotNull EntityLivingBase entity) {
        return entity.isHandActive() && isCrossbow(entity.getActiveItemStack()) && !isCrossbowLoaded(entity.getActiveItemStack());
    }

    @NotNull
    public static RiftLibInventoryHandler.ItemSearchResult findRangedAmmunition(@NotNull MagicalSquirrelEntity squirrel, @NotNull ItemStack weapon) {
        return squirrel.getInventoryHandler().findItem(RiftLibInventoryHandler.ItemSearchDirection.LAST_TO_FIRST, stack -> {
            if (stack.isEmpty()) return false;
            boolean matches;
            if (isCrossbow(weapon)) matches = stack.getItem() instanceof Bolt;
            else matches = stack.getItem() instanceof Arrow || stack.getItem() instanceof ItemArrow;
            return matches && (!(stack.getItem() instanceof IAmmo ammo) || ammo.getCurrentAmmo(stack) > 0);
        });
    }

    public static boolean canUseRangedWeapon(@NotNull MagicalSquirrelEntity squirrel) {
        ItemStack stack = squirrel.getHeldItemMainhand();
        return SquirrelWitcheryConfig.magicalSquirrelBehavior.canUseModdedRangedWeapons && isUsableRangedWeapon(stack)
                && (isShuriken(stack) || findRangedAmmunition(squirrel, stack).successful());
    }

    public static boolean isUsableRangedWeapon(@NotNull ItemStack stack) {
        if (!(isBow(stack) || isCrossbow(stack) || isShuriken(stack)) || !stack.hasTagCompound() || ToolHelper.isBroken(stack)) return false;
        return !(stack.getItem() instanceof Shuriken shuriken) || shuriken.getCurrentAmmo(stack) > 0;
    }

    public static int getDrawTicks(@NotNull ItemStack stack) {
        if (!(stack.getItem() instanceof BowCore bow)) return 0;
        return Math.max(5, (int) Math.ceil(bow.getDrawTime() / Math.max(0.001D, ProjectileLauncherNBT.from(stack).drawSpeed)));
    }

    public static boolean loadCrossbow(@NotNull MagicalSquirrelEntity squirrel) {
        ItemStack stack = squirrel.getHeldItemMainhand();
        if (squirrel.world.isRemote || !canUseRangedWeapon(squirrel) || !(stack.getItem() instanceof CrossBow crossbow)
                || crossbow.isLoaded(stack) || !squirrel.isHandActive() || squirrel.getActiveItemStack() != stack
                || squirrel.getItemInUseMaxCount() < getDrawTicks(stack)
        ) {
            return false;
        }
        crossbow.setLoaded(stack, true);
        squirrel.playSound(slimeknights.tconstruct.common.Sounds.crossbow_reload, 1.5f, 0.9f + squirrel.getRNG().nextFloat() * 0.1f);
        squirrel.resetActiveHand();
        return true;
    }

    public static boolean shootRangedWeapon(@NotNull MagicalSquirrelEntity squirrel, @NotNull EntityLivingBase target) {
        if (squirrel.world.isRemote || !canUseRangedWeapon(squirrel)) return false;
        ItemStack weapon = squirrel.getHeldItemMainhand();
        boolean throwing = isShuriken(weapon);
        if (!throwing && (!squirrel.isHandActive() || squirrel.getActiveItemStack() != weapon)) return false;
        if (isBow(weapon) && squirrel.getItemInUseMaxCount() < getDrawTicks(weapon)) return false;
        if (isCrossbow(weapon) && (!isCrossbowLoaded(weapon) || squirrel.getItemInUseMaxCount() < 10)) return false;

        ItemStack ammunition;
        if (throwing) ammunition = weapon;
        else ammunition = findRangedAmmunition(squirrel, weapon).foundStack();
        ItemStack projectileStack = ammunition.copy();
        float speed = switch (weapon.getItem()) {
            case CrossBow crossbow -> 7f;
            case LongBow longbow -> 5.5f;
            case BowCore bow -> 3f;
            default -> 2.1f;
        };
        float inaccuracy = 0f;
        int projectileCount = 1;
        boolean consumeAmmoPerProjectile = true;
        boolean consumeDurabilityPerProjectile = true;
        float bonusInaccuracy = 0f;
        if (!throwing) {
            speed *= Math.max(0.001f, ProjectileLauncherNBT.from(weapon).range);
            if (weapon.getItem() instanceof LongBow) inaccuracy = 1.2f;
            else if (isBow(weapon)) inaccuracy = 1f;
            // The native bow event has a player-only shooter field; native ammo traits also support a null player.
            TinkerToolEvent.OnBowShoot event = TinkerToolEvent.OnBowShoot.fireEvent(weapon, ammunition, null, squirrel.getItemInUseMaxCount(), inaccuracy);
            projectileCount = event.projectileCount;
            consumeAmmoPerProjectile = event.consumeAmmoPerProjectile;
            consumeDurabilityPerProjectile = event.consumeDurabilityPerProjectile;
            bonusInaccuracy = event.bonusInaccuracy;
            inaccuracy = event.getBaseInaccuracy();
        }

        boolean fired = false;
        for (int index = 0; index < projectileCount; index++) {
            boolean consumeAmmo = !fired || consumeAmmoPerProjectile;
            ItemStack consumedStack = ammunition.copy();
            boolean usedAmmo = false;
            if (consumeAmmo) {
                if (consumedStack.getItem() instanceof IAmmo ammo) {
                    if (ammo.getCurrentAmmo(consumedStack) <= 0) break;
                    usedAmmo = ammo.useAmmo(consumedStack, squirrel);
                }
                else {
                    if (consumedStack.isEmpty()) break;
                    consumedStack.shrink(1);
                    usedAmmo = true;
                }
            }

            EntityArrow projectile;
            if (projectileStack.getItem() instanceof ProjectileCore ammo) {
                ItemStack reference = projectileStack.copy();
                reference.setCount(1);
                ammo.setAmmo(usedAmmo ? 1 : 0, reference);
                ToolHelper.unbreakTool(reference);
                EntityProjectileBase tinkersProjectile = switch (ammo) {
                    case Bolt bolt -> new slimeknights.tconstruct.tools.common.entity.EntityBolt(squirrel.world);
                    case Shuriken shuriken -> new slimeknights.tconstruct.tools.common.entity.EntityShuriken(squirrel.world);
                    default -> new slimeknights.tconstruct.tools.common.entity.EntityArrow(squirrel.world);
                };
                tinkersProjectile.tinkerProjectile.setItemStack(reference);
                tinkersProjectile.tinkerProjectile.setLaunchingStack(weapon.copy());
                tinkersProjectile.tinkerProjectile.setPower(1f);
                projectile = tinkersProjectile;
            }
            else if (projectileStack.getItem() instanceof ItemArrow arrow) {
                projectile = arrow.createArrow(squirrel.world, projectileStack, squirrel);
            }
            else break;

            projectile.shootingEntity = squirrel;
            projectile.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
            if (!usedAmmo && !(projectile instanceof EntityProjectileBase)) projectile.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
            projectile.setLocationAndAngles(squirrel.posX, squirrel.posY + squirrel.getEyeHeight(), squirrel.posZ, squirrel.rotationYaw, squirrel.rotationPitch);
            double deltaX = target.posX - projectile.posX;
            double deltaZ = target.posZ - projectile.posZ;
            double deltaY = target.getEntityBoundingBox().minY + target.height / 3D - projectile.posY;
            double flightTicks = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) / speed;
            double gravity = 0.05D;
            if (projectile instanceof EntityProjectileBase tinkersProjectile) gravity = tinkersProjectile.getGravity();
            float shotInaccuracy = inaccuracy;
            if (index > 0) shotInaccuracy += bonusInaccuracy;
            if (projectileStack.getItem() instanceof Shuriken) shotInaccuracy *= ProjectileNBT.from(projectileStack).accuracy;
            else if (projectileStack.getItem() instanceof ProjectileCore) {
                shotInaccuracy -= (1f - 1f / Math.max(0.001f, ProjectileNBT.from(projectileStack).accuracy)) * speed / 2f;
            }
            projectile.shoot(deltaX, deltaY + gravity * flightTicks * flightTicks / 2D, deltaZ, speed, shotInaccuracy);
            if (projectile instanceof EntityProjectileBase tinkersProjectile) {
                for (IProjectileTrait trait : tinkersProjectile.tinkerProjectile.getProjectileTraits()) {
                    trait.onLaunch(tinkersProjectile, squirrel.world, squirrel);
                }
            }
            if (!throwing) projectile.setIsCritical(true);
            if (!ProjectileEvent.OnLaunch.fireEvent(projectile, weapon, squirrel) || !squirrel.world.spawnEntity(projectile)) continue;

            if (consumeAmmo) {
                ammunition.setTagCompound(consumedStack.getTagCompound());
                ammunition.setItemDamage(consumedStack.getItemDamage());
                ammunition.setCount(consumedStack.getCount());
            }
            if (!throwing && (!fired || consumeDurabilityPerProjectile)) ToolHelper.damageTool(weapon, 1, squirrel);
            fired = true;
        }
        if (fired) {
            if (weapon.getItem() instanceof CrossBow crossbow) crossbow.setLoaded(weapon, false);
            squirrel.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1f, 0.8f + squirrel.getRNG().nextFloat() * 0.2f);
            if (throwing) squirrel.swingArm(EnumHand.MAIN_HAND);
            squirrel.resetActiveHand();
        }
        return fired;
    }

    public static void recoverProjectiles(@NotNull MagicalSquirrelEntity squirrel) {
        if (squirrel.world.isRemote || !squirrel.isEntityAlive()) return;
        for (EntityProjectileBase projectile : squirrel.world.getEntitiesWithinAABB(EntityProjectileBase.class, squirrel.getEntityBoundingBox().grow(1D))) {
            if (projectile.shootingEntity != squirrel || !projectile.inGround || projectile.arrowShake > 0
                    || projectile.pickupStatus != EntityArrow.PickupStatus.ALLOWED || !projectile.tinkerProjectile.pickup(squirrel, false)
            ) {
                continue;
            }
            squirrel.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2f, 1f);
            squirrel.onItemPickup(projectile, 1);
            projectile.setDead();
        }
    }
}
