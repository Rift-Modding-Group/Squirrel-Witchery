package anightdazingzoroark.squirrelwitchery;

import anightdazingzoroark.riftlib.inventory.RiftLibInventoryHandler;
import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEntity;
import anightdazingzoroark.squirrelwitchery.server.entity.ai.MagicalSquirrelAttackAI;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityBolt;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityBoomerang;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrownJavelin;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrownWeapon;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrowingAxe;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrowingKnife;
import com.oblivioussp.spartanweaponry.init.EnchantmentRegistrySW;
import com.oblivioussp.spartanweaponry.init.ItemRegistrySW;
import com.oblivioussp.spartanweaponry.init.SoundRegistry;
import com.oblivioussp.spartanweaponry.item.ItemBolt;
import com.oblivioussp.spartanweaponry.item.ItemCrossbow;
import com.oblivioussp.spartanweaponry.item.ItemBoomerang;
import com.oblivioussp.spartanweaponry.item.ItemJavelin;
import com.oblivioussp.spartanweaponry.item.ItemThrowingAxe;
import com.oblivioussp.spartanweaponry.item.ItemThrowingKnife;
import com.oblivioussp.spartanweaponry.item.ItemThrowingWeapon;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import com.oblivioussp.spartanweaponry.util.Quaternion;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.UUID;

public class SpartanWeaponryUtils {
    //-----crossbows-----
    public static boolean isCrossbow(@NotNull ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemCrossbow;
    }

    public static boolean isCrossbowLoaded(@NotNull ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemCrossbow crossbow && crossbow.isLoaded(stack);
    }

    public static boolean isChargingCrossbow(@NotNull EntityLivingBase entity) {
        return entity.isHandActive() && isCrossbow(entity.getActiveItemStack()) && !isCrossbowLoaded(entity.getActiveItemStack());
    }

    public static boolean isAimingCrossbow(@NotNull EntityLivingBase entity) {
        return entity.isHandActive() && isCrossbowLoaded(entity.getActiveItemStack());
    }

    @NotNull
    public static RiftLibInventoryHandler.ItemSearchResult findCrossbowAmmunition(@NotNull MagicalSquirrelEntity squirrel) {
        return squirrel.getInventoryHandler().findItem(
                RiftLibInventoryHandler.ItemSearchDirection.LAST_TO_FIRST,
                stack -> !stack.isEmpty() && stack.getItem() instanceof ItemBolt
        );
    }

