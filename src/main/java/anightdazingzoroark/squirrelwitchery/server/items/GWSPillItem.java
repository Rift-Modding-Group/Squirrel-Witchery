package anightdazingzoroark.squirrelwitchery.server.items;

import anightdazingzoroark.squirrelwitchery.server.effect.SquirrelWitcheryEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class GWSPillItem extends Item {
    public GWSPillItem() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
        ItemStack stack = player.getHeldItem(handIn);

        worldIn.playSound(
                null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS,
                0.5f, worldIn.rand.nextFloat() * 0.1f + 0.9f
        );

        //apply 5 minutes effect and 8 minute cooldown
        if (!worldIn.isRemote) {
            player.addPotionEffect(new PotionEffect(SquirrelWitcheryEffects.GWS, 5 * 60 * 20));
            player.getCooldownTracker().setCooldown(this, 8 * 60 * 20);
        }
        stack.shrink(1);

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }
}