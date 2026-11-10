package anightdazingzoroark.squirrelwitchery.server.entity.ai;

import anightdazingzoroark.riftlib.inventory.RiftLibInventoryHandler;
import anightdazingzoroark.squirrelwitchery.SquirrelWitcheryConfig;
import anightdazingzoroark.squirrelwitchery.compat.SpartanWeaponryCompat;
import anightdazingzoroark.squirrelwitchery.compat.TinkersConstructCompat;
import anightdazingzoroark.squirrelwitchery.MagicalSquirrelCastingUtils;
import anightdazingzoroark.squirrelwitchery.server.entity.MagicalSquirrelEntity;
import anightdazingzoroark.squirrelwitchery.server.items.WitchShotgunItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.items.casters.ItemFocus;

import java.util.function.Function;

public enum MagicalSquirrelWeaponType {
    //melee attacks
    MELEE(entity -> new MagicalSquirrelAttackBehavior(entity) {
        @Nullable
        private Path attackPath;
        @Nullable
        private Vec3d lastPathTarget;
        private int nextPathUpdateTick;
        private int nextAttackTick;

        @Override
        public boolean canUse() {
            return !this.squirrel.getHeldItemMainhand().isEmpty();
        }

        @Override
        public boolean canStart(@NotNull EntityLivingBase target) {
            Path path = this.squirrel.getNavigator().getPathToEntityLiving(target);
            this.attackPath = path;
            double reachSquared = this.squirrel.width * 2D * (this.squirrel.width * 2D) + target.width;
            return path != null || this.squirrel.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ) <= reachSquared;
        }

        @Override
        public void start() {
            this.squirrel.getNavigator().setPath(this.attackPath, 1D);
        }

        @Override
        public void stop() {
            this.attackPath = null;
            this.lastPathTarget = null;
            this.nextPathUpdateTick = 0;
            this.nextAttackTick = 0;
        }

        @Override
        public void move(@NotNull EntityLivingBase target) {
            if (this.squirrel.ticksExisted < this.nextPathUpdateTick) return;
            if (this.lastPathTarget != null && target.getDistanceSq(this.lastPathTarget.x, this.lastPathTarget.y, this.lastPathTarget.z) < 1D
                    && this.squirrel.getRNG().nextFloat() >= 0.05f
            ) {
                return;
            }

            this.lastPathTarget = new Vec3d(target.posX, target.getEntityBoundingBox().minY, target.posZ);
            int pathUpdateDelay = 4 + this.squirrel.getRNG().nextInt(7);
            double distanceSquared = this.squirrel.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
            if (distanceSquared > 1024D) pathUpdateDelay += 10;
            else if (distanceSquared > 256D) pathUpdateDelay += 5;
            if (!this.squirrel.getNavigator().tryMoveToEntityLiving(target, 1D)) pathUpdateDelay += 15;
            this.nextPathUpdateTick = this.squirrel.ticksExisted + pathUpdateDelay;
        }

        @Override
        public void update(@NotNull EntityLivingBase target) {
            this.attack(target);
        }

        @Override
        public void attack(@NotNull EntityLivingBase target) {
            double reachSquared = this.squirrel.width * 2D * (this.squirrel.width * 2D) + target.width;
            if (this.squirrel.ticksExisted < this.nextAttackTick
                    || this.squirrel.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ) > reachSquared
            ) {
                return;
            }

            int attackCooldown = 20;
            if (Loader.isModLoaded("tconstruct") && TinkersConstructCompat.isMeleeWeapon(this.squirrel.getHeldItemMainhand())) {
                attackCooldown = TinkersConstructCompat.getAttackCooldownTicks(this.squirrel);
            }
            this.nextAttackTick = this.squirrel.ticksExisted + attackCooldown;
            this.squirrel.swingArm(EnumHand.MAIN_HAND);
            this.squirrel.attackEntityAsMob(target);
        }

