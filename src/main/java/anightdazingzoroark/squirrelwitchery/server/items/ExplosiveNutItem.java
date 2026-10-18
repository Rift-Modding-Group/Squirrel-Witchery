package anightdazingzoroark.squirrelwitchery.server.items;

import anightdazingzoroark.squirrelwitchery.server.entity.ExplosiveNutProjectileEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ExplosiveNutItem extends Item {
    public ExplosiveNutItem() {
        super();
        this.setMaxStackSize(16);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        EntityPlayer player = (EntityPlayer) entityLiving;
        ExplosiveNutProjectileEntity explosiveNut = new ExplosiveNutProjectileEntity(worldIn, player);
        explosiveNut.shoot(player, player.rotationPitch, player.rotationYaw, 0f, 1.6f, 1f);
        if (!worldIn.isRemote) {
            worldIn.spawnEntity(explosiveNut);
            player.getCooldownTracker().setCooldown(this, 20);
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }
}