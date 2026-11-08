package anightdazingzoroark.squirrelwitchery.server.entity;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.manager.AnimationDataEntityNonLiving;
import anightdazingzoroark.riftlib.inventory.RiftLibInventoryHandler;
import anightdazingzoroark.squirrelwitchery.server.inventory.MagicalSquirrelInventory;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import org.jetbrains.annotations.NotNull;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;

import java.util.List;

public class MagicalSquirrelEssenceEntity extends Entity implements IAnimatable<AnimationDataEntityNonLiving>, IEntityAdditionalSpawnData {
    @NotNull
    private final RiftLibInventoryHandler essenceItemSlot = new RiftLibInventoryHandler(1);
    @NotNull
    private final RiftLibInventoryHandler inventory = new RiftLibInventoryHandler(9);
    @NotNull
    private final AnimationDataEntityNonLiving animationData = new AnimationDataEntityNonLiving(this);

    public MagicalSquirrelEssenceEntity(World worldIn) {
        super(worldIn);
        this.setSize(1f, 0.5f);
        this.preventEntitySpawning = true;
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();

        if (this.isDead) return;
        if (!this.world.isRemote) {
            List<EntityPlayer> nearbyPlayers = this.world.getEntitiesWithinAABB(EntityPlayer.class, this.getEntityBoundingBox());
            if (!nearbyPlayers.isEmpty()) {
                //drop inventories
                this.essenceItemSlot.dropAllItems(this.world, this.getPosition());
                this.inventory.dropAllItems(this.world, this.getPosition());

                //kill
                this.setDead();
            }
        }
        //spawn thaumcraft particles in its spot
        else {
            for (int index = 0; index < 2; index++) {
                FXGeneric puff = new FXGeneric(
                        this.world,
                        this.posX + (this.rand.nextDouble() - 0.5D) * this.width,
                        this.posY + this.rand.nextDouble() * this.height,
                        this.posZ + (this.rand.nextDouble() - 0.5D) * this.width,
                        this.rand.nextGaussian() * 0.005D, 0.005D + this.rand.nextDouble() * 0.005D,
                        this.rand.nextGaussian() * 0.005D
                );
                puff.setMaxAge(28 + this.rand.nextInt(16));
                puff.setRBGColorF(0.9843f, 0.5569f, 0.6039f);
                puff.setAlphaF(0f, 0.35f, 0.35f, 0f);
                puff.setGridSize(16);
                puff.setParticles(56 + this.rand.nextInt(4), 1, 1);
                float scale = 3f + this.rand.nextFloat() * 2f;
                puff.setScale(scale * 0.65f, scale, scale * 1.25f);
                puff.setLayer(0);
                puff.setRotationSpeed(this.rand.nextFloat(), (this.rand.nextFloat() - 0.5f) * 0.5f);
                puff.setSlowDown(0.98D);
                puff.setGravity(0f);
                ParticleEngine.addEffect(this.world, puff);
            }
        }
    }

    //should help out w syncin essence from server to client
    @Override
    public void writeSpawnData(@NotNull ByteBuf buffer) {
        ByteBufUtils.writeItemStack(buffer, this.getEssenceItem());
    }

    //same here
    @Override
    public void readSpawnData(@NotNull ByteBuf buffer) {
        this.essenceItemSlot.setStackInSlot(0, ByteBufUtils.readItemStack(buffer));
    }

    @NotNull
    public ItemStack getEssenceItem() {
        return this.essenceItemSlot.getStackInSlot(0);
    }

    public void addEssenceItem(@NotNull ItemStack itemStack) {
        this.essenceItemSlot.insertItem(itemStack);
    }

    public void copyInventoryFromSquirrel(@NotNull MagicalSquirrelInventory magicalSquirrelInventory) {
        for (int x = 0; x < 9; x++) {
            ItemStack stackAtIndex = magicalSquirrelInventory.getStackInSlot(x);
            if (stackAtIndex.isEmpty()) continue;
            this.inventory.insertItem(stackAtIndex);
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setTag("Inventory", this.inventory.serializeNBT());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("Inventory", 10)) this.inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
    }

    @Override
    @NotNull
    public AnimationDataEntityNonLiving getAnimationData() {
        return this.animationData;
    }

    @Override
    public void initializeAnimationData(@NotNull AnimationDataEntityNonLiving animData) {
        animData.addAnimation("animation.magical_squirrel_essence.item_rotate");
    }
}