        @Override
        public void finishUsing() {}
    }),
    //bows, affects anything that extends ItemBow
    BOW(entity -> new MagicalSquirrelRangedAttackBehavior(entity) {
        private int nextShotTick;

        @Override
        public boolean canUse() {
            return SquirrelWitcheryConfig.magicalSquirrelBehavior.canUseBows && this.findAmmunition().successful();
        }

        @Override
        public boolean canStart(@NotNull EntityLivingBase target) {
            return true;
        }

        @Override
        public void start() {
            this.squirrel.setUsingBow(true);
            this.nextShotTick = this.squirrel.ticksExisted + 60;
        }

        @Override
        public void stop() {
            this.nextShotTick = 0;
        }

        @Override
        public void update(@NotNull EntityLivingBase target) {
            int remainingTicks = this.nextShotTick - this.squirrel.ticksExisted;
            if (remainingTicks <= 20) this.squirrel.setActiveHand(EnumHand.MAIN_HAND);
            else this.squirrel.resetActiveHand();

            if (remainingTicks <= 0) {
                this.squirrel.attackEntityWithRangedAttack(target, 1f);
                this.nextShotTick = this.squirrel.ticksExisted + 60;
            }
        }

        @NotNull
        private RiftLibInventoryHandler.ItemSearchResult findAmmunition() {
            return this.squirrel.getInventoryHandler().findItem(
                    RiftLibInventoryHandler.ItemSearchDirection.LAST_TO_FIRST,
                    stack -> !stack.isEmpty() && this.squirrel.getHeldItemMainhand().getItem() instanceof ItemBow bow && bow.isArrow(stack)
            );
        }

        @Override
        public void attack(@NotNull EntityLivingBase target) {
            ItemStack weapon = this.squirrel.getHeldItemMainhand();
            if (!(weapon.getItem() instanceof ItemBow bow) || this.squirrel.getItemInUseMaxCount() < 20) return;

            RiftLibInventoryHandler.ItemSearchResult ammunition = this.findAmmunition();
            if (!ammunition.successful()) return;
            ItemStack arrowStack = ammunition.foundStack();
            if (!(arrowStack.getItem() instanceof ItemArrow arrowItem)) return;

            EntityArrow arrow = bow.customizeArrow(arrowItem.createArrow(this.squirrel.world, arrowStack, this.squirrel));
            double deltaX = target.posX - arrow.posX;
            double deltaZ = target.posZ - arrow.posZ;
            double deltaY = target.getEntityBoundingBox().minY + target.height / 3D - arrow.posY;
            double flightTicks = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) / 3D;
            arrow.shoot(deltaX, deltaY + 0.025D * flightTicks * flightTicks, deltaZ, 3f, 1f);

            int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, weapon);
            if (power > 0) arrow.setDamage(arrow.getDamage() + power * 0.5D + 0.5D);
            int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, weapon);
            if (punch > 0) arrow.setKnockbackStrength(punch);
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, weapon) > 0) arrow.setFire(100);
            boolean infinite = arrowStack.getItem() == Items.ARROW && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, weapon) > 0;
            arrow.pickupStatus = infinite ? EntityArrow.PickupStatus.CREATIVE_ONLY : EntityArrow.PickupStatus.ALLOWED;
            if (!this.squirrel.world.spawnEntity(arrow)) return;
            this.recordRangedShot(target);

            if (!infinite) this.squirrel.getInventoryHandler().extractItem(ammunition.slot(), 1, false);
            weapon.damageItem(1, this.squirrel);
            this.squirrel.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1f, 1f / (this.squirrel.getRNG().nextFloat() * 0.4f + 1.2f) + 0.5f);
            this.squirrel.resetActiveHand();
        }

        @Override
        public void finishUsing() {}
    }),
    //witchs shotgun
    WITCHS_SHOTGUN(entity -> new MagicalSquirrelRangedAttackBehavior(entity) {
        private int nextShotTick;

        @Override
        public boolean canUse() {
            return SquirrelWitcheryConfig.magicalSquirrelBehavior.canUseWitchsShotgun
                    && this.squirrel.getHeldItemMainhand().getItem() instanceof WitchShotgunItem;
        }

        @Override
        public boolean canStart(@NotNull EntityLivingBase target) {
            return true;
        }

        @Override
        public void start() {}

        @Override
        public void stop() {}

        @Override
        public void update(@NotNull EntityLivingBase target) {
            if (this.squirrel.ticksExisted >= this.nextShotTick) this.squirrel.attackEntityWithRangedAttack(target, 1f);
        }

        @Override
        public void attack(@NotNull EntityLivingBase target) {
            if (!this.canUse() || this.squirrel.ticksExisted < this.nextShotTick) return;

            Vec3d start = this.squirrel.getPositionEyes(1f);
            double deltaX = target.posX - start.x;
            double deltaY = target.getEntityBoundingBox().minY + target.height / 2D - start.y;
            double deltaZ = target.posZ - start.z;
            float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)));
            float yaw = (float) Math.toDegrees(Math.atan2(-deltaX, deltaZ));
            this.recordRangedShot(target);
            WitchShotgunItem.shoot(this.squirrel, pitch, yaw);
            this.nextShotTick = this.squirrel.ticksExisted + WitchShotgunItem.COOLDOWN_TICKS;
        }

        @Override
        public void finishUsing() {}
    }),
    //spartan weaponry crossbows
    SPARTAN_CROSSBOW(entity -> new MagicalSquirrelRangedAttackBehavior(entity) {
        private int readyTick;

        @Override
        public boolean canUse() {
            return Loader.isModLoaded("spartanweaponry") && SpartanWeaponryCompat.canUseCrossbow(this.squirrel);
        }

        @Override
        public boolean canStart(@NotNull EntityLivingBase target) {
            return true;
        }

        @Override
        public void start() {}

        @Override
        public void stop() {}

        @Override
        public void update(@NotNull EntityLivingBase target) {
            ItemStack crossbow = this.squirrel.getHeldItemMainhand();
            boolean canAttack = this.squirrel.ticksExisted >= this.readyTick && this.canRangedAttack(target);
            if (canAttack) this.squirrel.setActiveHand(EnumHand.MAIN_HAND);
            else this.squirrel.resetActiveHand();

            if (canAttack && SpartanWeaponryCompat.isCrossbowLoaded(crossbow) && this.squirrel.isHandActive()
                    && this.squirrel.getActiveItemStack() == crossbow
                    && this.squirrel.getItemInUseMaxCount() >= SpartanWeaponryCompat.getCrossbowAimTicks(crossbow)
            ) {
                this.squirrel.attackEntityWithRangedAttack(target, 1f);
            }
        }

        @Override
        public void attack(@NotNull EntityLivingBase target) {
            if (this.squirrel.ticksExisted >= this.readyTick && SpartanWeaponryCompat.shootCrossbow(this.squirrel, target)) {
                this.recordRangedShot(target);
            }
        }

        @Override
        public void finishUsing() {
            if (SpartanWeaponryCompat.loadCrossbow(this.squirrel)) {
                this.readyTick = this.squirrel.ticksExisted + SpartanWeaponryCompat.getCrossbowCooldownTicks();
            }
        }
    }),
    //spartan weaponry knives, axes, javelins and boomerangs
    SPARTAN_THROWING_WEAPON(entity -> new MagicalSquirrelRangedAttackBehavior(
            entity, squirrel -> SpartanWeaponryCompat.getThrowingRange(squirrel.getHeldItemMainhand())
    ) {
        private int readyTick;

        @Override
        public boolean canUse() {
            return Loader.isModLoaded("spartanweaponry") && SpartanWeaponryCompat.canUseThrowingWeapon(this.squirrel);
        }

        @Override
        public boolean canStart(@NotNull EntityLivingBase target) {
            return true;
        }

        @Override
        public void start() {}

        @Override
        public void stop() {}

        @Override
        public void update(@NotNull EntityLivingBase target) {
            boolean canAttack = this.squirrel.ticksExisted >= this.readyTick && this.canRangedAttack(target);
            this.squirrel.setUsingBow(canAttack);
            if (!canAttack) {
                this.squirrel.resetActiveHand();
                return;
            }
            this.squirrel.setActiveHand(EnumHand.MAIN_HAND);
            if (this.squirrel.getItemInUseMaxCount() >= SpartanWeaponryCompat.getThrowingChargeTicks(this.squirrel.getHeldItemMainhand())) {
                this.squirrel.attackEntityWithRangedAttack(target, 1f);
            }
        }

        @Override
        public void attack(@NotNull EntityLivingBase target) {
            if (this.squirrel.ticksExisted >= this.readyTick && SpartanWeaponryCompat.throwWeapon(this.squirrel, target)) {
                this.recordRangedShot(target);
                this.readyTick = this.squirrel.ticksExisted + 20;
            }
        }

        @Override
        public void finishUsing() {}
    }),
    TINKERS_BOW(entity -> new MagicalSquirrelRangedAttackBehavior(entity) {
        private int readyTick;

        @Override
        public boolean canUse() {
            return Loader.isModLoaded("tconstruct") && TinkersConstructCompat.isBow(this.squirrel.getHeldItemMainhand())
                    && TinkersConstructCompat.canUseRangedWeapon(this.squirrel);
        }

        @Override
        public boolean canStart(@NotNull EntityLivingBase target) {
            return true;
        }

        @Override
        public void start() {}

        @Override
        public void stop() {
            this.readyTick = 0;
            this.squirrel.setUsingBow(false);
            this.squirrel.resetActiveHand();
        }

        @Override
        public void update(@NotNull EntityLivingBase target) {
            boolean canAttack = this.squirrel.ticksExisted >= this.readyTick
                    && this.canRangedAttack(target);
            this.squirrel.setUsingBow(canAttack);
            if (!canAttack) {
                this.squirrel.resetActiveHand();
                return;
            }
            this.squirrel.setActiveHand(EnumHand.MAIN_HAND);
            if (this.squirrel.getItemInUseMaxCount() >= TinkersConstructCompat.getDrawTicks(this.squirrel.getHeldItemMainhand())) {
                this.squirrel.attackEntityWithRangedAttack(target, 1f);
            }
        }

        @Override
        public void attack(@NotNull EntityLivingBase target) {
            if (this.canUse() && this.squirrel.ticksExisted >= this.readyTick && TinkersConstructCompat.shootBow(this.squirrel, target)) {
                this.recordRangedShot(target);
                this.readyTick = this.squirrel.ticksExisted + 20;
                this.squirrel.setUsingBow(false);
            }
        }

        @Override
        public void finishUsing() {}
    }),
    TINKERS_CROSSBOW(entity -> new MagicalSquirrelRangedAttackBehavior(entity) {
        private int readyTick;

        @Override
        public boolean canUse() {
            return Loader.isModLoaded("tconstruct") && TinkersConstructCompat.isCrossbow(this.squirrel.getHeldItemMainhand())
                    && TinkersConstructCompat.canUseRangedWeapon(this.squirrel);
        }

        @Override
        public boolean canStart(@NotNull EntityLivingBase target) {
            return true;
        }

        @Override
        public void start() {}

        @Override
        public void stop() {
            this.readyTick = 0;
            this.squirrel.setUsingBow(false);
            this.squirrel.resetActiveHand();
        }

        @Override
        public void update(@NotNull EntityLivingBase target) {
            boolean canAttack = this.squirrel.ticksExisted >= this.readyTick && this.canRangedAttack(target);
            this.squirrel.setUsingBow(canAttack && TinkersConstructCompat.isCrossbowLoaded(this.squirrel.getHeldItemMainhand()));
            if (!canAttack) {
                this.squirrel.resetActiveHand();
                return;
            }
            this.squirrel.setActiveHand(EnumHand.MAIN_HAND);
            if (!TinkersConstructCompat.isCrossbowLoaded(this.squirrel.getHeldItemMainhand())) {
                TinkersConstructCompat.loadCrossbow(this.squirrel);
            }
            else if (this.squirrel.getItemInUseMaxCount() >= 10) this.squirrel.attackEntityWithRangedAttack(target, 1f);
        }

        @Override
        public void attack(@NotNull EntityLivingBase target) {
            if (this.canUse() && this.squirrel.ticksExisted >= this.readyTick && TinkersConstructCompat.shootCrossbow(this.squirrel, target)) {
                this.recordRangedShot(target);
                this.readyTick = this.squirrel.ticksExisted + 20;
                this.squirrel.setUsingBow(false);
            }
        }

        @Override
        public void finishUsing() {}
    }),
    TINKERS_SHURIKEN(entity -> new MagicalSquirrelRangedAttackBehavior(entity) {
        private int readyTick;

        @Override
        public boolean canUse() {
            return Loader.isModLoaded("tconstruct") && TinkersConstructCompat.isShuriken(this.squirrel.getHeldItemMainhand())
                    && TinkersConstructCompat.canUseRangedWeapon(this.squirrel);
        }

        @Override
        public boolean canStart(@NotNull EntityLivingBase target) {
            return true;
        }

        @Override
        public void start() {}

        @Override
        public void stop() {
            this.readyTick = 0;
        }

        @Override
        public void update(@NotNull EntityLivingBase target) {
            if (this.squirrel.ticksExisted >= this.readyTick) this.squirrel.attackEntityWithRangedAttack(target, 1f);
        }

        @Override
        public void attack(@NotNull EntityLivingBase target) {
            if (this.canUse() && this.squirrel.ticksExisted >= this.readyTick && TinkersConstructCompat.throwShuriken(this.squirrel, target)) {
                this.recordRangedShot(target);
                this.readyTick = this.squirrel.ticksExisted + 4;
            }
        }

        @Override
        public void finishUsing() {}
    }),
    //anything that allows casting tc spells
    CASTER(entity -> new MagicalSquirrelRangedAttackBehavior(entity, MagicalSquirrelCastingUtils::getAttackRange) {
        private int nextCastTick;
        @Nullable
        private EntityLivingBase healingOwner;
        @Nullable
        private EntityLivingBase healingShotTarget;
        private float healthBeforeHealingShot;

        @Override
        public boolean canUse() {
            if (!SquirrelWitcheryConfig.magicalSquirrelBehavior.canUseCastingItems || !(this.squirrel.getHeldItemMainhand().getItem() instanceof ItemCaster)) {
                this.squirrel.markEquipmentDirty();
                return false;
            }
            boolean usable = MagicalSquirrelCastingUtils.hasSpell(MagicalSquirrelCastingUtils.getOrInstallFocus(
                    this.squirrel.getHeldItemMainhand(), this.squirrel.getInventoryHandler()
            ));
            if (!usable) this.squirrel.markEquipmentDirty();
            return usable;
        }

        @Override
        @Nullable
        public EntityLivingBase getTarget() {
            ItemStack focus = MagicalSquirrelCastingUtils.getOrInstallFocus(this.squirrel.getHeldItemMainhand(), this.squirrel.getInventoryHandler());
            EntityLivingBase owner = this.squirrel.getOwner();
            EntityLivingBase target = MagicalSquirrelCastingUtils.getCastingTarget(
                    ItemFocus.getPackage(focus), owner, this.squirrel.getAttackTarget(), owner != null && owner == this.healingOwner
            );
            this.healingOwner = target == owner ? owner : null;
            return target;
        }

        @Override
        public boolean canStart(@NotNull EntityLivingBase target) {
            return true;
        }

        @Override
        public void start() {}

        @Override
        public void stop() {}

        @Override
        public void update(@NotNull EntityLivingBase target) {
            if (this.squirrel.ticksExisted >= this.nextCastTick) this.squirrel.attackEntityWithRangedAttack(target, 1f);
        }

        @Override
        public void attack(@NotNull EntityLivingBase target) {
            if (!this.canUse() || target != this.getTarget() || this.squirrel.ticksExisted < this.nextCastTick) return;
            this.nextCastTick = this.squirrel.ticksExisted + MagicalSquirrelCastingUtils.cast(this.squirrel, target);
        }

        @Override
        public void finishUsing() {}

        //---ranged attack overrides specific to healing---
        @Override
        public boolean canRangedAttack(@NotNull EntityLivingBase target) {
            //healing restores health instead of producing the damage events used by other ranged attacks
            if (target == this.healingShotTarget && target.getHealth() > this.healthBeforeHealingShot) {
                this.recordRangedHit(target);
                this.healingShotTarget = null;
            }
            return super.canRangedAttack(target);
        }

        @Override
        public boolean recordRangedShot(@NotNull EntityLivingBase target) {
            boolean recorded = super.recordRangedShot(target);
            if (recorded) {
                this.healingShotTarget = target == this.squirrel.getOwner() ? target : null;
                this.healthBeforeHealingShot = target.getHealth();
            }
            return recorded;
        }

        @Override
        public void resetRangedAttack() {
            super.resetRangedAttack();
            this.healingShotTarget = null;
            this.healthBeforeHealingShot = 0f;
        }
    });

    @NotNull
    public final Function<MagicalSquirrelEntity, MagicalSquirrelAttackBehavior> attackBehavior;

    MagicalSquirrelWeaponType(@NotNull Function<MagicalSquirrelEntity, MagicalSquirrelAttackBehavior> attackBehavior) {
        this.attackBehavior = attackBehavior;
    }
}