    public static boolean canUseCrossbow(@NotNull MagicalSquirrelEntity squirrel) {
        ItemStack crossbow = squirrel.getHeldItemMainhand();
        return SquirrelWitcheryConfig.magicalSquirrelBehavior.canUseModdedRangedWeapons
                && isCrossbow(crossbow)
                && (isCrossbowLoaded(crossbow) || findCrossbowAmmunition(squirrel).successful()
                || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, crossbow) > 0);
    }

    public static int getCrossbowAimTicks(@NotNull ItemStack stack) {
        if (!isCrossbow(stack)) return 0;
        return ((ItemCrossbow) stack.getItem()).getAimTicks(stack);
    }

    public static int getCrossbowCooldownTicks() {
        return Math.max(0, ConfigHandler.crossbowTicksCooldown);
    }

    public static boolean loadCrossbow(@NotNull MagicalSquirrelEntity squirrel) {
        if (squirrel.world.isRemote || !canUseCrossbow(squirrel)) return false;
        ItemStack crossbow = squirrel.getHeldItemMainhand();
        if (isCrossbowLoaded(crossbow) || !squirrel.isHandActive()
                || squirrel.getActiveItemStack() != crossbow || squirrel.getItemInUseCount() > 0) return false;

        RiftLibInventoryHandler.ItemSearchResult ammunition = findCrossbowAmmunition(squirrel);
        ItemStack boltStack = ammunition.foundStack();
        if (boltStack.isEmpty()) boltStack = new ItemStack(ItemRegistrySW.bolt);

        // Spartan's Infinity enchantment only preserves ordinary, untipped bolts.
        boolean infinite = boltStack.getItem().getClass() == ItemBolt.class
                && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, crossbow) > 0;
        ItemStack loadedBolts = boltStack.copy();
        int count = 1;
        if (EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.CROSSBOW_SPREADSHOT, crossbow) > 0) count = 3;
        loadedBolts.setCount(count);

        // Use Spartan's own root tags so players can reuse a squirrel's loaded crossbow.
        NBTTagCompound tag = crossbow.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            crossbow.setTagCompound(tag);
        }
        tag.setTag(ItemCrossbow.nbtAmmoStack, loadedBolts.writeToNBT(new NBTTagCompound()));
        tag.setBoolean(ItemCrossbow.NBT_IS_LOADED, true);
        if (!infinite) squirrel.getInventoryHandler().extractItem(ammunition.slot(), 1, false);
        squirrel.playSound(SoundRegistry.CROSSBOW_LOAD, 1f, 1f / (squirrel.getRNG().nextFloat() * 0.4f + 1.2f) * 0.5f);
        return true;
    }

    public static boolean shootCrossbow(@NotNull MagicalSquirrelEntity squirrel, @NotNull EntityLivingBase target) {
        if (squirrel.world.isRemote || !canUseCrossbow(squirrel)) return false;
        ItemStack weapon = squirrel.getHeldItemMainhand();
        if (!isCrossbowLoaded(weapon) || !squirrel.isHandActive()
                || squirrel.getActiveItemStack() != weapon
                || squirrel.getItemInUseMaxCount() < getCrossbowAimTicks(weapon)
        ) {
            return false;
        }

        ItemCrossbow crossbow = (ItemCrossbow) weapon.getItem();
        NBTTagCompound tag = weapon.getTagCompound();
        if (tag == null) return false;
        ItemStack ammunition = new ItemStack(tag.getCompoundTag(ItemCrossbow.nbtAmmoStack));
        if (ammunition.isEmpty()) {
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, weapon) <= 0) return false;
            ammunition = new ItemStack(ItemRegistrySW.bolt);
        }
        if (!(ammunition.getItem() instanceof ItemBolt boltItem)) return false;

        boolean infinite = boltItem.getClass() == ItemBolt.class && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, weapon) > 0;
        int projectileCount = ammunition.getCount() > 1 ? 3 : 1;
        boolean spawned = false;
        for (int index = 0; index < projectileCount; index++) {
            EntityBolt bolt = boltItem.createBolt(squirrel.world, ammunition, squirrel);
            bolt.setIsCritical(true);

            float velocity = crossbow.getBoltSpeed() * 3f;
            double deltaX = target.posX - bolt.posX;
            double deltaZ = target.posZ - bolt.posZ;
            double deltaY = target.getEntityBoundingBox().minY + target.height / 3D - bolt.posY;
            double flightTicks = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) / (velocity * bolt.getRangeMultiplier());
            Vec3d direction = new Vec3d(deltaX, deltaY + 0.025D * flightTicks * flightTicks, deltaZ).normalize();
            if (index > 0) {
                double horizontalLength = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
                Vec3d up = new Vec3d(1D, 0D, 0D);
                if (horizontalLength > 0.000001D) {
                    up = new Vec3d(-direction.y * direction.x / horizontalLength, horizontalLength,
                            -direction.y * direction.z / horizontalLength);
                }
                float angle = index == 1 ? -10f : 10f;
                direction = new Quaternion(up, angle, true).transformVector(direction);
            }
            bolt.shoot(direction.x, direction.y, direction.z, velocity, 0f);

            int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, weapon);
            if (power > 0) bolt.setDamage(bolt.getDamage() + power * 0.5D + 0.5D);
            int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, weapon);
            if (punch > 0) bolt.setKnockbackStrength(punch);
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, weapon) > 0) bolt.setFire(100);
            bolt.pickupStatus = infinite || index > 0 ? EntityArrow.PickupStatus.CREATIVE_ONLY : EntityArrow.PickupStatus.ALLOWED;
            spawned |= squirrel.world.spawnEntity(bolt);
        }
        if (!spawned) return false;

        tag.setBoolean(ItemCrossbow.NBT_IS_LOADED, false);
        tag.setTag(ItemCrossbow.nbtAmmoStack, new NBTTagCompound());
        weapon.damageItem(projectileCount, squirrel);
        squirrel.playSound(SoundRegistry.CROSSBOW_FIRE, 1f, 1f / (squirrel.getRNG().nextFloat() * 0.4f + 1.2f) + crossbow.getBoltSpeed() * 0.5f);
        squirrel.resetActiveHand();
        return true;
    }

    //-----throwing weapons-----
    public static boolean isThrowingWeapon(@NotNull ItemStack stack) {
        return stack.getItem() instanceof ItemThrowingKnife || stack.getItem() instanceof ItemThrowingAxe
                || stack.getItem() instanceof ItemJavelin || stack.getItem() instanceof ItemBoomerang;
    }

    public static boolean canUseThrowingWeapon(@NotNull MagicalSquirrelEntity squirrel) {
        ItemStack stack = squirrel.getHeldItemMainhand();
        if (!SquirrelWitcheryConfig.magicalSquirrelBehavior.canUseModdedRangedWeapons || !isThrowingWeapon(stack)) return false;
        ItemThrowingWeapon item = (ItemThrowingWeapon) stack.getItem();
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return true;
        if (!tag.hasKey(ItemThrowingWeapon.NBT_AMMO_USED) && tag.hasKey(ItemThrowingWeapon.NBT_AMMO)) {
            return tag.getInteger(ItemThrowingWeapon.NBT_AMMO) > 0;
        }
        return tag.getInteger(ItemThrowingWeapon.NBT_AMMO_USED) < item.getMaxAmmo(stack);
    }

    public static int getThrowingChargeTicks(@NotNull ItemStack stack) {
        if (!isThrowingWeapon(stack)) return 0;
        // Spartan's boomerang release uses a fixed five-tick charge.
        if (stack.getItem() instanceof ItemBoomerang) return 5;
        return Math.max(1, ((ItemThrowingWeapon) stack.getItem()).getMaxChargeTicks(stack));
    }

    public static double getThrowingRange(@NotNull ItemStack stack) {
        if (!isThrowingWeapon(stack)) return MagicalSquirrelAttackAI.SHOT_RANGE;
        if (stack.getItem() instanceof ItemBoomerang) {
            int range = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_RANGE, stack);
            return Math.min(MagicalSquirrelAttackAI.SHOT_RANGE, 5D + range * 3D);
        }
        return MagicalSquirrelAttackAI.SHOT_RANGE;
    }

    public static boolean throwWeapon(@NotNull MagicalSquirrelEntity squirrel, @NotNull EntityLivingBase target) {
        if (squirrel.world.isRemote || !canUseThrowingWeapon(squirrel)) return false;

        ItemStack stack = squirrel.getHeldItemMainhand();
        if (!squirrel.isHandActive() || squirrel.getActiveItemStack() != stack || squirrel.getItemInUseMaxCount() < getThrowingChargeTicks(stack)) {
            return false;
        }

        ItemThrowingWeapon item = (ItemThrowingWeapon) stack.getItem();
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        if (!tag.hasKey(ItemThrowingWeapon.NBT_AMMO_USED)) {
            int used = 0;
            if (tag.hasKey(ItemThrowingWeapon.NBT_AMMO)) {
                used = Math.clamp(item.getMaxAmmo(stack) - tag.getInteger(ItemThrowingWeapon.NBT_AMMO), 0, item.getMaxAmmo(stack));
                tag.removeTag(ItemThrowingWeapon.NBT_AMMO);
            }
            tag.setInteger(ItemThrowingWeapon.NBT_AMMO_USED, used);
        }
        if (!tag.hasUniqueId(ItemThrowingWeapon.NBT_UUID)) {
            tag.setUniqueId(ItemThrowingWeapon.NBT_UUID, UUID.randomUUID());
            tag.setBoolean(ItemThrowingWeapon.NBT_ORIGINAL, true);
        }

        EntityThrownWeapon projectile;
        float velocity;
        switch (item) {
            case ItemThrowingKnife ignored -> {
                projectile = new EntityThrowingKnife(squirrel.world, squirrel);
                velocity = 2.1f;
            }
            case ItemThrowingAxe ignored -> {
                projectile = new EntityThrowingAxe(squirrel.world, squirrel);
                velocity = 1.75f;
            }
            case ItemJavelin ignored -> {
                projectile = new EntityThrownJavelin(squirrel.world, squirrel);
                velocity = 2.4f;
            }
            case ItemBoomerang ignored -> {
                EntityBoomerang boomerang = new EntityBoomerang(squirrel.world, squirrel);
                boomerang.setMaxDistance(5D + EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_RANGE, stack) * 3D);
                boomerang.setPosition(squirrel.posX, squirrel.posY + squirrel.getEyeHeight() * 0.9D - 0.1D, squirrel.posZ);
                projectile = boomerang;
                velocity = 2f;
            }
            default -> {
                return false;
            }
        }
        projectile.setWeapon(stack);
        velocity *= 1f + EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_RANGE, stack) * 0.2f;
        double deltaX = target.posX - projectile.posX;
        double deltaZ = target.posZ - projectile.posZ;
        double deltaY = target.getEntityBoundingBox().minY + target.height / 3D - projectile.posY;
        double flightTicks = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) / velocity;
        if (!(projectile instanceof EntityBoomerang)) deltaY += 0.025D * flightTicks * flightTicks;
        projectile.shoot(deltaX, deltaY, deltaZ, velocity, 0.5f);
        projectile.setDamage((item.getDirectAttackDamage() + 1D) * item.getThrownDamageMultiplier());
        int power = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_DAMAGE, stack);
        if (power > 0) projectile.setDamage(projectile.getDamage() + power * 0.5D + 0.5D);
        projectile.setKnockbackStrength(EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, stack));
        if (EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_FIRE, stack) > 0) projectile.setFire(100);
        projectile.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
        if (!squirrel.world.spawnEntity(projectile)) return false;

        int used = tag.getInteger(ItemThrowingWeapon.NBT_AMMO_USED) + 1;
        tag.setInteger(ItemThrowingWeapon.NBT_AMMO_USED, used);
        stack.setItemDamage(0);
        if (used >= item.getMaxAmmo(stack) && !tag.getBoolean(ItemThrowingWeapon.NBT_ORIGINAL)) stack.shrink(1);
        squirrel.playSound(projectile instanceof EntityBoomerang ? SoundRegistry.BOOMERANG_THROW : SoundRegistry.THROWING_WEAPON_THROW,
                0.5f, 0.4f / (squirrel.getRNG().nextFloat() * 0.4f + 0.8f));
        squirrel.resetActiveHand();
        squirrel.setUsingBow(false);
        squirrel.swingArm(EnumHand.MAIN_HAND);
        return true;
    }

    public static boolean catchThrownWeapon(@NotNull MagicalSquirrelEntity squirrel, @NotNull EntityArrow arrow) {
        if (squirrel.world.isRemote) return false;
        if (!(arrow instanceof EntityThrownWeapon thrown) || thrown.isDead || thrown.shootingEntity != squirrel
                || thrown.pickupStatus != EntityArrow.PickupStatus.ALLOWED || thrown.arrowShake > 0
                || thrown.ticksExisted <= 5
                || !(thrown.inGround || thrown.isReturning() || thrown instanceof EntityBoomerang)) return false;
        ItemStack weapon = thrown.getWeaponStack();
        NBTTagCompound weaponTag = weapon.getTagCompound();
        if (weapon.isEmpty() || weaponTag == null || !weaponTag.hasUniqueId(ItemThrowingWeapon.NBT_UUID)) return false;
        UUID weaponId = weaponTag.getUniqueId(ItemThrowingWeapon.NBT_UUID);
        if (weaponId == null) return false;

        for (int slot = 0; slot < squirrel.getInventoryHandler().getSlots(); slot++) {
            ItemStack original = squirrel.getInventoryHandler().getStackInSlot(slot);
            NBTTagCompound originalTag = original.getTagCompound();
            if (original.isEmpty() || !ItemStack.areItemsEqualIgnoreDurability(original, weapon)
                    || originalTag == null || !originalTag.hasUniqueId(ItemThrowingWeapon.NBT_UUID)
                    || !weaponId.equals(originalTag.getUniqueId(ItemThrowingWeapon.NBT_UUID))
                    || !(original.getItem() instanceof ItemThrowingWeapon)
                    || originalTag.getInteger(ItemThrowingWeapon.NBT_AMMO_USED) <= 0) continue;
            int damage = original.getItemDamage() + weapon.getItemDamage();
            if (damage > original.getMaxDamage()) {
                squirrel.renderBrokenItemStack(weapon);
                damage -= original.getMaxDamage() + 1;
            }
            else {
                int used = originalTag.getInteger(ItemThrowingWeapon.NBT_AMMO_USED);
                originalTag.setInteger(ItemThrowingWeapon.NBT_AMMO_USED, used - 1);
            }
            original.setItemDamage(damage);
            squirrel.getInventoryHandler().markDirty();
            squirrel.onItemPickup(thrown, 1);
            thrown.setDead();
            return true;
        }

        ItemStack recovered = weapon.copy();
        EnchantmentHelper.setEnchantments(Collections.emptyMap(), recovered);
        for (int slot = 0; slot < squirrel.getInventoryHandler().getSlots() && !recovered.isEmpty(); slot++) {
            recovered = squirrel.getInventoryHandler().insertItem(slot, recovered, false);
        }
        if (!recovered.isEmpty()) squirrel.entityDropItem(recovered, 0.1f);
        squirrel.onItemPickup(thrown, 1);
        thrown.setDead();
        return true;
    }

    public static void recoverThrownWeapons(@NotNull MagicalSquirrelEntity squirrel) {
        if (squirrel.world.isRemote || !squirrel.isEntityAlive()) return;
        for (EntityArrow arrow : squirrel.world.getEntitiesWithinAABB(EntityArrow.class, squirrel.getEntityBoundingBox().grow(1D))) {
            catchThrownWeapon(squirrel, arrow);
        }
    }
}
