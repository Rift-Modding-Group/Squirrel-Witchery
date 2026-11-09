package anightdazingzoroark.squirrelwitchery.server.entity;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.manager.AnimationDataEntityNonLiving;
import anightdazingzoroark.riftlib.sounds.RiftLibSoundHelper;
import anightdazingzoroark.squirrelwitchery.server.sounds.SquirrelWitcherySounds;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class NutRainEntity extends Entity implements IAnimatable<AnimationDataEntityNonLiving>, IEntityAdditionalSpawnData {
    public static final float RADIUS = 2f;
    public static final float MAX_HEIGHT = 5f;

    @NotNull
    private final AnimationDataEntityNonLiving animationData = new AnimationDataEntityNonLiving(this);
    @Nullable
    private UUID casterId;
    private int remainingTicks = 60;
    private float rainHeight = MAX_HEIGHT;
    private int equipmentDamage = 8;

    public NutRainEntity(@NotNull World world) {
        super(world);
        this.setSize(0.5f, 0.5f);
        this.setNoGravity(true);
    }

    public NutRainEntity(@NotNull World world, @NotNull Vec3d origin, @Nullable UUID casterId, int duration, int equipmentDamage) {
        this(world);
        this.rainHeight = (float) getRainHeight(world, origin.x, origin.y, origin.z);
        this.setPosition(origin.x, origin.y + this.rainHeight, origin.z);
        this.casterId = casterId;
        this.remainingTicks = duration;
        this.equipmentDamage = equipmentDamage;
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.remainingTicks-- <= 0) {
            RiftLibSoundHelper.stopLoopingSound(this, SquirrelWitcherySounds.NUT_RAIN_SOUNDS, SoundCategory.PLAYERS);
            this.world.playSound(
                    null, this.getPosition(), SquirrelWitcherySounds.NUT_RAIN_END, SoundCategory.PLAYERS,
                    1f, 1f
            );
            this.setDead();
            return;
        }

        if (this.world.isRemote) return;

        //respect normal hurt resistance while applying sustained damage twice a second
        if (this.ticksExisted % 10 != 0) return;

        //get caster
        Entity caster = null;
        if (this.casterId != null && this.world instanceof WorldServer serverWorld) {
            caster = serverWorld.getEntityFromUuid(this.casterId);
        }

        //le attack
        DamageSource source = DamageSource.causeIndirectMagicDamage(this, caster);
        AxisAlignedBB rainArea = this.getRainArea();
        for (EntityLivingBase target : this.world.getEntitiesWithinAABB(EntityLivingBase.class, rainArea)) {
            if (target.isDead) continue;

            AxisAlignedBB bounds = target.getEntityBoundingBox();
            double x = Math.clamp(this.posX, bounds.minX + 0.001D, bounds.maxX - 0.001D);
            double z = Math.clamp(this.posZ, bounds.minZ + 0.001D, bounds.maxZ - 0.001D);
            double dx = x - this.posX;
            double dz = z - this.posZ;
            if (dx * dx + dz * dz > RADIUS * RADIUS) continue;
            double height = Math.min(this.rainHeight, getRainHeight(this.world, x, rainArea.minY, z));
            double top = rainArea.minY + height;
            if (height <= 0.1D || bounds.minY >= top) continue;

            //just 1 damage should be fine
            if (!target.attackEntityFrom(source, 1f)) continue;

            //damage armor
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                if (slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) continue;
                target.getItemStackFromSlot(slot).damageItem(this.equipmentDamage, target);
            }

            //damage shield
            ItemStack shield = target.getActiveItemStack();
            if (target.isActiveItemStackBlocking() && shield.getItem().isShield(shield, target)) {
                EnumHand hand = target.getActiveHand();
                ItemStack beforeDamage = shield.copy();
                shield.damageItem(this.equipmentDamage * 2, target);
                if (shield.isEmpty()) {
                    if (target instanceof EntityPlayer player) {
                        ForgeEventFactory.onPlayerDestroyItem(player, beforeDamage, hand);
                    }
                    target.setHeldItem(hand, ItemStack.EMPTY);
                    target.resetActiveHand();
                    target.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.rand.nextFloat() * 0.4F);
                }
            }
        }
    }

    @NotNull
    public AxisAlignedBB getRainArea() {
        return new AxisAlignedBB(
                this.posX - RADIUS, this.posY - this.rainHeight, this.posZ - RADIUS,
                this.posX + RADIUS, this.posY, this.posZ + RADIUS
        );
    }

    @Override
    protected void readEntityFromNBT(@NotNull NBTTagCompound compound) {
        this.remainingTicks = Math.clamp(compound.getInteger("RemainingTicks"), 0, 1200);
        this.rainHeight = Math.clamp(compound.getFloat("RainHeight"), 0f, MAX_HEIGHT);
        this.equipmentDamage = Math.clamp(compound.getInteger("EquipmentDamage"), 1, 100);
        this.casterId = compound.hasUniqueId("Caster") ? compound.getUniqueId("Caster") : null;
    }

    @Override
    protected void writeEntityToNBT(@NotNull NBTTagCompound compound) {
        compound.setInteger("RemainingTicks", this.remainingTicks);
        compound.setFloat("RainHeight", this.rainHeight);
        compound.setInteger("EquipmentDamage", this.equipmentDamage);
        if (this.casterId != null) compound.setUniqueId("Caster", this.casterId);
    }

    @Override
    public void writeSpawnData(@NotNull ByteBuf buffer) {
        buffer.writeInt(this.remainingTicks);
        buffer.writeFloat(this.rainHeight);
    }

    @Override
    public void readSpawnData(@NotNull ByteBuf buffer) {
        this.remainingTicks = buffer.readInt();
        this.rainHeight = Math.clamp(buffer.readFloat(), 0f, MAX_HEIGHT);
    }

    @Override
    @NotNull
    public AnimationDataEntityNonLiving getAnimationData() {
        return this.animationData;
    }

    @Override
    public void initializeAnimationData(@NotNull AnimationDataEntityNonLiving animationData) {
        animationData.addParticleEffect("squirrelwitchery:nut_rain", "base");
    }

    public static double getRainHeight(@NotNull World world, double x, double y, double z) {
        if (!world.getCollisionBoxes(null, new AxisAlignedBB(
                x - 0.001D, y - 0.001D, z - 0.001D, x + 0.001D, y + 0.001D, z + 0.001D
        )).isEmpty()) return 0D;
        double top = Math.min(y + MAX_HEIGHT, world.getHeight() - 0.05D);
        if (top <= y) return 0D;
        RayTraceResult ceiling = world.rayTraceBlocks(new Vec3d(x, y, z), new Vec3d(x, top, z), false, true, false);
        if (ceiling != null) top = ceiling.hitVec.y - 0.05D;
        return Math.max(0D, top - y);
    }
}
